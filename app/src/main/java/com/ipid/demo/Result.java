package com.ipid.demo;

import static com.ipid.demo.constants.Constants.DEFAULT_BANK_ACCOUNT_ID;
import static com.ipid.demo.constants.Constants.DEFAULT_CURRENCY_AUD;
import static com.ipid.demo.constants.Constants.DEFAULT_CURRENCY_SGD;
import static com.ipid.demo.constants.Constants.DEFAULT_NOTIFICATION_ID;
import static com.ipid.demo.constants.Constants.DEFAULT_SELECTED_CURRENCY;
import static com.ipid.demo.constants.Constants.DEFAULT_SELECTED_CURRENCY_SELECTED;
import static com.ipid.demo.constants.Constants.DRAWABLE_TYPE;
import static com.ipid.demo.constants.Constants.EMPTY_STRING;
import static com.ipid.demo.constants.Constants.FINALIZE_PAY_TITLE;
import static com.ipid.demo.constants.Constants.GET_PAID_RECEIVER_NOTIFICATION_PREFIX;
import static com.ipid.demo.constants.Constants.INITIAL_VALUE;
import static com.ipid.demo.constants.Constants.MARKET_EXCHANGE_PREFIX;
import static com.ipid.demo.constants.Constants.PAY_RECEIVER_NOTIFICATION_PREFIX;
import static com.ipid.demo.constants.Constants.PAY_SENDER_ASK_DETAILS_NOTIFICATION_PREFIX;
import static com.ipid.demo.constants.Constants.PAY_SENDER_NOTIFICATION_PREFIX;
import static com.ipid.demo.constants.Constants.PERIOD;
import static com.ipid.demo.constants.Constants.REAL_FORMATTER;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.ipid.demo.adapters.CurrencyAdapter;
import com.ipid.demo.constants.RequestType;
import com.ipid.demo.databinding.FragmentResultBinding;
import com.ipid.demo.db.AppDatabase;
import com.ipid.demo.db.entity.BankAccount;
import com.ipid.demo.db.entity.CurrencyConversion;
import com.ipid.demo.db.entity.Customer;
import com.ipid.demo.db.entity.LookupCurrency;
import com.ipid.demo.db.entity.Notification;
import com.ipid.demo.db.entity.Payment;
import com.ipid.demo.db.entity.PaymentDetails;
import com.ipid.demo.models.CurrencyModel;
import com.ipid.demo.utils.DateUtils;
import com.ipid.demo.utils.NameUtils;

import java.util.ArrayList;
import java.util.List;

public class Result extends Fragment {

    private static final String TAG = "ResultFragment";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FragmentResultBinding binding;

    // Arguments passed from previous screen, will be saved on the final screen
    private int customerId;
    private int notificationId;
    private String amountFrom;
    private String amountTo;
    private String currencyFrom;
    private String currencyTo;
    private String countryFrom;
    private String countryTo;
    private String exchangeRate;
    private String remarks;

    public Result() {
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
    public static Result newInstance(String param1, String param2) {
        Result fragment = new Result();
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
        inflater.inflate(R.layout.fragment_result, container, false);
        binding = FragmentResultBinding.inflate(inflater,container,false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        initCurrencySpinnerFrom();
        initCurrencySpinnerTo();
        initConversion();
        initCurrencyFromTextChange();
        initCurrencyToTextChange();
        initData();
        initButtonListeners();
    }

    private void initCurrencySpinnerFrom() {
        Spinner spinnerCurrenciesFrom = getView().findViewById(R.id.spinnerCompareFrom);
        Spinner spinnerCurrenciesTo = getView().findViewById(R.id.spinnerCompareTo);
        TextView textViewCurrencyFrom = getView().findViewById(R.id.textViewCurrencyFromLabel);
        TextView textViewCurrencyTo = getView().findViewById(R.id.textViewCurrencyToLabel);
        EditText editTextCurrencyFrom = getView().findViewById(R.id.editTextCurrencyFrom);
        EditText editTextCurrencyTo = getView().findViewById(R.id.editTextCurrencyTo);

        AppDatabase db = AppDatabase.getDbInstance(this.getActivity().getApplicationContext());
        List<LookupCurrency> currencies = db.lookupCurrencyDao().getAllCurrencies();

        List<CurrencyModel> currencyList = new ArrayList();
        for (LookupCurrency currency : currencies) {
            int id = getResources().getIdentifier(NameUtils.getDrawableFlagName(currency.country.toLowerCase()), DRAWABLE_TYPE, getContext().getPackageName());
            currencyList.add(new CurrencyModel(currency.country, id, currency.description));
        }

        CurrencyAdapter currencyAdapter = new CurrencyAdapter(getContext(), currencyList);
        spinnerCurrenciesFrom.setAdapter(currencyAdapter);

        spinnerCurrenciesFrom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (editTextCurrencyFrom.getText().toString().length() > 0 && editTextCurrencyTo.getText().toString().length() > 0) {
                    CurrencyModel selectedCurrencyFrom = (CurrencyModel) spinnerCurrenciesFrom.getSelectedItem();
                    CurrencyModel selectedCurrencyTo = (CurrencyModel) spinnerCurrenciesTo.getSelectedItem();

                    if (selectedCurrencyFrom.getCurrency().equalsIgnoreCase(selectedCurrencyTo.getCurrency())) {
                        textViewCurrencyFrom.setText(selectedCurrencyFrom.getCurrency());

                        if (!selectedCurrencyFrom.getCurrency().equalsIgnoreCase(DEFAULT_CURRENCY_SGD)) {
                            spinnerCurrenciesTo.setSelection(DEFAULT_SELECTED_CURRENCY); // SGD
                            textViewCurrencyTo.setText(DEFAULT_CURRENCY_SGD);
                        } else {
                            spinnerCurrenciesTo.setSelection(DEFAULT_SELECTED_CURRENCY_SELECTED); // AUD
                            textViewCurrencyTo.setText(DEFAULT_CURRENCY_AUD);
                        }

                        LookupCurrency currencyFrom = db.lookupCurrencyDao().findByCurrency(textViewCurrencyFrom.getText().toString());
                        LookupCurrency currencyTo = db.lookupCurrencyDao().findByCurrency(textViewCurrencyTo.getText().toString());

                        CurrencyConversion currencyConversion = db.currencyConversionDao().findByCurrencyFromTo(currencyFrom.id, currencyTo.id);

                        double convertedTo = Double.parseDouble(editTextCurrencyFrom.getText().toString()) * currencyConversion.value;
                        editTextCurrencyTo.setText(REAL_FORMATTER.format(convertedTo));

                    } else {
                        CurrencyModel selectedItem = (CurrencyModel) spinnerCurrenciesFrom.getSelectedItem();

                        textViewCurrencyFrom.setText(selectedItem.getCurrency());

                        LookupCurrency currencyFrom = db.lookupCurrencyDao().findByCurrency(selectedItem.getCurrency());
                        LookupCurrency currencyTo = db.lookupCurrencyDao().findByCurrency(selectedCurrencyTo.getCurrency());

                        CurrencyConversion currencyConversion = db.currencyConversionDao().findByCurrencyFromTo(currencyFrom.id, currencyTo.id);

                        double convertedTo = Double.parseDouble(editTextCurrencyFrom.getText().toString()) * currencyConversion.value;
                        editTextCurrencyTo.setText(REAL_FORMATTER.format(convertedTo));
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        CurrencyModel currencyFrom = (CurrencyModel) spinnerCurrenciesFrom.getSelectedItem();
        textViewCurrencyFrom.setText(currencyFrom.getCurrency());
    }

    private void initCurrencySpinnerTo() {
        Spinner spinnerCurrenciesFrom = getView().findViewById(R.id.spinnerCompareFrom);
        Spinner spinnerCurrenciesTo = getView().findViewById(R.id.spinnerCompareTo);
        TextView textViewCurrencyFrom = getView().findViewById(R.id.textViewCurrencyFromLabel);
        TextView textViewCurrencyTo = getView().findViewById(R.id.textViewCurrencyToLabel);
        EditText editTextCurrencyFrom = getView().findViewById(R.id.editTextCurrencyFrom);
        EditText editTextCurrencyTo = getView().findViewById(R.id.editTextCurrencyTo);

        AppDatabase db = AppDatabase.getDbInstance(this.getActivity().getApplicationContext());
        List<LookupCurrency> currencies = db.lookupCurrencyDao().getAllCurrencies();

        List<CurrencyModel> currencyList = new ArrayList();
        for (LookupCurrency currency : currencies) {
            int id = getResources().getIdentifier(NameUtils.getDrawableFlagName(currency.country.toLowerCase()), DRAWABLE_TYPE, getContext().getPackageName());
            currencyList.add(new CurrencyModel(currency.country, id, currency.description));
        }

        CurrencyAdapter currencyAdapter = new CurrencyAdapter(getContext(), currencyList);
        spinnerCurrenciesTo.setAdapter(currencyAdapter);
        spinnerCurrenciesTo.setSelection(DEFAULT_SELECTED_CURRENCY);

        spinnerCurrenciesTo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (editTextCurrencyFrom.getText().toString().length() > 0 && editTextCurrencyTo.getText().toString().length() > 0) {
                    CurrencyModel selectedCurrencyFrom = (CurrencyModel) spinnerCurrenciesFrom.getSelectedItem();
                    CurrencyModel selectedCurrencyTo = (CurrencyModel) spinnerCurrenciesTo.getSelectedItem();

                    if (selectedCurrencyFrom.getCurrency().equalsIgnoreCase(selectedCurrencyTo.getCurrency())) {
                        textViewCurrencyTo.setText(selectedCurrencyTo.getCurrency());

                        if (!selectedCurrencyTo.getCurrency().equalsIgnoreCase(DEFAULT_CURRENCY_SGD)) {
                            spinnerCurrenciesFrom.setSelection(DEFAULT_SELECTED_CURRENCY); // SGD
                            textViewCurrencyFrom.setText(DEFAULT_CURRENCY_SGD);
                        } else {
                            spinnerCurrenciesFrom.setSelection(DEFAULT_SELECTED_CURRENCY_SELECTED); // AUD
                            textViewCurrencyFrom.setText(DEFAULT_CURRENCY_AUD);
                        }

                        LookupCurrency currencyFrom = db.lookupCurrencyDao().findByCurrency(textViewCurrencyFrom.getText().toString());
                        LookupCurrency currencyTo = db.lookupCurrencyDao().findByCurrency(textViewCurrencyTo.getText().toString());

                        CurrencyConversion currencyConversion = db.currencyConversionDao().findByCurrencyFromTo(currencyTo.id, currencyFrom.id);

                        double convertedTo = Double.parseDouble(editTextCurrencyTo.getText().toString()) * currencyConversion.value;
                        editTextCurrencyFrom.setText(REAL_FORMATTER.format(convertedTo));

                    } else {
                        CurrencyModel selectedItem = (CurrencyModel) spinnerCurrenciesTo.getSelectedItem();

                        textViewCurrencyTo.setText(selectedItem.getCurrency());

                        LookupCurrency currencyFrom = db.lookupCurrencyDao().findByCurrency(selectedCurrencyFrom.getCurrency());
                        LookupCurrency currencyTo = db.lookupCurrencyDao().findByCurrency(selectedItem.getCurrency());

                        CurrencyConversion currencyConversion = db.currencyConversionDao().findByCurrencyFromTo(currencyTo.id, currencyFrom.id);

                        double convertedTo = Double.parseDouble(editTextCurrencyTo.getText().toString()) * currencyConversion.value;
                        editTextCurrencyFrom.setText(REAL_FORMATTER.format(convertedTo));
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        CurrencyModel currencyTo = (CurrencyModel) spinnerCurrenciesTo.getSelectedItem();
        textViewCurrencyTo.setText(currencyTo.getCurrency());
    }

    private void initConversion() {
        TextView textViewCurrencyFrom = getView().findViewById(R.id.textViewCurrencyFromLabel);
        TextView textViewCurrencyTo = getView().findViewById(R.id.textViewCurrencyToLabel);
        EditText editTextCurrencyFrom = getView().findViewById(R.id.editTextCurrencyFrom);
        EditText editTextCurrencyTo = getView().findViewById(R.id.editTextCurrencyTo);

        AppDatabase db = AppDatabase.getDbInstance(this.getActivity().getApplicationContext());
        LookupCurrency currencyFrom = db.lookupCurrencyDao().findByCurrency(textViewCurrencyFrom.getText().toString());
        LookupCurrency currencyTo = db.lookupCurrencyDao().findByCurrency(textViewCurrencyTo.getText().toString());
        CurrencyConversion currencyConversion = db.currencyConversionDao().findByCurrencyFromTo(currencyFrom.id, currencyTo.id);

        double convertedTo = INITIAL_VALUE * currencyConversion.value;
        editTextCurrencyFrom.setText(REAL_FORMATTER.format(INITIAL_VALUE));
        editTextCurrencyTo.setText(REAL_FORMATTER.format(convertedTo));
    }

    private void initCurrencyFromTextChange() {
        TextView textViewCurrencyFrom = getView().findViewById(R.id.textViewCurrencyFromLabel);
        TextView textViewCurrencyTo = getView().findViewById(R.id.textViewCurrencyToLabel);
        EditText editTextCurrencyFrom = getView().findViewById(R.id.editTextCurrencyFrom);
        EditText editTextCurrencyTo = getView().findViewById(R.id.editTextCurrencyTo);

        editTextCurrencyFrom.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (editTextCurrencyFrom.hasFocus()) {
                    if (s.toString().length() == 0) {
                        editTextCurrencyTo.setText(EMPTY_STRING);
                    } else {
                        AppDatabase db = AppDatabase.getDbInstance(getActivity().getApplicationContext());
                        LookupCurrency currencyFrom = db.lookupCurrencyDao().findByCurrency(textViewCurrencyFrom.getText().toString());
                        LookupCurrency currencyTo = db.lookupCurrencyDao().findByCurrency(textViewCurrencyTo.getText().toString());
                        CurrencyConversion currencyConversion = db.currencyConversionDao().findByCurrencyFromTo(currencyFrom.id, currencyTo.id);

                        double convertedTo = Double.parseDouble(s.toString()) * currencyConversion.value;
                        editTextCurrencyTo.setText(REAL_FORMATTER.format(convertedTo));
                    }
                }
            }
        });
    }

    private void initCurrencyToTextChange() {
        TextView textViewCurrencyFrom = getView().findViewById(R.id.textViewCurrencyFromLabel);
        TextView textViewCurrencyTo = getView().findViewById(R.id.textViewCurrencyToLabel);
        EditText editTextCurrencyFrom = getView().findViewById(R.id.editTextCurrencyFrom);
        EditText editTextCurrencyTo = getView().findViewById(R.id.editTextCurrencyTo);

        editTextCurrencyTo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (editTextCurrencyTo.hasFocus()) {
                    if (s.toString().length() == 0) {
                        editTextCurrencyFrom.setText(EMPTY_STRING);
                    } else {
                        AppDatabase db = AppDatabase.getDbInstance(getActivity().getApplicationContext());
                        LookupCurrency currencyFrom = db.lookupCurrencyDao().findByCurrency(textViewCurrencyFrom.getText().toString());
                        LookupCurrency currencyTo = db.lookupCurrencyDao().findByCurrency(textViewCurrencyTo.getText().toString());
                        CurrencyConversion currencyConversion = db.currencyConversionDao().findByCurrencyFromTo(currencyFrom.id, currencyTo.id);

                        double convertedTo = Double.parseDouble(s.toString()) / currencyConversion.value;
                        editTextCurrencyFrom.setText(REAL_FORMATTER.format(convertedTo));
                    }
                }
            }
        });
    }

    private void initData() {
        EditText editTextCurrencyFrom = getView().findViewById(R.id.editTextCurrencyFrom);
        TextView textViewCurrencyFrom = getView().findViewById(R.id.textViewCurrencyFromLabel);
        TextView textViewDate = getView().findViewById(R.id.textViewDate);

        AppDatabase db = AppDatabase.getDbInstance(this.getActivity().getApplicationContext());

        if (getArguments() != null) {
            ResultArgs resultArgs = ResultArgs.fromBundle(getArguments());
            customerId = resultArgs.getId();
            notificationId = resultArgs.getNotificationId();
            amountFrom = resultArgs.getAmountFrom();
            amountTo = resultArgs.getAmountTo();
            currencyFrom = resultArgs.getCurrencyFrom();
            currencyTo = resultArgs.getCurrencyTo();
            countryFrom = resultArgs.getCountryFrom();
            countryTo = resultArgs.getCountryTo();
            exchangeRate = resultArgs.getExchangeRate();
            remarks = resultArgs.getRemarks();

            Spinner spinnerCurrenciesFrom = getView().findViewById(R.id.spinnerCompareFrom);
            spinnerCurrenciesFrom.setSelection(getIndex(spinnerCurrenciesFrom, countryFrom));

            LookupCurrency lookupCurrency = db.lookupCurrencyDao().findByCountry(countryFrom);
            textViewCurrencyFrom.setText(lookupCurrency.description);

            editTextCurrencyFrom.setText(amountFrom);
            textViewDate.setText(MARKET_EXCHANGE_PREFIX + DateUtils.getFormattedDate(DateUtils.getDateTime()));
        }
    }

    private int getIndex(Spinner spinner, String myString){
        for (int i = 0; i < spinner.getCount(); i++){
            CurrencyModel currencyModel = (CurrencyModel) spinner.getItemAtPosition(i);
            if (currencyModel.getCountryName().equalsIgnoreCase(myString)){
                return i;
            }
        }

        return 0;
    }

    private void initButtonListeners() {
        Button btnDigiPay = binding.btnDigiPay;
        btnDigiPay.setOnClickListener((v -> {
            processTransaction();
            ResultDirections.ActionResultFragmentToTransitPaymentFragment action = ResultDirections.actionResultFragmentToTransitPaymentFragment();
            action.setMessage(FINALIZE_PAY_TITLE);
            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main).navigate(action);
        }));

        Button btnDBS = binding.btnDBS;
        btnDBS.setOnClickListener((v -> {
            processTransaction();
            ResultDirections.ActionResultFragmentToTransitPaymentFragment action = ResultDirections.actionResultFragmentToTransitPaymentFragment();
            action.setMessage(FINALIZE_PAY_TITLE);
            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main).navigate(action);
        }));

        Button btnWise = binding.btnWise;
        btnWise.setOnClickListener((v -> {
            processTransaction();
            ResultDirections.ActionResultFragmentToTransitPaymentFragment action = ResultDirections.actionResultFragmentToTransitPaymentFragment();
            action.setMessage(FINALIZE_PAY_TITLE);
            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main).navigate(action);
        }));

        Button btnViewDetails = binding.btnViewDetails;
        btnViewDetails.setOnClickListener((v -> {
            // Recipient's banking details
            ResultDirections.ActionResultFragmentToResultDetailsFragment action = ResultDirections.actionResultFragmentToResultDetailsFragment();
            action.setCustomerId(customerId);
            action.setNotificationId(notificationId);
            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main).navigate(action);
        }));
    }

    private void processTransaction() {
        if (notificationId == DEFAULT_NOTIFICATION_ID) {
            // Save transaction if it is a normal pay workflow
            saveTransaction();
        } else {
            AppDatabase db = AppDatabase.getDbInstance(this.getActivity().getApplicationContext());
            Notification notification = db.notificationDao().findById(notificationId);

            // Update the status of notification
            // Get paid step 3 (receiver clicking Proceed)
            // Requestee paying to requester
            processPendingTransaction(notification);
        }
    }

    private void setToCompleteNotifications(int paymentId) {
        // Set pending = false for completed transactions
        AppDatabase db = AppDatabase.getDbInstance(this.getActivity().getApplicationContext());

        List<Notification> notifications = db.notificationDao().findByPaymentId(paymentId);
        for (Notification notification : notifications) {
            notification.pending = false;
            db.notificationDao().update(notification);
        }
    }

    private void processPendingTransaction(Notification notification) {
        EditText editTextCurrencyFrom = getView().findViewById(R.id.editTextCurrencyFrom);
        EditText editTextCurrencyTo = getView().findViewById(R.id.editTextCurrencyTo);
        TextView textViewCurrencyFromLabel = getView().findViewById(R.id.textViewCurrencyFromLabel);
        TextView textViewCurrencyToLabel = getView().findViewById(R.id.textViewCurrencyToLabel);

        AppDatabase db = AppDatabase.getDbInstance(this.getActivity().getApplicationContext());
        int id = ((MyApplication) this.getActivity().getApplication()).getId();

        setToCompleteNotifications(notification.paymentId);

        Customer customerFrom = db.customerDao().findById(id);
        Customer customerTo = db.customerDao().findById(customerId);    // Requester
        BankAccount preferredBankAccountFrom = db.bankAccountDao().findPreferredBank(id);
        BankAccount preferredBankAccountTo = db.bankAccountDao().findPreferredBank(customerId);
        LookupCurrency lookupCurrencyFrom = db.lookupCurrencyDao().findByCurrency(textViewCurrencyFromLabel.getText().toString());
        LookupCurrency lookupCurrencyTo = db.lookupCurrencyDao().findByCurrency(textViewCurrencyToLabel.getText().toString());
        CurrencyConversion currencyConversion = db.currencyConversionDao().findByCurrencyFromTo(lookupCurrencyFrom.id, lookupCurrencyTo.id);

        // Save payment
        Payment payment = new Payment();
        payment.customerFrom = id;
        payment.customerTo = customerTo.id;
        payment.remarks = remarks;
        payment.status = true;
        payment.createdDate = DateUtils.getDateTime();
        db.paymentDao().insertPayment(payment);
        payment = db.paymentDao().findLatestPaymentByCustomer(id);

        // Save payment details
        PaymentDetails paymentDetails = new PaymentDetails();
        paymentDetails.paymentId = payment.id;
        paymentDetails.bankAccountFrom = preferredBankAccountFrom == null ? DEFAULT_BANK_ACCOUNT_ID : preferredBankAccountFrom.id;
        paymentDetails.bankAccountTo = preferredBankAccountTo == null ? DEFAULT_BANK_ACCOUNT_ID : preferredBankAccountTo.id;
        paymentDetails.currencyFrom = lookupCurrencyFrom.id;
        paymentDetails.currencyTo = lookupCurrencyTo.id;
        paymentDetails.amountFrom = Double.parseDouble(editTextCurrencyFrom.getText().toString());
        paymentDetails.amountTo = Double.parseDouble(editTextCurrencyTo.getText().toString());
        paymentDetails.exchangeRate = Double.parseDouble(REAL_FORMATTER.format(currencyConversion.value));
        db.paymentDetailsDao().insertPaymentDetails(paymentDetails);

        // Save notification
        // Receiver notification
        notification = new Notification();
        notification.customerFrom = id;
        notification.customerTo = customerTo.id;
        notification.paymentId = payment.id;
        notification.description = NameUtils.getFullName(customerFrom.firstName, customerFrom.lastName) + GET_PAID_RECEIVER_NOTIFICATION_PREFIX;
        notification.type = RequestType.PAY.name();
        notification.status = true;
        notification.pending = false;
        notification.resent = false;
        notification.createdDate = DateUtils.getDateTime();
        db.notificationDao().insertNotification(notification);

        // Sender notification
        notification = new Notification();
        notification.customerFrom = id;
        notification.customerTo = id;
        notification.paymentId = payment.id;
        notification.description = PAY_SENDER_NOTIFICATION_PREFIX + NameUtils.getFullName(customerTo.firstName, customerTo.lastName) + PERIOD;
        notification.type = RequestType.PAY.name();
        notification.status = true;
        notification.pending = false;
        notification.resent = false;
        notification.createdDate = DateUtils.getDateTime();
        db.notificationDao().insertNotification(notification);
    }

    private void saveTransaction() {
        EditText editTextCurrencyFrom = getView().findViewById(R.id.editTextCurrencyFrom);
        EditText editTextCurrencyTo = getView().findViewById(R.id.editTextCurrencyTo);
        TextView textViewCurrencyFromLabel = getView().findViewById(R.id.textViewCurrencyFromLabel);
        TextView textViewCurrencyToLabel = getView().findViewById(R.id.textViewCurrencyToLabel);

        AppDatabase db = AppDatabase.getDbInstance(this.getActivity().getApplicationContext());
        int id = ((MyApplication) this.getActivity().getApplication()).getId();

        Customer customerFrom = db.customerDao().findById(id);
        Customer customerTo = db.customerDao().findById(customerId);
        BankAccount preferredBankAccount = db.bankAccountDao().findPreferredBank(id);
        LookupCurrency lookupCurrencyFrom = db.lookupCurrencyDao().findByCurrency(textViewCurrencyFromLabel.getText().toString());
        LookupCurrency lookupCurrencyTo = db.lookupCurrencyDao().findByCurrency(textViewCurrencyToLabel.getText().toString());
        CurrencyConversion currencyConversion = db.currencyConversionDao().findByCurrencyFromTo(lookupCurrencyFrom.id, lookupCurrencyTo.id);

        // Save payment
        Payment payment = new Payment();
        payment.customerFrom = id;
        payment.customerTo = customerTo.id;
        payment.remarks = remarks;
        payment.status = true;
        payment.createdDate = DateUtils.getDateTime();
        db.paymentDao().insertPayment(payment);
        payment = db.paymentDao().findLatestPaymentByCustomer(id);

        // Save payment details
        PaymentDetails paymentDetails = new PaymentDetails();
        paymentDetails.paymentId = payment.id;
        paymentDetails.bankAccountFrom = preferredBankAccount.id;
        paymentDetails.bankAccountTo = DEFAULT_BANK_ACCOUNT_ID; // To be populated by recipient under pending list
        paymentDetails.currencyFrom = lookupCurrencyFrom.id;
        paymentDetails.currencyTo = lookupCurrencyTo.id;
        paymentDetails.amountFrom = Double.parseDouble(editTextCurrencyFrom.getText().toString());
        paymentDetails.amountTo = Double.parseDouble(editTextCurrencyTo.getText().toString());
        paymentDetails.exchangeRate = Double.parseDouble(REAL_FORMATTER.format(currencyConversion.value));
        db.paymentDetailsDao().insertPaymentDetails(paymentDetails);

        // Save notification
        // Receiver notification
        Notification notification = new Notification();
        notification.customerFrom = id;
        notification.customerTo = customerTo.id;
        notification.paymentId = payment.id;
        notification.description = NameUtils.getFullName(customerFrom.firstName, customerFrom.lastName) + PAY_RECEIVER_NOTIFICATION_PREFIX;
        notification.type = RequestType.PAY.name();
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
        notification.description = PAY_SENDER_ASK_DETAILS_NOTIFICATION_PREFIX + NameUtils.getFullName(customerTo.firstName, customerTo.lastName) + " and asked for the banking details.";
        notification.type = RequestType.PAY.name();
        notification.status = true;
        notification.pending = true;
        notification.resent = false;
        notification.createdDate = DateUtils.getDateTime();
        db.notificationDao().insertNotification(notification);
    }
}