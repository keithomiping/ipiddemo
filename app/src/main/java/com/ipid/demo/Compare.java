package com.ipid.demo;

import static com.ipid.demo.constants.Constants.DEFAULT_ARGS_VALUE;
import static com.ipid.demo.constants.Constants.DEFAULT_CURRENCY_AUD;
import static com.ipid.demo.constants.Constants.DEFAULT_CURRENCY_SGD;
import static com.ipid.demo.constants.Constants.DEFAULT_SELECTED_CURRENCY;
import static com.ipid.demo.constants.Constants.DEFAULT_SELECTED_CURRENCY_SELECTED;
import static com.ipid.demo.constants.Constants.DRAWABLE_TYPE;
import static com.ipid.demo.constants.Constants.EMPTY_STRING;
import static com.ipid.demo.constants.Constants.INITIAL_VALUE;
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
import com.ipid.demo.databinding.FragmentCompareBinding;
import com.ipid.demo.db.AppDatabase;
import com.ipid.demo.db.entity.CurrencyConversion;
import com.ipid.demo.db.entity.Customer;
import com.ipid.demo.db.entity.LookupCurrency;
import com.ipid.demo.models.CurrencyModel;
import com.ipid.demo.utils.NameUtils;

import java.util.ArrayList;
import java.util.List;

public class Compare extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final String TAG = "CompareFragment";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FragmentCompareBinding binding;
    public Compare() {
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
    public static Compare newInstance(String param1, String param2) {
        Compare fragment = new Compare();
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
        inflater.inflate(R.layout.fragment_compare,container,false);
        binding = FragmentCompareBinding.inflate(inflater,container,false);
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
        initCustomerName();
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
                        LookupCurrency currencyTo = db.lookupCurrencyDao().findByCurrency(textViewCurrencyTo.getText().toString());

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

                        CurrencyConversion currencyConversion = db.currencyConversionDao().findByCurrencyFromTo(currencyFrom.id, currencyTo.id);

                        double convertedTo = Double.parseDouble(editTextCurrencyFrom.getText().toString()) * currencyConversion.value;
                        editTextCurrencyTo.setText(REAL_FORMATTER.format(convertedTo));

                    } else {
                        CurrencyModel selectedItem = (CurrencyModel) spinnerCurrenciesTo.getSelectedItem();

                        textViewCurrencyTo.setText(selectedItem.getCurrency());

                        LookupCurrency currencyFrom = db.lookupCurrencyDao().findByCurrency(textViewCurrencyFrom.getText().toString());
                        LookupCurrency currencyTo = db.lookupCurrencyDao().findByCurrency(selectedItem.getCurrency());

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

    private void initCustomerName() {
        TextView textViewName = getView().findViewById(R.id.textViewName);
        TextView textViewIconName = getView().findViewById(R.id.textViewIconName);
        EditText editTextCurrencyFrom = getView().findViewById(R.id.editTextCurrencyFrom);
        EditText editTextCurrencyTo = getView().findViewById(R.id.editTextCurrencyTo);
        TextView textViewCurrencyFromLabel = getView().findViewById(R.id.textViewCurrencyFromLabel);
        TextView textViewCurrencyToLabel = getView().findViewById(R.id.textViewCurrencyToLabel);

        AppDatabase db = AppDatabase.getDbInstance(this.getActivity().getApplicationContext());
        int customerId;
        String countryFrom;
        String amountFrom;
        String remarks = "";;

        if (getArguments() != null) {
            CompareArgs args = CompareArgs.fromBundle(getArguments());
            customerId = args.getId();
            countryFrom = args.getCountryFrom();
            amountFrom = args.getAmountFrom();
            remarks = args.getRemarks();

            Customer customer = db.customerDao().findById(customerId);

            textViewName.setText(NameUtils.getFullName(customer.firstName, customer.lastName));
            textViewIconName.setText(customer.firstName.charAt(0) + "");

            if (!countryFrom.equalsIgnoreCase(DEFAULT_ARGS_VALUE)) {
                Spinner spinnerCurrenciesFrom = getView().findViewById(R.id.spinnerCompareFrom);
                spinnerCurrenciesFrom.setSelection(getIndex(spinnerCurrenciesFrom, countryFrom));

                LookupCurrency lookupCurrency = db.lookupCurrencyDao().findByCountry(countryFrom);
                textViewCurrencyFromLabel.setText(lookupCurrency.description);
            }

            if (!amountFrom.equalsIgnoreCase(DEFAULT_ARGS_VALUE)) {
                editTextCurrencyFrom.setText(amountFrom);
            }
        }

        Button compareBtn = binding.compareBtn;
        String finalRemarks = remarks;
        compareBtn.setOnClickListener((v -> {
            LookupCurrency currencyFrom = db.lookupCurrencyDao().findByCurrency(textViewCurrencyFromLabel.getText().toString());
            LookupCurrency currencyTo = db.lookupCurrencyDao().findByCurrency(textViewCurrencyToLabel.getText().toString());
            CurrencyConversion currencyConversion = db.currencyConversionDao().findByCurrencyFromTo(currencyFrom.id, currencyTo.id);

            CompareDirections.ActionCompareFragmentToResultFragment action = CompareDirections.actionCompareFragmentToResultFragment();
            action.setAmountFrom(editTextCurrencyFrom.getText().toString());
            action.setAmountTo(editTextCurrencyTo.getText().toString());
            action.setCurrencyFrom(currencyFrom.description);
            action.setCurrencyTo(currencyTo.description);
            action.setCountryFrom(currencyFrom.country);
            action.setCountryTo(currencyTo.country);
            action.setExchangeRate(REAL_FORMATTER.format(currencyConversion.value));
            action.setRemarks(finalRemarks);
            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main).navigate(action);
        }));
    }

    private int getIndex(Spinner spinner, String myString){
        for (int i=0; i < spinner.getCount(); i++){
            CurrencyModel currencyModel = (CurrencyModel) spinner.getItemAtPosition(i);
            if (currencyModel.getCountryName().equalsIgnoreCase(myString)){
                return i;
            }
        }

        return 0;
    }
}
