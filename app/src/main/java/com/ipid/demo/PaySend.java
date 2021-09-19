package com.ipid.demo;

import static com.ipid.demo.constants.Constants.DEFAULT_BANK_ACCOUNT_ID;
import static com.ipid.demo.constants.Constants.DRAWABLE_TYPE;
import static com.ipid.demo.constants.Constants.INVALID_BANK_ACCOUNT;
import static com.ipid.demo.constants.Constants.PERIOD;
import static com.ipid.demo.constants.Constants.REQUEST_TO_PAY_RECEIVER_PREFIX;
import static com.ipid.demo.constants.Constants.REQUEST_TO_PAY_SENDER_PREFIX;

import android.app.Dialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.ipid.demo.adapters.CustomAdapter;
import com.ipid.demo.constants.RequestType;
import com.ipid.demo.databinding.FragmentPaySendBinding;
import com.ipid.demo.db.AppDatabase;
import com.ipid.demo.db.entity.BankAccount;
import com.ipid.demo.db.entity.Customer;
import com.ipid.demo.db.entity.LookupBank;
import com.ipid.demo.db.entity.LookupCurrency;
import com.ipid.demo.db.entity.Notification;
import com.ipid.demo.db.entity.Payment;
import com.ipid.demo.db.entity.PaymentDetails;
import com.ipid.demo.models.BankModel;
import com.ipid.demo.services.impl.ValidationServiceImpl;
import com.ipid.demo.utils.DateUtils;
import com.ipid.demo.utils.NameUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple z{@link Fragment} subclass.
 * Use the {@link PaySend#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PaySend extends Fragment {

    private static final String TAG = "PaySendFragment";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private ValidationServiceImpl validationService;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Dialog dialog;
    private FragmentPaySendBinding binding;
    private BankModel selectedBank;
    private int preSelectedIndex = -1;

    // Arguments passed from previous screen, will be saved on the final screen
    private int customerId;
    private String amountFrom;
    private String amountTo;
    private String currencyFrom;
    private String currencyTo;
    private String countryFrom;
    private String countryTo;
    private String exchangeRate;
    private String remarks;

    public PaySend() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileAliases.
     */
    // TODO: Rename and change types and number of parameters
    public static PaySend newInstance(String param1, String param2) {
        PaySend fragment = new PaySend();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        validationService = new ValidationServiceImpl();

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        inflater.inflate(R.layout.fragment_pay_send, container, false);
        binding = FragmentPaySendBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        dialog = new Dialog(getActivity());

        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        if (getArguments() != null) {
            ResultArgs resultArgs = ResultArgs.fromBundle(getArguments());
            customerId = resultArgs.getId();
            amountFrom = resultArgs.getAmountFrom();
            amountTo = resultArgs.getAmountTo();
            currencyFrom = resultArgs.getCurrencyFrom();
            currencyTo = resultArgs.getCurrencyTo();
            countryFrom = resultArgs.getCountryFrom();
            countryTo = resultArgs.getCountryTo();
            exchangeRate = resultArgs.getExchangeRate();
            remarks = resultArgs.getRemarks();

            Log.i(TAG, "onViewCreated: " + customerId);
            Log.i(TAG, "onViewCreated: " + amountFrom);
            Log.i(TAG, "onViewCreated: " + amountTo);
            Log.i(TAG, "onViewCreated: " + currencyFrom);
            Log.i(TAG, "onViewCreated: " + currencyTo);
            Log.i(TAG, "onViewCreated: " + countryFrom);
            Log.i(TAG, "onViewCreated: " + countryTo);
            Log.i(TAG, "onViewCreated: " + exchangeRate);
            Log.i(TAG, "onViewCreated: " + remarks);
        }

        initBankListView();
        initSendRequest();
    }

    private void initBankListView() {
        ListView listView = binding.listView;
        AppDatabase db = AppDatabase.getDbInstance(this.getActivity().getApplicationContext());
        int id = ((MyApplication) this.getActivity().getApplication()).getId();

        List<BankAccount> bankAccounts = db.bankAccountDao().findByCustomerId(id);
        List<BankModel> bankModelList = new ArrayList();
        for (BankAccount bankAccount : bankAccounts) {
            LookupBank lookupBank = db.lookupBankDao().findById(bankAccount.bankId);

            int drawableCountry = getResources().getIdentifier(NameUtils.getDrawableFlagName(bankAccount.country.toLowerCase()), DRAWABLE_TYPE, getContext().getPackageName());
            bankModelList.add(new BankModel(bankAccount.preferred, bankAccount.id, lookupBank.name, bankAccount.accountNumber, drawableCountry, false, bankAccount.preferred));
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

        // Initialize selected bank to be the preferred bank
        selectedBank = bankModelList.get(0);
    }

    private void initSendRequest() {
        Button sendRequestLayout = getView().findViewById(R.id.buttonSendRequest);
        sendRequestLayout.setOnClickListener(v -> {
            AppDatabase db = AppDatabase.getDbInstance(this.getActivity().getApplicationContext());
            int id = ((MyApplication) this.getActivity().getApplication()).getId();

            if (!validate()) {
                return;
            }

            Customer customerFrom = db.customerDao().findById(id);
            Customer customerTo = db.customerDao().findById(customerId);

            // Save payment
            Payment payment = new Payment();
            payment.customerFrom = id;
            payment.customerTo = customerTo.id;
            payment.remarks = remarks;
            payment.status = true;
            payment.createdDate = DateUtils.getDateTime();
            db.paymentDao().insertPayment(payment);

            payment = db.paymentDao().findLatestPaymentByCustomer(id);
            LookupCurrency lookupCurrencyFrom = db.lookupCurrencyDao().findByCurrency(currencyFrom);
            LookupCurrency lookupCurrencyTo = db.lookupCurrencyDao().findByCurrency(currencyTo);

            // Save payment details
            PaymentDetails paymentDetails = new PaymentDetails();
            paymentDetails.paymentId = payment.id;
            paymentDetails.bankAccountFrom = selectedBank.getBankId();
            paymentDetails.bankAccountTo = DEFAULT_BANK_ACCOUNT_ID;
            paymentDetails.currencyFrom = lookupCurrencyFrom.id;
            paymentDetails.currencyTo = lookupCurrencyTo.id;
            paymentDetails.amountFrom = Double.parseDouble(amountFrom);
            paymentDetails.amountTo = Double.parseDouble(amountTo);
            paymentDetails.exchangeRate = Double.parseDouble(exchangeRate);
            db.paymentDetailsDao().insertPaymentDetails(paymentDetails);

            // Save notification
            // Receiver notification
            Notification notification = new Notification();
            notification.customerFrom = id;
            notification.customerTo = customerTo.id;
            notification.paymentId = payment.id;
            notification.description = NameUtils.getFullName(customerFrom.firstName, customerFrom.lastName) + REQUEST_TO_PAY_SENDER_PREFIX;
            notification.type = RequestType.GET_PAID.name();
            notification.status = true;
            notification.pending = true;
            notification.resent = false;
            notification.createdDate = DateUtils.getDateTime();
            db.notificationDao().insertNotification(notification);

            // Sender notification
            notification = new Notification();
            notification.customerFrom = id;
            notification.customerTo = id;
            notification.paymentId = payment.id;
            notification.description = REQUEST_TO_PAY_RECEIVER_PREFIX + NameUtils.getFullName(customerTo.firstName, customerTo.lastName) + PERIOD;
            notification.type = RequestType.GET_PAID.name();
            notification.status = true;
            notification.pending = true;
            notification.resent = false;
            notification.createdDate = DateUtils.getDateTime();
            db.notificationDao().insertNotification(notification);

            showDialog();
        });
    }

    private void showDialog() {
        dialog.setContentView(R.layout.popup_sucess);
        ImageView closeBtn = dialog.findViewById(R.id.closePopupImg);
        Button btnShare = dialog.findViewById(R.id.btnShare);

        AppDatabase db = AppDatabase.getDbInstance(this.getActivity().getApplicationContext());
        int id = ((MyApplication) this.getActivity().getApplication()).getId();

        btnShare.setOnClickListener(v -> {
            Customer customerFrom = db.customerDao().findById(id);
            ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(NameUtils.getFullName(customerFrom.firstName, customerFrom.lastName) + " has requested you to pay " + amountFrom + ". Please log in to iPiD.");

            Toast.makeText(getActivity(), "Link copied!", Toast.LENGTH_SHORT).show();
        });

        closeBtn.setOnClickListener(v -> {
            dialog.dismiss();
            // Redirect to home page
            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main).navigate(R.id.action_paySendFragment_to_HomeFragment);
        });

        dialog.setOnCancelListener(
            dialog -> {
                // When you touch outside of dialog bounds,
                // The dialog gets canceled and this method executes.
                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main).navigate(R.id.action_paySendFragment_to_HomeFragment);
            }
        );

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private boolean validate() {
        if (selectedBank == null) {
            Toast.makeText(getActivity(), INVALID_BANK_ACCOUNT, Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}