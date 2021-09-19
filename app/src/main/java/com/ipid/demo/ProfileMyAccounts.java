package com.ipid.demo;

import static com.ipid.demo.constants.Constants.ACCOUNT_SETUP;
import static com.ipid.demo.constants.Constants.ADD_ACCOUNT_TITLE;
import static com.ipid.demo.constants.Constants.BANK_ACCOUNTS_TITLE;
import static com.ipid.demo.constants.Constants.COMMA;
import static com.ipid.demo.constants.Constants.DRAWABLE_TYPE;
import static com.ipid.demo.constants.Constants.EMPTY_FIELD;
import static com.ipid.demo.constants.Constants.EMPTY_STRING;
import static com.ipid.demo.constants.Constants.INVALID_ACCOUNT_NUMBER;
import static com.ipid.demo.constants.Constants.NEW_LINE;
import static com.ipid.demo.constants.Constants.PREFERRED_ACCOUNT;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.ipid.demo.adapters.BankAdapter;
import com.ipid.demo.adapters.CountryAdapter;
import com.ipid.demo.databinding.FragmentProfileMyAccountsBinding;
import com.ipid.demo.db.AppDatabase;
import com.ipid.demo.db.entity.BankAccount;
import com.ipid.demo.db.entity.LookupBank;
import com.ipid.demo.db.entity.LookupCountry;
import com.ipid.demo.models.CountryModel;
import com.ipid.demo.utils.DateUtils;
import com.ipid.demo.utils.NameUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ProfileMyAccounts extends Fragment {

    private static final String TAG = "ProfileMyAccounts";

    private List<CountryModel> countryList;
    private CountryAdapter countryAdapter;
    private int reqCode = 1;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types oAf parameters
    private String mParam1;
    private String mParam2;
    private FragmentProfileMyAccountsBinding binding;

    private Button buttonProfileEdit;
    private Dialog preferredAccountDialog;
    private boolean isEditedAccount = false;

    public ProfileMyAccounts() {
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
    public static ProfileMyAccounts newInstance(String param1, String param2) {
        ProfileMyAccounts fragment = new ProfileMyAccounts();
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
        inflater.inflate(R.layout.fragment_profile_my_accounts, container, false);
        binding = FragmentProfileMyAccountsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        preferredAccountDialog = new Dialog(getActivity());

        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        // Refer to bundle arguments set in NotificationsFragment.onViewCreated
        if (getArguments() != null && getArguments().getString(ACCOUNT_SETUP) != null) {
            Switch preferredSwitch = getView().findViewById(R.id.preferredSwitch);
            TextView textViewBankAccountLabel = getView().findViewById(R.id.textViewBankAccountLabel);
            TextView editTextAccountNumber = getView().findViewById(R.id.editTextAccountNumber);
            ImageView imageViewBankAccountEditIcon = getView().findViewById(R.id.imageViewBankAccountEditIcon);
            ImageView imageViewBankAccountCloseIcon = getView().findViewById(R.id.imageViewBankAccountCloseIcon);
            ConstraintLayout viewLayoutMyAccounts = getView().findViewById(R.id.viewLayoutMyAccounts);
            ConstraintLayout editLayoutMyAccounts = getView().findViewById(R.id.editLayoutMyAccounts);

            textViewBankAccountLabel.setText(ADD_ACCOUNT_TITLE);
            imageViewBankAccountEditIcon.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.ic_baseline_save_24));
            imageViewBankAccountCloseIcon.setVisibility(View.VISIBLE);
            viewLayoutMyAccounts.setVisibility(View.INVISIBLE);
            editLayoutMyAccounts.setVisibility(View.VISIBLE);

            AppDatabase db = AppDatabase.getDbInstance(this.getActivity().getApplicationContext());
            int id = ((MyApplication) this.getActivity().getApplication()).getId();
            List<BankAccount> bankAccounts = db.bankAccountDao().findByCustomerId(id);

            if (bankAccounts.size() == 0) {
                preferredSwitch.setChecked(Boolean.TRUE);
            }

            preferredSwitch.setOnClickListener(v -> {
                List<BankAccount> accounts = db.bankAccountDao().findByCustomerId(id);

                if (!preferredSwitch.isChecked() && accounts.size() == 0) {
                    showPreferredAccountDialog();
                    preferredSwitch.setChecked(Boolean.TRUE);
                }
            });
            // Set initial data
            editTextAccountNumber.setText(EMPTY_STRING);
            isEditedAccount = true;
        }

        // Initialize fields and events
        initBankAccounts();
        initImportBankAccount();
        initCountrySpinner();
        initBankSpinner();
        initEditing();
    }

    private void initEditing() {
        TextView textViewBankAccountLabel = getView().findViewById(R.id.textViewBankAccountLabel);
        ImageView imageViewBankAccountEditIcon = getView().findViewById(R.id.imageViewBankAccountEditIcon);
        ImageView imageViewBankAccountCloseIcon = getView().findViewById(R.id.imageViewBankAccountCloseIcon);
        ConstraintLayout viewLayoutMyAccounts = getView().findViewById(R.id.viewLayoutMyAccounts);
        ConstraintLayout editLayoutMyAccounts = getView().findViewById(R.id.editLayoutMyAccounts);
        EditText accountNumber = getView().findViewById(R.id.editTextAccountNumber);
        Spinner spinnerBanks = getView().findViewById(R.id.spinnerBanks);
        Spinner spinnerCountries = getView().findViewById(R.id.spinnerCountries);
        Switch preferredSwitch = getView().findViewById(R.id.preferredSwitch);

        imageViewBankAccountEditIcon.setOnClickListener(v -> {
            if (isEditedAccount) {
                if (!validateAccount()) {
                    return;
                }

                // Show add account screen
                AppDatabase db = AppDatabase.getDbInstance(this.getActivity().getApplicationContext());
                int id = ((MyApplication) this.getActivity().getApplication()).getId();

                textViewBankAccountLabel.setText(BANK_ACCOUNTS_TITLE);
                imageViewBankAccountEditIcon.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.ic_baseline_edit_24));
                imageViewBankAccountCloseIcon.setVisibility(View.GONE);
                viewLayoutMyAccounts.setVisibility(View.VISIBLE);
                editLayoutMyAccounts.setVisibility(View.INVISIBLE);

                LookupBank lookupBank = db.lookupBankDao().findByName(spinnerBanks.getSelectedItem().toString());
                CountryModel countryModel = (CountryModel) spinnerCountries.getSelectedItem();

                if (preferredSwitch.isChecked()) {
                    db.bankAccountDao().resetPreferredBankAccounts(id);
                }

                // Save bank account information
                BankAccount bankAccount = new BankAccount();
                bankAccount.customerId = id;
                bankAccount.bankId = lookupBank.id;
                bankAccount.accountNumber = accountNumber.getText().toString();
                bankAccount.country = countryModel.getCountryName();
                bankAccount.status = true;
                bankAccount.preferred = preferredSwitch.isChecked();
                bankAccount.createdDate = DateUtils.getDateTime();
                db.bankAccountDao().insertBankAccount(bankAccount);

                initBankAccounts();
                isEditedAccount = false;

            } else {
                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main).navigate(R.id.action_notificationFragment_to_preferredAccountFragment);
            }
        });

        imageViewBankAccountCloseIcon.setOnClickListener(v -> {
            textViewBankAccountLabel.setText(BANK_ACCOUNTS_TITLE);
            imageViewBankAccountEditIcon.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.ic_baseline_edit_24));
            imageViewBankAccountCloseIcon.setVisibility(View.GONE);
            viewLayoutMyAccounts.setVisibility(View.VISIBLE);
            editLayoutMyAccounts.setVisibility(View.INVISIBLE);

            initBankAccounts();
            isEditedAccount = false;
        });
    }

    private boolean validateAccount() {
        EditText editTextAccountNumber = getView().findViewById(R.id.editTextAccountNumber);

        if (editTextAccountNumber.getText().toString().isEmpty()) {
            Toast.makeText(getActivity(), INVALID_ACCOUNT_NUMBER, Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (reqCode == requestCode && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                return;
            }
            processData(data.getData());
        }
    }

    private void initImportBankAccount() {
        TextView textViewImportBankAccount = binding.textViewImportBankAccount;
        textViewImportBankAccount.setOnClickListener(v -> {
            openFileChooser();
        });
    }

    private void processData(Uri uri) {
        AppDatabase db = AppDatabase.getDbInstance(this.getActivity().getApplicationContext());
        int id = ((MyApplication) this.getActivity().getApplication()).getId();

        BufferedReader br;
        try {
            br = new BufferedReader(new InputStreamReader(getActivity().getContentResolver().openInputStream(uri)));
            String line;
            while ((line = br.readLine()) != null) {
                Log.i(TAG, "processData: " + line);

                String bankCode = line.split(COMMA)[0];
                String bankNumber = line.split(COMMA)[1];
                Log.i(TAG, "line[0]: " + bankCode);
                Log.i(TAG, "line[1]: " + bankNumber);

                LookupBank lookupBank = db.lookupBankDao().findByCode(bankCode);

                // Save bank account information
                BankAccount bankAccount = new BankAccount();
                bankAccount.customerId = id;
                bankAccount.bankId = lookupBank.id;
                bankAccount.accountNumber = bankNumber;
                bankAccount.country = lookupBank.country;
                bankAccount.status = true;
                bankAccount.createdDate = DateUtils.getDateTime();
                db.bankAccountDao().insertBankAccount(bankAccount);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        binding.editLayoutMyAccounts.setVisibility(View.INVISIBLE);
        binding.viewLayoutMyAccounts.setVisibility(View.VISIBLE);

        // Refresh list and show view layout
        initBankAccounts();
    }

    private void initBankAccounts() {
        AppDatabase db = AppDatabase.getDbInstance(this.getActivity().getApplicationContext());
        int id = ((MyApplication) this.getActivity().getApplication()).getId();
        List<BankAccount> bankAccounts = db.bankAccountDao().findByCustomerId(id);

        TextView textViewBankAccount = getView().findViewById(R.id.textViewBankAccount);
        if (bankAccounts.size() == 0) {
            textViewBankAccount.setText(EMPTY_FIELD + NEW_LINE);
        } else {
            StringBuilder banks = new StringBuilder();
            for (BankAccount bankAccount : bankAccounts) {
                if (bankAccount.preferred) {
                    banks.append(PREFERRED_ACCOUNT + NEW_LINE);
                }

                LookupBank lookupBank = db.lookupBankDao().findById(bankAccount.bankId);
                banks.append(lookupBank.name + NEW_LINE + bankAccount.accountNumber + NEW_LINE + lookupBank.code + NEW_LINE + NEW_LINE);
            }
            banks.deleteCharAt(banks.length() - 1);
            textViewBankAccount.setText(banks);
        }
    }

    public void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent, reqCode);
    }

    private void initBankSpinner() {
        Spinner spinnerCountries = getView().findViewById(R.id.spinnerCountries);
        CountryModel clickedItem = (CountryModel) spinnerCountries.getSelectedItem();

        AppDatabase db = AppDatabase.getDbInstance(this.getActivity().getApplicationContext());
        List<LookupBank> banks = db.lookupBankDao().findByCountry(clickedItem.getCountryName());

        List bankList = new ArrayList();
        for (LookupBank lookupBank : banks) {
            bankList.add(lookupBank.name);
        }

        Spinner spinnerBanks = getView().findViewById(R.id.spinnerBanks);

        BankAdapter bankAdapter = new BankAdapter(getContext(), bankList);
        spinnerBanks.setAdapter(bankAdapter);

        spinnerBanks.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initCountrySpinner() {
        AppDatabase db = AppDatabase.getDbInstance(this.getActivity().getApplicationContext());
        List<LookupCountry> countries = db.lookupCountryDao().getAllCountries();

        countryList = new ArrayList();
        for (LookupCountry lookupCountry : countries) {
            int id = getResources().getIdentifier(NameUtils.getDrawableFlagName(lookupCountry.description.toLowerCase()), DRAWABLE_TYPE, getContext().getPackageName());
            countryList.add(new CountryModel(lookupCountry.description, id));
        }

        Spinner spinnerCountries = getView().findViewById(R.id.spinnerCountries);

        countryAdapter = new CountryAdapter(getContext(), countryList);
        spinnerCountries.setAdapter(countryAdapter);

        spinnerCountries.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                initBankSpinner();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void showPreferredAccountDialog() {
        preferredAccountDialog.setContentView(R.layout.popup_preferred_bank);

        ImageView closeBtn = preferredAccountDialog.findViewById(R.id.closePopupImg);
        closeBtn.setOnClickListener(v -> {
            preferredAccountDialog.dismiss();
        });

        preferredAccountDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        preferredAccountDialog.show();
    }
}