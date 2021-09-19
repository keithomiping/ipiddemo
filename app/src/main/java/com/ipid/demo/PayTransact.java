package com.ipid.demo;

import static com.facebook.share.model.GameRequestContent.ActionType.SEND;
import static com.ipid.demo.constants.Constants.DEFAULT_SELECTED_CURRENCY;
import static com.ipid.demo.constants.Constants.DEFAULT_SELECTED_CURRENCY_SELECTED;
import static com.ipid.demo.constants.Constants.INVALID_ACCOUNT_NUMBER;
import static com.ipid.demo.constants.Constants.INVALID_AMOUNT;
import static com.ipid.demo.constants.Constants.PAY;
import static com.ipid.demo.constants.Constants.REAL_FORMATTER;
import static com.ipid.demo.constants.Constants.REQUEST;
import static com.ipid.demo.constants.Constants.ZERO;

import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.ipid.demo.constants.RequestType;
import com.ipid.demo.databinding.FragmentPayTransactBinding;
import com.ipid.demo.db.AppDatabase;
import com.ipid.demo.db.entity.BankAccount;
import com.ipid.demo.db.entity.CurrencyConversion;
import com.ipid.demo.db.entity.LookupCurrency;
import com.ipid.demo.services.impl.ValidationServiceImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple z{@link Fragment} subclass.
 * Use the {@link PayTransact#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PayTransact extends Fragment {
    private static final String TAG = "PayTransactFragment";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private ValidationServiceImpl validationService;
    private FragmentPayTransactBinding binding;
    private String requestType = RequestType.PAY.name();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PayTransact() {
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
    public static PayTransact newInstance(String param1, String param2) {
        PayTransact fragment = new PayTransact();
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
        inflater.inflate(R.layout.fragment_pay_transact, container, false);
        binding = FragmentPayTransactBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            PayTransactArgs resultArgs = PayTransactArgs.fromBundle(getArguments());
            requestType = resultArgs.getRequestType();
        }

        initListeners();
        initCurrencySpinner();
        initText();
    }

    private void initCurrencySpinner() {
        AppDatabase db = AppDatabase.getDbInstance(this.getActivity().getApplicationContext());
        List<LookupCurrency> lookupCurrencies = db.lookupCurrencyDao().getAllCurrencies();
        List<String> currencies = populateCurrencies(lookupCurrencies);

        Spinner spinner = binding.currencySpinner;
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, currencies);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private List<String> populateCurrencies(List<LookupCurrency> lookupCurrencies) {
        List<String> currencies = new ArrayList<>(lookupCurrencies.size());

        for (LookupCurrency lookupCurrency : lookupCurrencies) {
            currencies.add(lookupCurrency.description);
        }

        return currencies;
    }

    private void setAmount(String text) {
        TextView textViewAmount = binding.textViewAmount;

        if (textViewAmount.getText().toString().equals(ZERO)) {
            textViewAmount.setText(text);
        } else {
            textViewAmount.append(text);
        }
    }

    private void initListeners() {
        TextView textViewAmount = binding.textViewAmount;
        TextView textViewOne = binding.textViewOne;
        EditText editTextRemarks = binding.editTextRemarks;
        Button btnContinue = binding.btnContinue;
        Spinner spinner = binding.currencySpinner;

        textViewOne.setOnClickListener((v -> {
            setAmount("1");
        }));

        TextView textViewTwo = binding.textViewTwo;
        textViewTwo.setOnClickListener((v -> {
            setAmount("2");
        }));

        TextView textViewThree = binding.textViewThree;
        textViewThree.setOnClickListener((v -> {
            setAmount("3");
        }));

        TextView textViewFour = binding.textViewFour;
        textViewFour.setOnClickListener((v -> {
            setAmount("4");
        }));

        TextView textViewFive = binding.textViewFive;
        textViewFive.setOnClickListener((v -> {
            setAmount("5");
        }));

        TextView textViewSix = binding.textViewSix;
        textViewSix.setOnClickListener((v -> {
            setAmount("6");
        }));

        TextView textViewSeven = binding.textViewSeven;
        textViewSeven.setOnClickListener((v -> {
            setAmount("7");
        }));

        TextView textViewEight = binding.textViewEight;
        textViewEight.setOnClickListener((v -> {
            setAmount("8");
        }));
        TextView textViewNine = binding.textViewNine;
        textViewNine.setOnClickListener((v -> {
            setAmount("9");
        }));

        TextView textViewZero = binding.textViewZero;
        textViewZero.setOnClickListener((v -> {
            setAmount("0");
        }));

        TextView textViewDot = binding.textViewDot;
        textViewDot.setOnClickListener((v -> {
            textViewAmount.append(".");
        }));

        ImageView imageViewBackSpace = binding.imageViewBackSpace;
        imageViewBackSpace.setOnClickListener((v -> {
            String currentAmount = textViewAmount.getText().toString();
            if (currentAmount.length() > 1) {
                textViewAmount.setText(currentAmount.substring(0, currentAmount.length() - 1));
            } else {
                textViewAmount.setText("0");
            }
        }));

        btnContinue.setOnClickListener(v -> {
            if (!validate()) {
                return;
            }

            AppDatabase db = AppDatabase.getDbInstance(this.getActivity().getApplicationContext());
            int id = ((MyApplication) this.getActivity().getApplication()).getId();

            BankAccount bankAccount = db.bankAccountDao().findPreferredBank(id);
            LookupCurrency preferredBankCurrency = db.lookupCurrencyDao().findByCountry(bankAccount.country);
            List<LookupCurrency> lookupCurrencies = db.lookupCurrencyDao().getAllCurrencies();
            List<String> currencies = populateCurrencies(lookupCurrencies);

            String currencyFrom = spinner.getSelectedItem().toString();
            String currencyTo = preferredBankCurrency.description;  // Get from preferred bank account

            if (currencyTo.equalsIgnoreCase(currencyFrom)) {
                if (currencies.get(DEFAULT_SELECTED_CURRENCY).equalsIgnoreCase(currencyTo)) {
                    currencyTo = currencies.get(DEFAULT_SELECTED_CURRENCY_SELECTED);
                } else {
                    currencyTo = currencies.get(DEFAULT_SELECTED_CURRENCY);
                }
            }

            LookupCurrency lookupCurrencyFrom = db.lookupCurrencyDao().findByCurrency(currencyFrom);
            LookupCurrency lookupCurrencyTo = db.lookupCurrencyDao().findByCurrency(currencyTo);
            CurrencyConversion currencyConversion = db.currencyConversionDao().findByCurrencyFromTo(lookupCurrencyFrom.id, lookupCurrencyTo.id);
            double convertedTo = Double.parseDouble(textViewAmount.getText().toString()) * currencyConversion.value;

            PayTransactDirections.ActionPayTransactFragmentToPayFragment action = PayTransactDirections.actionPayTransactFragmentToPayFragment();
            action.setRequestType(requestType);
            action.setAmountFrom(REAL_FORMATTER.format(Double.parseDouble(textViewAmount.getText().toString())));
            action.setAmountTo(REAL_FORMATTER.format(convertedTo));
            action.setCurrencyFrom(currencyFrom);
            action.setCurrencyTo(currencyTo);
            action.setExchangeRate(REAL_FORMATTER.format(currencyConversion.value));
            action.setRemarks(editTextRemarks.getText().toString());
            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main).navigate(action);
        });
    }

    private boolean validate() {
        TextView textViewAmount = binding.textViewAmount;
        double amount = Double.parseDouble(textViewAmount.getText().toString());

        if (amount == 0D) {
            Toast.makeText(getActivity(), INVALID_AMOUNT, Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void initText() {
        TextView textViewAmount = binding.textViewTitle;
        TextView textViewOne = binding.textViewOne;
        TextView textViewTwo = binding.textViewTwo;
        TextView textViewThree = binding.textViewThree;
        TextView textViewFour = binding.textViewFour;
        TextView textViewFive = binding.textViewFive;
        TextView textViewSix = binding.textViewSix;
        TextView textViewSeven = binding.textViewSeven;
        TextView textViewEight = binding.textViewEight;
        TextView textViewNine = binding.textViewNine;
        TextView textViewZero = binding.textViewZero;
        TextView textViewDot = binding.textViewDot;

        if (requestType == RequestType.PAY.name()) {
            textViewAmount.setText(PAY);
        } else {
            textViewAmount.setText(REQUEST);
        }

        textViewOne.setPaintFlags(textViewOne.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        textViewTwo.setPaintFlags(textViewTwo.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        textViewThree.setPaintFlags(textViewThree.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        textViewFour.setPaintFlags(textViewFour.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        textViewFive.setPaintFlags(textViewFive.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        textViewSix.setPaintFlags(textViewSix.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        textViewSeven.setPaintFlags(textViewSeven.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        textViewEight.setPaintFlags(textViewEight.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        textViewNine.setPaintFlags(textViewNine.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        textViewZero.setPaintFlags(textViewZero.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        textViewDot.setPaintFlags(textViewDot.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    }
}