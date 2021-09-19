package com.ipid.demo;

import static com.ipid.demo.constants.Constants.INVALID_MOBILE_NUMBER;
import static com.ipid.demo.constants.Constants.PHONE_NUMBER_ALREADY_EXISTS;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.hbb20.CountryCodePicker;
import com.ipid.demo.db.AppDatabase;
import com.ipid.demo.db.entity.Customer;
import com.ipid.demo.services.impl.ValidationServiceImpl;

public class TransitRegistration extends AppCompatActivity {

    private ValidationServiceImpl validationService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transit_registration);

        validationService = new ValidationServiceImpl();
    }

    private boolean validate() {
        EditText textViewPhoneNumber = findViewById(R.id.textViewPhoneNumber);

        if (textViewPhoneNumber.getText().toString().isEmpty()) {
            Toast.makeText(TransitRegistration.this, INVALID_MOBILE_NUMBER, Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    public void onClickGo(View view) {
        CountryCodePicker ccp = findViewById(R.id.ccp);
        EditText textViewPhoneNumber = findViewById(R.id.textViewPhoneNumber);

        AppDatabase db = AppDatabase.getDbInstance(this.getApplicationContext());

        if (!validate()) {
            return;
        }

        String fullNumber = ccp.getSelectedCountryCodeWithPlus() + " " + textViewPhoneNumber.getText().toString();
        if (validationService.isRegisteredPhoneNumber(db, fullNumber)) {
            Toast.makeText(this, PHONE_NUMBER_ALREADY_EXISTS, Toast.LENGTH_SHORT).show();
        } else {
            updateCustomer(fullNumber);

            // Redirect to Home screen
            startActivity(new Intent(this, MainActivity.class));
        }
    }

    private void updateCustomer(String phoneNumber) {
        AppDatabase db = AppDatabase.getDbInstance(this.getApplicationContext());
        int id = ((MyApplication) this.getApplication()).getId();

        Customer customer = db.customerDao().findById(id);
        customer.phoneNumber = phoneNumber;

        db.customerDao().update(customer);
    }
}