package com.ipid.demo.ui.notifications;

import static com.ipid.demo.constants.Constants.ABOUT_ME_ALIASES;
import static com.ipid.demo.constants.Constants.ACCOUNT_SETUP;
import static com.ipid.demo.constants.Constants.CLOSE;
import static com.ipid.demo.constants.Constants.DRAWABLE_TYPE;
import static com.ipid.demo.constants.Constants.EDIT;
import static com.ipid.demo.constants.Constants.EMPTY_FIELD;
import static com.ipid.demo.constants.Constants.EMPTY_STRING;
import static com.ipid.demo.constants.Constants.INVALID_ADDRESS;
import static com.ipid.demo.constants.Constants.INVALID_EMAIL;
import static com.ipid.demo.constants.Constants.INVALID_NAME;
import static com.ipid.demo.constants.Constants.INVALID_PHONE_NUMBER;
import static com.ipid.demo.constants.Constants.MY_ACCOUNTS;
import static com.ipid.demo.constants.Constants.MY_REWARDS;
import static com.ipid.demo.constants.Constants.NEW_LINE;
import static com.ipid.demo.constants.Constants.NEXT;
import static com.ipid.demo.constants.Constants.PREFERRED_ACCOUNT;
import static com.ipid.demo.constants.Constants.PROFILE_ABOUT_ME;
import static com.ipid.demo.constants.Constants.SPACE;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.hbb20.CountryCodePicker;
import com.ipid.demo.adapters.BankAccountAdapter;
import com.ipid.demo.models.BankModel;
import com.ipid.demo.models.CountryModel;
import com.ipid.demo.MyApplication;
import com.ipid.demo.ProfileAboutMe;
import com.ipid.demo.ProfileAliases;
import com.ipid.demo.ProfileMyAccounts;
import com.ipid.demo.ProfileMyRewards;
import com.ipid.demo.R;
import com.ipid.demo.databinding.FragmentNotificationsBinding;
import com.ipid.demo.db.AppDatabase;
import com.ipid.demo.db.entity.BankAccount;
import com.ipid.demo.db.entity.Customer;
import com.ipid.demo.db.entity.LookupBank;
import com.ipid.demo.services.impl.ValidationServiceImpl;
import com.ipid.demo.utils.DateUtils;
import com.ipid.demo.utils.NameUtils;

import java.util.ArrayList;
import java.util.List;

public class NotificationsFragment extends Fragment {

    private static final String TAG = "NotificationsFragment";

    private NotificationsViewModel notificationsViewModel;
    private FragmentNotificationsBinding binding;
    private ValidationServiceImpl validationService;

    private Dialog dialog;
    private Dialog removeBankAccountsDialog;
    private Dialog preferredAccountDialog;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        dialog = new Dialog(getActivity());
        removeBankAccountsDialog = new Dialog(getActivity());
        preferredAccountDialog = new Dialog(getActivity());

        validationService = new ValidationServiceImpl();

        return root;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        TextView textViewAboutMe = binding.textViewAboutMe;
        TextView textViewMyAccounts = binding.textViewMyAccounts;
        TextView textViewMyRewards = binding.textViewMyRewards;
        Button buttonProfileNext = binding.buttonProfileNext;

        // Check if account is not setup
        if (getArguments() != null) {
            NotificationsFragmentArgs args = NotificationsFragmentArgs.fromBundle(getArguments());
            boolean accountSetup = args.getAccountSetup();
            boolean viewMyAccounts = args.getViewMyAccounts();
            Log.i(TAG, "onViewCreated: " + accountSetup);
            Log.i(TAG, "onViewCreated: " + viewMyAccounts);

            if (accountSetup) {
                if (viewMyAccounts) {
                    // User will be redirected to setup their bank account if no bank account has been setup in profile
                    textViewMyAccounts.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.textview_selected_round_corners));
                    textViewAboutMe.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.textview_unselected_round_corners));
                    textViewMyRewards.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.textview_unselected_round_corners));

                    buttonProfileNext.setVisibility(View.VISIBLE);
                    buttonProfileNext.setText(CLOSE);

                    // Used to view the edit layout by default
                    getParentFragmentManager().beginTransaction().replace(R.id.flFragment, new ProfileMyAccounts(), MY_ACCOUNTS).commit();

                } else {
                    getParentFragmentManager().beginTransaction().replace(R.id.flFragment, new ProfileAboutMe(), PROFILE_ABOUT_ME).commit();
                }
            } else {
                // User will be redirected to setup their bank account if no bank account has been setup in profile
                textViewMyAccounts.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.textview_selected_round_corners));
                textViewAboutMe.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.textview_unselected_round_corners));
                textViewMyRewards.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.textview_unselected_round_corners));

                buttonProfileNext.setVisibility(View.VISIBLE);
                buttonProfileNext.setText(CLOSE);

                // Used to view the edit layout by default
                ProfileMyAccounts profileMyAccounts = new ProfileMyAccounts();
                Bundle bundleArgs = new Bundle();
                bundleArgs.putString(ACCOUNT_SETUP, Boolean.toString(accountSetup));
                profileMyAccounts.setArguments(bundleArgs);
                getParentFragmentManager().beginTransaction().replace(R.id.flFragment, profileMyAccounts, MY_ACCOUNTS).commit();
            }
        }

        initTabs();
        initNextButton();
    }

    private void initTabs() {
        TextView textViewAboutMe = binding.textViewAboutMe;
        TextView textViewMyAccounts = binding.textViewMyAccounts;
        TextView textViewMyRewards = binding.textViewMyRewards;
        Button buttonProfileNext = binding.buttonProfileNext;

        textViewAboutMe.setOnClickListener(v -> {
            textViewAboutMe.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.textview_selected_round_corners));
            textViewMyAccounts.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.textview_unselected_round_corners));
            textViewMyRewards.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.textview_unselected_round_corners));

            buttonProfileNext.setText(NEXT);
            buttonProfileNext.setVisibility(View.VISIBLE);

            getParentFragmentManager().beginTransaction().replace(R.id.flFragment, new ProfileAboutMe(), PROFILE_ABOUT_ME).commit();
        });

        textViewMyAccounts.setOnClickListener(v -> {
            textViewMyAccounts.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.textview_selected_round_corners));
            textViewAboutMe.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.textview_unselected_round_corners));
            textViewMyRewards.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.textview_unselected_round_corners));

            buttonProfileNext.setText(CLOSE);
            buttonProfileNext.setVisibility(View.VISIBLE);

            getParentFragmentManager().beginTransaction().replace(R.id.flFragment, new ProfileMyAccounts(), MY_ACCOUNTS).commit();
        });

        textViewMyRewards.setOnClickListener(v -> {
            textViewMyRewards.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.textview_selected_round_corners));
            textViewAboutMe.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.textview_unselected_round_corners));
            textViewMyAccounts.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.textview_unselected_round_corners));

            buttonProfileNext.setVisibility(View.GONE);

            getParentFragmentManager().beginTransaction().replace(R.id.flFragment, new ProfileMyRewards(), MY_REWARDS).commit();
        });
    }

    private void initNextButton() {
        TextView textViewAboutMe = binding.textViewAboutMe;
        TextView textViewMyAccounts = binding.textViewMyAccounts;
        TextView textViewMyRewards = binding.textViewMyRewards;
        Button buttonProfileNext = binding.buttonProfileNext;

        buttonProfileNext.setOnClickListener(v -> {
            ProfileAboutMe aboutMeFragment = (ProfileAboutMe) getParentFragmentManager().findFragmentByTag(PROFILE_ABOUT_ME);
            if (aboutMeFragment != null && aboutMeFragment.isVisible()) {
                TextView textViewDetails = getView().findViewById(R.id.textViewAboutMeDetails);
                TextView textViewAboutMeAliases = getView().findViewById(R.id.textViewAboutMeAliases);

                // FIXME Show my aliases if profile detail is displayed
                if (ContextCompat.getColor(getContext(), R.color.font_color) == textViewDetails.getCurrentTextColor()) {
                    ProfileAliases profileAliases = new ProfileAliases();

                    textViewDetails.setTextColor(ContextCompat.getColor(getContext(), R.color.font_color_200));
                    textViewAboutMeAliases.setTextColor(ContextCompat.getColor(getContext(), R.color.font_color));

                    // If "Who I am" tab is currently displayed, show "My Aliases" tab
                    getParentFragmentManager().beginTransaction().replace(R.id.profileAboutMeFragment, profileAliases, ABOUT_ME_ALIASES).commit();

                } else {
                    textViewMyAccounts.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.textview_selected_round_corners));
                    textViewAboutMe.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.textview_unselected_round_corners));
                    textViewMyRewards.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.textview_unselected_round_corners));

                    buttonProfileNext.setText(CLOSE);
                    buttonProfileNext.setVisibility(View.VISIBLE);

                    // If "My Aliases" tab is currently displayed, show "My Accounts" tab
                    getParentFragmentManager().beginTransaction().replace(R.id.flFragment, new ProfileMyAccounts(), MY_ACCOUNTS).commit();
                }
            }

            ProfileMyAccounts myAccountsFragment = (ProfileMyAccounts) getParentFragmentManager().findFragmentByTag(MY_ACCOUNTS);
            if (myAccountsFragment != null && myAccountsFragment.isVisible()) {
                // Close
                // Redirect to home page
                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main).navigate(R.id.action_notificationFragment_to_homeFragment);
            }
        });
    }

    private void showDialog() {
        dialog.setContentView(R.layout.popup_edit_bank_accounts);

        ImageView closeBtn = dialog.findViewById(R.id.closePopupImg);
        Button btnAdd = dialog.findViewById(R.id.btnAdd);
        Button btnRemove = dialog.findViewById(R.id.btnRemove);
        EditText accountNumber = binding.flFragment.findViewById(R.id.editTextAccountNumber);
        Switch preferredSwitch = binding.flFragment.findViewById(R.id.preferredSwitch);

        closeBtn.setOnClickListener(v -> {
            dialog.dismiss();
        });

        preferredSwitch.setOnClickListener(v -> {
            AppDatabase db = AppDatabase.getDbInstance(this.getActivity().getApplicationContext());
            int id = ((MyApplication) this.getActivity().getApplication()).getId();
            List<BankAccount> bankAccounts = db.bankAccountDao().findByCustomerId(id);

            if (!preferredSwitch.isChecked() && bankAccounts.size() == 0) {
                showPreferredAccountDialog();
                preferredSwitch.setChecked(Boolean.TRUE);
            }
        });

        btnAdd.setOnClickListener(v -> {
            dialog.dismiss();

            accountNumber.setText(EMPTY_STRING);  // Needed to clear previous value

            AppDatabase db = AppDatabase.getDbInstance(this.getActivity().getApplicationContext());
            int id = ((MyApplication) this.getActivity().getApplication()).getId();
            List<BankAccount> bankAccounts = db.bankAccountDao().findByCustomerId(id);

            if (bankAccounts.size() == 0) {
                preferredSwitch.setChecked(Boolean.TRUE);
            }

            // Render edit fields
            // Redirect to bank accounts screen
            binding.flFragment.findViewById(R.id.viewLayoutMyAccounts).setVisibility(View.INVISIBLE);
            binding.flFragment.findViewById(R.id.editLayoutMyAccounts).setVisibility(View.VISIBLE);
        });

        btnRemove.setOnClickListener(v -> {
            dialog.dismiss();
            showRemoveBankAccountsDialog();
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
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

    private void showRemoveBankAccountsDialog() {
        removeBankAccountsDialog.setContentView(R.layout.popup_remove_bank_accounts);

        ListView listView = removeBankAccountsDialog.findViewById(R.id.listViewBankAccounts);
        AppDatabase db = AppDatabase.getDbInstance(this.getActivity().getApplicationContext());
        int id = ((MyApplication) this.getActivity().getApplication()).getId();

        List<BankAccount> bankAccounts = db.bankAccountDao().findByCustomerId(id);
        List<BankModel> bankModelList = new ArrayList();
        for (BankAccount bankAccount : bankAccounts) {
            LookupBank lookupBank = db.lookupBankDao().findById(bankAccount.bankId);

            int drawableCountry = getResources().getIdentifier(NameUtils.getDrawableFlagName(bankAccount.country.toLowerCase()), DRAWABLE_TYPE, getContext().getPackageName());
            bankModelList.add(new BankModel(false, bankAccount.id, lookupBank.name, bankAccount.accountNumber, drawableCountry, false, bankAccount.preferred));
        }

        BankAccountAdapter adapter = new BankAccountAdapter(this.getActivity(), bankModelList);
        listView.setAdapter(adapter);

        ImageView closeBtn = removeBankAccountsDialog.findViewById(R.id.closePopupImg);

        removeBankAccountsDialog.setOnCancelListener(
            dialog -> {
                //When you touch outside of dialog bounds,
                //the dialog gets canceled and this method executes.
                refreshBankAccounts();
            }
        );

        closeBtn.setOnClickListener(v -> {
            removeBankAccountsDialog.dismiss();
            refreshBankAccounts();
        });

        removeBankAccountsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        removeBankAccountsDialog.show();
    }

    private void refreshBankAccounts() {
        AppDatabase db = AppDatabase.getDbInstance(this.getActivity().getApplicationContext());
        int id = ((MyApplication) this.getActivity().getApplication()).getId();

        TextView textViewBankAccount = binding.flFragment.findViewById(R.id.textViewBankAccount);
        List<BankAccount> updatedBankAccounts = db.bankAccountDao().findByCustomerId(id);
        if (updatedBankAccounts.size() == 0) {
            textViewBankAccount.setText(EMPTY_FIELD + NEW_LINE);
        } else {
            StringBuilder banks = new StringBuilder();
            for (BankAccount account : updatedBankAccounts) {
                if (account.preferred) {
                    banks.append(PREFERRED_ACCOUNT + NEW_LINE);
                }

                LookupBank bank = db.lookupBankDao().findById(account.bankId);
                banks.append(bank.name + NEW_LINE + account.accountNumber + NEW_LINE + bank.code + NEW_LINE + NEW_LINE);
            }
            banks.deleteCharAt(banks.length() - 1);
            textViewBankAccount.setText(banks);
        }
    }

    private boolean validateProfileDetails() {
        EditText name = binding.flFragment.findViewById(R.id.editTextName);
        EditText address = binding.flFragment.findViewById(R.id.editTextAddress);

        if (name.getText().toString().split(SPACE).length <= 1) {
            Toast.makeText(getActivity(), INVALID_NAME, Toast.LENGTH_SHORT).show();
            return false;
        }

        if (address.getText().toString().isEmpty()) {
            Toast.makeText(getActivity(), INVALID_ADDRESS, Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private boolean validateProfileAliases() {
        EditText phoneNumber = binding.flFragment.findViewById(R.id.editTextPhoneNumber);
        EditText email = binding.flFragment.findViewById(R.id.editTextEmail);

        if (phoneNumber.getText().toString().isEmpty()) {
            Toast.makeText(getActivity(), INVALID_PHONE_NUMBER, Toast.LENGTH_SHORT).show();
            return false;
        }

        if (email.getText().toString().isEmpty()) {
            Toast.makeText(getActivity(), INVALID_EMAIL, Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}