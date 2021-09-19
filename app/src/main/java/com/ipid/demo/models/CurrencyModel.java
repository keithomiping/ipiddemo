package com.ipid.demo.models;

public class CurrencyModel {
    private String countryName;
    private int countryFlagImage;
    private String currency;

    public CurrencyModel(String countryName, int countryFlagImage, String currency) {
        this.countryName = countryName;
        this.countryFlagImage = countryFlagImage;
        this.currency = currency;
    }

    public String getCountryName() {
        return countryName;
    }

    public int getCountryFlagImage() {
        return countryFlagImage;
    }

    public String getCurrency() {
        return currency;
    }
}
