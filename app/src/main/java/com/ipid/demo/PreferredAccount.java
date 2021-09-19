package com.ipid.demo;

import static com.ipid.demo.constants.Constants.DEFAULT_NOTIFICATION_ID;
import static com.ipid.demo.constants.Constants.DRAWABLE_TYPE;
import static com.ipid.demo.constants.Constants.EMPTY_BANK_ACCOUNT;
import static com.ipid.demo.constants.Constants.PAY_RECEIVER_UPDATE_DETAILS_NOTIFICATION_PREFIX;
import static com.ipid.demo.constants.Constants.PAY_SENDER_UPDATE_DETAILS_NOTIFICATION_PREFIX;
import static com.ipid.demo.constants.Constants.SPACE;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ipid.demo.adapters.CustomAdapter;
import com.ipid.demo.constants.RequestType;
import com.ipid.demo.custom.CustomListView;
import com.ipid.demo.databinding.FragmentPreferredAccountBinding;
import com.ipid.demo.db.AppDatabase;
import com.ipid.demo.db.entity.BankAccount;
import com.ipid.demo.db.entity.Customer;
import com.ipid.demo.db.entity.LookupBank;
import com.ipid.demo.db.entity.Notification;
import com.ipid.demo.db.entity.PaymentDetails;
import com.ipid.demo.models.BankModel;
import com.ipid.demo.utils.DateUtils;
import com.ipid.demo.utils.NameUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PreferredAccount#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PreferredAccount extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private FragmentPreferredAccountBinding binding;
    private BankModel selectedBank;
    private int preSelectedIndex = -1;
    private int notificationId = 0;

    public PreferredAccount() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PreferredAccount.
     */
    // TODO: Rename and change types and number of parameters
    public static PreferredAccount newInstance(String param1, String param2) {
        PreferredAccount fragment = new PreferredAccount();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        inflater.inflate(R.layout.fragment_preferred_account, container, false);
        binding = FragmentPreferredAccountBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        initArguments();
        initBankListView();
        initAddAccount();
        initListener();
    }

    private void initArguments() {
        if (getArguments() != null) {
            PreferredAccountArgs args = PreferredAccountArgs.fromBundle(getArguments());
            notificationId = args.getNotificationId();
        }
    }

    private boolean validate() {
        if (selectedBank == null) {
            Toast.makeText(getActivity(), EMPTY_BANK_ACCOUNT, Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void initListener() {
        Button buttonConfirm = getView().findViewById(R.id.buttonConfirm);

        buttonConfirm.setOnClickListener(v -> {
            AppDatabase db = AppDatabase.getDbInstance(this.getActivity().getApplicationContext());
            int id = ((MyApplication) this.getActivity().getApplication()).getId();

            if (!validate()) {
                return;
            }

            if (notificationId == DEFAULT_NOTIFICATION_ID) {
                // Update the selected bank account as preferred
                db.bankAccountDao().resetPreferredBankAccounts(id);

                BankAccount bankAccount = db.bankAccountDao().findById(selectedBank.getBankId());
                bankAccount.preferred = true;
                db.bankAccountDao().update(bankAccount);

                PreferredAccountDirections.ActionPreferredAccountFragmentToProfileNavigationFragment action = PreferredAccountDirections.actionPreferredAccountFragmentToProfileNavigationFragment();
                action.setViewMyAccounts(Boolean.TRUE);
                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main).navigate(action);

            } else {
                // Update the preferred bank account
                // Update the payment transaction preferred bank account (bank account to)
                db.bankAccountDao().resetPreferredBankAccounts(id);

                BankAccount bankAccount = db.bankAccountDao().findById(selectedBank.getBankId());
                bankAccount.preferred = true;
                db.bankAccountDao().update(bankAccount);

                Notification notification = db.notificationDao().findById(notificationId);
                PaymentDetails paymentDetails = db.paymentDetailsDao().findByPaymentId(notification.paymentId);
                Customer customerFrom = db.customerDao().findById(id);
                Customer customerTo = db.customerDao().findById(notification.customerFrom);

                notification.pending = false;
                db.notificationDao().update(notification);

                paymentDetails.bankAccountTo = bankAccount.id;
                db.paymentDetailsDao().update(paymentDetails);

                // Save notification
                // Receiver notification
                Notification newNotification = new Notification();
                newNotification.customerFrom = id;
                newNotification.customerTo = notification.customerFrom;    // Send to payee after updating banking details
                newNotification.paymentId = notification.paymentId;
                newNotification.description = NameUtils.getFullName(customerFrom.firstName, customerFrom.lastName) + PAY_RECEIVER_UPDATE_DETAILS_NOTIFICATION_PREFIX;
                newNotification.type = RequestType.PAY.name();
                newNotification.status = true;
                newNotification.pending = false;
                newNotification.resent = false;
                newNotification.createdDate = DateUtils.getDateTime();
                db.notificationDao().insertNotification(newNotification);

                // Sender notification
                newNotification = new Notification();
                newNotification.customerFrom = id;
                newNotification.customerTo = id;
                newNotification.paymentId = notification.paymentId;
                newNotification.description = PAY_SENDER_UPDATE_DETAILS_NOTIFICATION_PREFIX + SPACE + NameUtils.getFullName(customerTo.firstName, customerTo.lastName) + " can now proceed with their payment.";
                newNotification.type = RequestType.PAY.name();
                newNotification.status = true;
                newNotification.pending = false;
                newNotification.resent = false;
                newNotification.createdDate = DateUtils.getDateTime();
                db.notificationDao().insertNotification(newNotification);

                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main).navigate(R.id.action_preferredAccountFragment_to_homeFragment);
            }
        });
    }

    private void initBankListView() {
        CustomListView listView = getView().findViewById(R.id.listView);

        AppDatabase db = AppDatabase.getDbInstance(this.getActivity().getApplicationContext());
        int id = ((MyApplication) this.getActivity().getApplication()).getId();

        List<BankAccount> bankAccounts = db.bankAccountDao().findByCustomerId(id);
        List<BankModel> bankModelList = new ArrayList();
        for (BankAccount bankAccount : bankAccounts) {
            LookupBank lookupBank = db.lookupBankDao().findById(bankAccount.bankId);

            int drawableCountry = getResources().getIdentifier(NameUtils.getDrawableFlagName(bankAccount.country.toLowerCase()), DRAWABLE_TYPE, getContext().getPackageName());
            bankModelList.add(new BankModel(bankAccount.preferred, bankAccount.id, lookupBank.name, bankAccount.accountNumber, drawableCountry, true, bankAccount.preferred));
        }

        CustomAdapter adapter = new CustomAdapter(this.getActivity(), bankModelList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id1) -> {
            BankModel bankModel = bankModelList.get(position);
            bankModel.setSelected(true);

            selectedBank = bankModel;
            bankModelList.set(position, bankModel);

            if (preSelectedIndex > -1) {
                BankModel preRecord = bankModelList.get(preSelectedIndex);
                preRecord.setSelected(false);
                bankModelList.set(preSelectedIndex, preRecord);
            } else {
                // Initially selected preferred account, unselect
                BankModel preRecord = bankModelList.get(0);
                preRecord.setSelected(false);
                bankModelList.set(0, preRecord);
            }

            preSelectedIndex = position;
            adapter.updateRecords(bankModelList);
        });

        if (bankModelList.size() > 0) {
            // Initialize selected bank to be the preferred bank
            selectedBank = bankModelList.get(0);
        }
    }

    private void initAddAccount() {
        TextView textViewAddAccount = getView().findViewById(R.id.textViewAddAccount);

        textViewAddAccount.setOnClickListener(v -> {
            PreferredAccountDirections.ActionPreferredAccountFragmentToProfileNavigationFragment action = PreferredAccountDirections.actionPreferredAccountFragmentToProfileNavigationFragment();
            action.setAccountSetup(false);
            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main).navigate(action);
        });
    }
}