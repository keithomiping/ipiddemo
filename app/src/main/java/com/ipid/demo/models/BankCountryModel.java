package com.ipid.demo.models;

public class BankCountryModel {
    private String bankName;
    private int bankAccountId;
    private int countryFlagImage;

    public BankCountryModel(String bankName, int bankAccountId, int countryFlagImage) {
        this.bankName = bankName;
        this.bankAccountId = bankAccountId;
        this.countryFlagImage = countryFlagImage;
    }

    public String getBankName() {
        return bankName;
    }

    public int getBankAccountId() {
        return bankAccountId;
    }

    public int getCountryFlagImage() {
        return countryFlagImage;
    }
}
