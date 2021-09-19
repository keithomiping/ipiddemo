package com.ipid.demo;

import static android.app.Activity.RESULT_OK;

import static com.ipid.demo.constants.Constants.EMPTY_FIELD;
import static com.ipid.demo.constants.Constants.GOOGLE_PLACES_API_KEY;
import static com.ipid.demo.constants.Constants.INVALID_ADDRESS;
import static com.ipid.demo.constants.Constants.INVALID_MOBILE_NUMBER;
import static com.ipid.demo.constants.Constants.INVALID_NAME;
import static com.ipid.demo.constants.Constants.SPACE;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.ipid.demo.db.AppDatabase;
import com.ipid.demo.db.entity.Customer;
import com.ipid.demo.utils.NameUtils;

import java.util.Arrays;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileDetails#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileDetails extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private EditText editTextAddress;
    private boolean isEditedName = false;
    private boolean isEditedAddress = false;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileDetails() {
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
    public static ProfileDetails newInstance(String param1, String param2) {
        ProfileDetails fragment = new ProfileDetails();
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
        return inflater.inflate(R.layout.fragment_profile_details, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        initFields();
        initAddress();
        initEditing();
    }

    private void initFields() {
        AppDatabase db = AppDatabase.getDbInstance(this.getActivity().getApplicationContext());
        int id = ((MyApplication) this.getActivity().getApplication()).getId();
        Customer customer = db.customerDao().findById(id);

        TextView nameTextView = getView().findViewById(R.id.textViewName);
        if (customer.firstName == null || customer.lastName == null) {
            nameTextView.setText(EMPTY_FIELD);
        } else {
            nameTextView.setText(NameUtils.getFullName(customer.firstName, customer.lastName));
        }

        TextView addressTextView = getView().findViewById(R.id.textViewAddress);
        if (customer.address == null) {
            addressTextView.setText(EMPTY_FIELD);
        } else {
            addressTextView.setText(customer.address);
        }
    }

    private void initAddress() {
        // Initialize places
        Places.initialize(getActivity().getApplicationContext(), GOOGLE_PLACES_API_KEY);

        // Set editText non focusable
        editTextAddress = getView().findViewById(R.id.editTextAddress);
        editTextAddress.setFocusable(false);
        editTextAddress.setOnClickListener(v -> {
            // Initialize place field list
            List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.NAME);
            // Create intent
            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fieldList).build(getActivity());
            // Start activity result
            startActivityForResult(intent, 100);
        });
    }

    private void initEditing() {
        ImageView imageViewNameEditIcon = getView().findViewById(R.id.imageViewNameEditIcon);
        ImageView imageViewNameCloseIcon = getView().findViewById(R.id.imageViewNameCloseIcon);
        ImageView imageViewAddressEditIcon = getView().findViewById(R.id.imageViewAddressEditIcon);
        ImageView imageViewAddressCloseIcon = getView().findViewById(R.id.imageViewAddressCloseIcon);
        TextView textViewName = getView().findViewById(R.id.textViewName);
        TextView textViewAddress = getView().findViewById(R.id.textViewAddress);
        EditText editTextName = getView().findViewById(R.id.editTextName);
        EditText editTextAddress = getView().findViewById(R.id.editTextAddress);

        imageViewNameEditIcon.setOnClickListener(v -> {
            AppDatabase db = AppDatabase.getDbInstance(this.getActivity().getApplicationContext());
            int id = ((MyApplication) this.getActivity().getApplication()).getId();
            Customer customer = db.customerDao().findById(id);

            if (isEditedName) {
                if (!validateName()) {
                    return;
                }
                imageViewNameEditIcon.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.ic_baseline_edit_24));
                imageViewNameCloseIcon.setVisibility(View.GONE);
                textViewName.setVisibility(View.VISIBLE);
                editTextName.setVisibility(View.INVISIBLE);

                // Save
                customer.firstName = NameUtils.getFirstName(editTextName.getText().toString());
                customer.lastName = NameUtils.getLastName(editTextName.getText().toString());
                db.customerDao().update(customer);

                // Refresh updated data
                textViewName.setText(NameUtils.getFullName(customer.firstName, customer.lastName));
                isEditedName = false;
            } else {
                // Edit
                imageViewNameEditIcon.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.ic_baseline_save_24));
                imageViewNameCloseIcon.setVisibility(View.VISIBLE);
                textViewName.setVisibility(View.INVISIBLE);
                editTextName.setVisibility(View.VISIBLE);

                // Set initial data
                if (customer.firstName != null && customer.lastName != null) {
                    editTextName.setText(NameUtils.getFullName(customer.firstName, customer.lastName));
                }
                isEditedName = true;
            }
        });

        imageViewNameCloseIcon.setOnClickListener(v -> {
            AppDatabase db = AppDatabase.getDbInstance(this.getActivity().getApplicationContext());
            int id = ((MyApplication) this.getActivity().getApplication()).getId();
            Customer customer = db.customerDao().findById(id);

            imageViewNameEditIcon.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.ic_baseline_edit_24));
            imageViewNameCloseIcon.setVisibility(View.GONE);
            textViewName.setVisibility(View.VISIBLE);
            editTextName.setVisibility(View.INVISIBLE);

            textViewName.setText(NameUtils.getFullName(customer.firstName, customer.lastName));
            isEditedName = false;
        });

        imageViewAddressEditIcon.setOnClickListener(v -> {
            AppDatabase db = AppDatabase.getDbInstance(this.getActivity().getApplicationContext());
            int id = ((MyApplication) this.getActivity().getApplication()).getId();
            Customer customer = db.customerDao().findById(id);

            if (isEditedAddress) {
                if (!validateAddress()) {
                    return;
                }
                imageViewAddressEditIcon.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.ic_baseline_edit_24));
                imageViewAddressCloseIcon.setVisibility(View.GONE);
                textViewAddress.setVisibility(View.VISIBLE);
                editTextAddress.setVisibility(View.INVISIBLE);

                // Save
                customer.address = editTextAddress.getText().toString();
                db.customerDao().update(customer);

                // Refresh updated data
                textViewAddress.setText(customer.address);
                isEditedAddress = false;
            } else {
                // Edit
                imageViewAddressEditIcon.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.ic_baseline_save_24));
                imageViewAddressCloseIcon.setVisibility(View.VISIBLE);
                textViewAddress.setVisibility(View.INVISIBLE);
                editTextAddress.setVisibility(View.VISIBLE);

                // Set initial data
                editTextAddress.setText(customer.address);
                isEditedAddress = true;
            }
        });

        imageViewAddressCloseIcon.setOnClickListener(v -> {
            AppDatabase db = AppDatabase.getDbInstance(this.getActivity().getApplicationContext());
            int id = ((MyApplication) this.getActivity().getApplication()).getId();
            Customer customer = db.customerDao().findById(id);

            imageViewAddressEditIcon.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.ic_baseline_edit_24));
            imageViewAddressCloseIcon.setVisibility(View.GONE);
            textViewAddress.setVisibility(View.VISIBLE);
            editTextAddress.setVisibility(View.INVISIBLE);

            if (customer.address == null) {
                textViewAddress.setText(EMPTY_FIELD);
            } else {
                textViewAddress.setText(customer.address);
            }
            isEditedAddress = false;
        });
    }

    private boolean validateName() {
        EditText editTextName = getView().findViewById(R.id.editTextName);

        if (editTextName.getText().toString().isEmpty()) {
            Toast.makeText(getActivity(), INVALID_NAME, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (editTextName.getText().toString().split(SPACE).length <= 1) {
            Toast.makeText(getActivity(), INVALID_NAME, Toast.LENGTH_SHORT).show();
            return false;
        }
         return true;
    }


    private boolean validateAddress() {
        EditText editTextAddress = getView().findViewById(R.id.editTextAddress);

        if (editTextAddress.getText().toString().isEmpty()) {
            Toast.makeText(getActivity(), INVALID_ADDRESS, Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            Place place = Autocomplete.getPlaceFromIntent(data);
            editTextAddress.setText(place.getAddress());
        } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
            Status status = Autocomplete.getStatusFromIntent(data);
            Toast.makeText(getActivity().getApplicationContext(), status.getStatusMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}