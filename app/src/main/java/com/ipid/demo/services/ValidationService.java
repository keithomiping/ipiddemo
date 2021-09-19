package com.ipid.demo.services;

import com.ipid.demo.db.AppDatabase;

public interface ValidationService {

    /**
     * Validates if the user given first name, last name and phone number exists in the system
     *
     * @param db
     * @param firstName
     * @param lastName
     * @param phoneNumber
     * @return
     */
    public boolean isRegisteredUser(AppDatabase db, String firstName, String lastName, String phoneNumber);

    /**
     * Validates if the user given first name and last name exists in the system
     *
     * @param db
     * @param firstName
     * @param lastName
     * @return
     */
    public boolean isRegisteredUser(AppDatabase db, String firstName, String lastName);

    public boolean isRegisteredPhoneNumber(AppDatabase db, String phoneNumber);

    public boolean isRegisteredEmail(AppDatabase db, String email);
}
