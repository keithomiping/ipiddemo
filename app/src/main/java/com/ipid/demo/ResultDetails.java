package com.ipid.demo;

import static com.ipid.demo.constants.Constants.DEFAULT_NOTIFICATION_ID;
import static com.ipid.demo.constants.Constants.EMPTY_FIELD;
import static com.ipid.demo.constants.Constants.NEW_LINE;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ipid.demo.constants.RequestType;
import com.ipid.demo.databinding.FragmentResultDetailsBinding;
import com.ipid.demo.db.AppDatabase;
import com.ipid.demo.db.entity.BankAccount;
import com.ipid.demo.db.entity.Customer;
import com.ipid.demo.db.entity.LookupBank;
import com.ipid.demo.db.entity.Notification;
import com.ipid.demo.db.entity.PaymentDetails;
import com.ipid.demo.utils.NameUtils;

public class ResultDetails extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final String TAG = "ResultDetails";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FragmentResultDetailsBinding binding;

    private int customerId = 0;
    private int notificationId = 0;

    public ResultDetails() {
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
    public static ResultDetails newInstance(String param1, String param2) {
        ResultDetails fragment = new ResultDetails();
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
        inflater.inflate(R.layout.fragment_result_details, container, false);
        binding = FragmentResultDetailsBinding.inflate(inflater,container,false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        TextView textViewName = getView().findViewById(R.id.textViewName);
        TextView textViewPhone = getView().findViewById(R.id.textViewPhone);
        TextView textViewEmail = getView().findViewById(R.id.textViewEmail);
        TextView textViewBank = getView().findViewById(R.id.textViewBank);
        TextView textViewBIC = getView().findViewById(R.id.textViewBIC);
        TextView textViewAccount = getView().findViewById(R.id.textViewAccount);
        TextView textViewAddress = getView().findViewById(R.id.textViewAddress);
        Button buttonCopy = getView().findViewById(R.id.buttonCopy);

        AppDatabase db = AppDatabase.getDbInstance(this.getActivity().getApplicationContext());

        if (getArguments() != null) {
            ResultDetailsArgs args = ResultDetailsArgs.fromBundle(getArguments());
            customerId = args.getCustomerId();
            notificationId = args.getNotificationId();

            Customer customer = db.customerDao().findById(customerId);

            textViewName.setText(NameUtils.getFullName(customer.firstName, customer.lastName));
            textViewPhone.setText(customer.phoneNumber);
            textViewEmail.setText(customer.emailAddress);

            if (customer.address != null) {
                textViewAddress.setText(customer.address);
            } else {
                textViewAddress.setText(EMPTY_FIELD);
            }

            BankAccount bankAccount;
            if (notificationId == DEFAULT_NOTIFICATION_ID) {
                bankAccount = db.bankAccountDao().findPreferredBank(customerId);
            } else {
                Notification notification = db.notificationDao().findById(notificationId);
                PaymentDetails paymentDetails = db.paymentDetailsDao().findByPaymentId(notification.paymentId);

                if (notification.type.equalsIgnoreCase(RequestType.PAY.name())) {
                    bankAccount = db.bankAccountDao().findById(paymentDetails.bankAccountTo);
                } else {
                    bankAccount = db.bankAccountDao().findById(paymentDetails.bankAccountFrom);
                }
            }

            if (bankAccount != null) {
                LookupBank lookupBank = db.lookupBankDao().findById(bankAccount.bankId);

                textViewBank.setText(lookupBank.name);
                textViewBIC.setText(lookupBank.code);
                textViewAccount.setText(bankAccount.accountNumber);
            } else {
                textViewBank.setText(EMPTY_FIELD);
                textViewBIC.setText(EMPTY_FIELD);
                textViewAccount.setText(EMPTY_FIELD);
            }
        }

        buttonCopy.setOnClickListener(v -> {
            Customer customer = db.customerDao().findById(customerId);
            BankAccount bankAccount = db.bankAccountDao().findPreferredBank(customerId);

            ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            StringBuilder sb = new StringBuilder();
            sb.append("Name: " + NameUtils.getFullName(customer.firstName, customer.lastName) + NEW_LINE);
            sb.append("Phone: " + customer.phoneNumber + NEW_LINE);

            if (bankAccount != null) {
                LookupBank lookupBank = db.lookupBankDao().findById(bankAccount.bankId);
                sb.append("Bank: " + lookupBank.name + NEW_LINE);
                sb.append("BIC: " + lookupBank.code + NEW_LINE);
                sb.append("Account Number: " + bankAccount.accountNumber);
            }
            clipboard.setText(sb.toString());

            Toast.makeText(getActivity(), "Banking details copied!", Toast.LENGTH_SHORT).show();
        });
    }
}