package com.ipid.demo;

import static com.ipid.demo.constants.Constants.EMAIL_ALREADY_EXISTS;
import static com.ipid.demo.constants.Constants.EMPTY_FIELD;
import static com.ipid.demo.constants.Constants.EMPTY_STRING;
import static com.ipid.demo.constants.Constants.INVALID_EMAIL;
import static com.ipid.demo.constants.Constants.INVALID_PHONE_NUMBER;
import static com.ipid.demo.constants.Constants.PHONE_NUMBER_ALREADY_EXISTS;
import static com.ipid.demo.constants.Constants.PLUS;
import static com.ipid.demo.constants.Constants.SPACE;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hbb20.CountryCodePicker;
import com.ipid.demo.db.AppDatabase;
import com.ipid.demo.db.entity.Customer;
import com.ipid.demo.services.impl.ValidationServiceImpl;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileAliases#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileAliases extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ValidationServiceImpl validationService;
    private boolean isEditedPhone = false;
    private boolean isEditedEmail = false;

    public ProfileAliases() {
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
    public static ProfileAliases newInstance(String param1, String param2) {
        ProfileAliases fragment = new ProfileAliases();
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
        // Initialize service
        validationService = new ValidationServiceImpl();

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile_aliases, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        initFields();
        initEditing();
    }

    private void initFields() {
        AppDatabase db = AppDatabase.getDbInstance(this.getActivity().getApplicationContext());
        int id = ((MyApplication) this.getActivity().getApplication()).getId();
        Customer customer = db.customerDao().findById(id);

        TextView emailTextView = getView().findViewById(R.id.textViewEmailAddress);
        emailTextView.setText(customer.emailAddress);

        TextView phoneNumberTextView = getView().findViewById(R.id.textViewPhoneNumber);
        if (customer.phoneNumber == null) {
            phoneNumberTextView.setText(EMPTY_FIELD);
        } else {
            phoneNumberTextView.setText(customer.phoneNumber);
        }
    }

    private void initEditing() {
        CountryCodePicker ccp =  getView().findViewById(R.id.ccp);
        ImageView imageViewPhoneEditIcon = getView().findViewById(R.id.imageViewPhoneEditIcon);
        ImageView imageViewPhoneCloseIcon = getView().findViewById(R.id.imageViewPhoneCloseIcon);
        ImageView imageViewEmailEditIcon = getView().findViewById(R.id.imageViewEmailEditIcon);
        ImageView imageViewEmailCloseIcon = getView().findViewById(R.id.imageViewEmailCloseIcon);
        TextView textViewPhoneNumber = getView().findViewById(R.id.textViewPhoneNumber);
        TextView textViewEmailAddress = getView().findViewById(R.id.textViewEmailAddress);
        LinearLayout phoneLayout = getView().findViewById(R.id.phoneLayout);
        EditText editTextPhoneNumber = getView().findViewById(R.id.editTextPhoneNumber);
        EditText editTextEmail = getView().findViewById(R.id.editTextEmail);

        imageViewPhoneEditIcon.setOnClickListener(v -> {
            AppDatabase db = AppDatabase.getDbInstance(this.getActivity().getApplicationContext());
            int id = ((MyApplication) this.getActivity().getApplication()).getId();
            Customer customer = db.customerDao().findById(id);

            if (isEditedPhone) {
                if (!validatePhoneNumber()) {
                    return;
                }
                imageViewPhoneEditIcon.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.ic_baseline_edit_24));
                imageViewPhoneCloseIcon.setVisibility(View.GONE);
                textViewPhoneNumber.setVisibility(View.VISIBLE);
                phoneLayout.setVisibility(View.INVISIBLE);

                // Save
                customer.phoneNumber = ccp.getSelectedCountryCodeWithPlus() + SPACE + editTextPhoneNumber.getText().toString();
                db.customerDao().update(customer);

                // Refresh updated data
                textViewPhoneNumber.setText(customer.phoneNumber);
                isEditedPhone = false;
            } else {
                // Edit
                imageViewPhoneEditIcon.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.ic_baseline_save_24));
                imageViewPhoneCloseIcon.setVisibility(View.VISIBLE);
                textViewPhoneNumber.setVisibility(View.INVISIBLE);
                phoneLayout.setVisibility(View.VISIBLE);

                String numbers[] = customer.phoneNumber.split(SPACE);
                String phoneCode = numbers[0].replace(PLUS, EMPTY_STRING);
                String number = customer.phoneNumber.replace(numbers[0] + SPACE, EMPTY_STRING);

                // Retrieve updated customer information
                ccp.setCountryForPhoneCode(Integer.valueOf(phoneCode));
                editTextPhoneNumber.setText(number);
                isEditedPhone = true;
            }
        });

        imageViewPhoneCloseIcon.setOnClickListener(v -> {
            AppDatabase db = AppDatabase.getDbInstance(this.getActivity().getApplicationContext());
            int id = ((MyApplication) this.getActivity().getApplication()).getId();
            Customer customer = db.customerDao().findById(id);

            imageViewPhoneEditIcon.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.ic_baseline_edit_24));
            imageViewPhoneCloseIcon.setVisibility(View.GONE);
            textViewPhoneNumber.setVisibility(View.VISIBLE);
            phoneLayout.setVisibility(View.INVISIBLE);

            textViewPhoneNumber.setText(customer.phoneNumber);
            isEditedPhone = false;
        });

        imageViewEmailEditIcon.setOnClickListener(v -> {
            AppDatabase db = AppDatabase.getDbInstance(this.getActivity().getApplicationContext());
            int id = ((MyApplication) this.getActivity().getApplication()).getId();
            Customer customer = db.customerDao().findById(id);

            if (isEditedEmail) {
                if (!validateEmailAddress()) {
                    return;
                }
                imageViewEmailEditIcon.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.ic_baseline_edit_24));
                imageViewEmailCloseIcon.setVisibility(View.GONE);
                textViewEmailAddress.setVisibility(View.VISIBLE);
                editTextEmail.setVisibility(View.INVISIBLE);

                // Save
                customer.emailAddress = editTextEmail.getText().toString();
                db.customerDao().update(customer);

                // Refresh updated data
                textViewEmailAddress.setText(customer.emailAddress);
                isEditedEmail = false;
            } else {
                // Edit
                imageViewEmailEditIcon.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.ic_baseline_save_24));
                imageViewEmailCloseIcon.setVisibility(View.VISIBLE);
                textViewEmailAddress.setVisibility(View.INVISIBLE);
                editTextEmail.setVisibility(View.VISIBLE);

                editTextEmail.setText(customer.emailAddress);
                isEditedEmail = true;
            }
        });

        imageViewEmailCloseIcon.setOnClickListener(v -> {
            AppDatabase db = AppDatabase.getDbInstance(this.getActivity().getApplicationContext());
            int id = ((MyApplication) this.getActivity().getApplication()).getId();
            Customer customer = db.customerDao().findById(id);

            imageViewEmailEditIcon.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.ic_baseline_edit_24));
            imageViewEmailCloseIcon.setVisibility(View.GONE);
            textViewEmailAddress.setVisibility(View.VISIBLE);
            editTextEmail.setVisibility(View.INVISIBLE);

            textViewEmailAddress.setText(customer.emailAddress);
            isEditedEmail = false;
        });
    }

    public boolean validatePhoneNumber() {
        AppDatabase db = AppDatabase.getDbInstance(this.getActivity().getApplicationContext());
        int id = ((MyApplication) this.getActivity().getApplication()).getId();
        Customer customer = db.customerDao().findById(id);

        EditText editTextPhoneNumber = getView().findViewById(R.id.editTextPhoneNumber);
        CountryCodePicker ccp = getView().findViewById(R.id.ccp);

        if (editTextPhoneNumber.getText().toString().isEmpty()) {
            Toast.makeText(getActivity(), INVALID_PHONE_NUMBER, Toast.LENGTH_SHORT).show();
            return false;
        }

        String fullNumber = ccp.getSelectedCountryCodeWithPlus() + SPACE + editTextPhoneNumber.getText().toString();
        boolean isSameNumber = customer.phoneNumber.replace(SPACE, EMPTY_STRING).equalsIgnoreCase(fullNumber.replace(SPACE, EMPTY_STRING));
        if (!isSameNumber && validationService.isRegisteredPhoneNumber(db, fullNumber)) {
            Toast.makeText(getActivity(), PHONE_NUMBER_ALREADY_EXISTS, Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    public boolean validateEmailAddress() {
        AppDatabase db = AppDatabase.getDbInstance(this.getActivity().getApplicationContext());
        int id = ((MyApplication) this.getActivity().getApplication()).getId();
        Customer customer = db.customerDao().findById(id);
        EditText editTextEmail = getView().findViewById(R.id.editTextEmail);

        if (editTextEmail.getText().toString().isEmpty()) {
            Toast.makeText(getActivity(), INVALID_EMAIL, Toast.LENGTH_SHORT).show();
            return false;
        }

        boolean isSameEmail = editTextEmail.getText().toString().equals(customer.emailAddress);
        if (!isSameEmail && validationService.isRegisteredEmail(db, editTextEmail.getText().toString())) {
            Toast.makeText(getActivity(), EMAIL_ALREADY_EXISTS, Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}