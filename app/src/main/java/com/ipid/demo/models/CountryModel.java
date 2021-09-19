package com.ipid.demo.models;

public class CountryModel {
    private String countryName;
    private int countryFlagImage;

    public CountryModel(String countryName, int countryFlagImage) {
        this.countryName = countryName;
        this.countryFlagImage = countryFlagImage;
    }

    public String getCountryName() {
        return countryName;
    }

    public int getCountryFlagImage() {
        return countryFlagImage;
    }
}
