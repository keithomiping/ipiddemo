package com.ipid.demo.services.impl;

import com.ipid.demo.db.AppDatabase;
import com.ipid.demo.db.entity.Customer;
import com.ipid.demo.services.ValidationService;

public class ValidationServiceImpl implements ValidationService {

    @Override
    public boolean isRegisteredUser(AppDatabase db, String firstName, String lastName, String phoneNumber) {
        Customer customer = db.customerDao().findByFirstLastNameAndPhone(firstName, lastName, phoneNumber);

        if (customer != null) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean isRegisteredUser(AppDatabase db, String firstName, String lastName) {
        Customer customer = db.customerDao().findByFirstLastName(firstName, lastName);

        if (customer != null) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean isRegisteredPhoneNumber(AppDatabase db, String phoneNumber) {
        Customer customer = db.customerDao().findByPhoneNumber(phoneNumber);

        if (customer != null && customer.customerId != null) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean isRegisteredEmail(AppDatabase db, String email) {
        Customer customer = db.customerDao().findByEmail(email);

        if (customer != null && customer.customerId != null) {
            return true;
        } else {
            return false;
        }
    }
}
