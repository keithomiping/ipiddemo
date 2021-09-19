package com.ipid.demo.models;

public class BankModel {

    private boolean isSelected;
    private int bankId; // Bank Account ID
    private String bankName;
    private String bankNumber;
    private int countryFlagImage;
    private boolean showManageIcons; // Edit, Delete
    private boolean isPreferred;
    private boolean isEdited;

    public BankModel(boolean isSelected, int bankId, String bankName, String bankNumber, int countryFlagImage, boolean showManageIcons, boolean isPreferred) {
        this.isSelected = isSelected;
        this.bankId = bankId;
        this.bankName = bankName;
        this.bankNumber = bankNumber;
        this.countryFlagImage = countryFlagImage;
        this.showManageIcons = showManageIcons;
        this.isPreferred = isPreferred;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public int getBankId() {
        return bankId;
    }

    public void setBankId(int bankId) {
        this.bankId = bankId;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankNumber() {
        return bankNumber;
    }

    public void setBankNumber(String bankNumber) {
        this.bankNumber = bankNumber;
    }

    public int getCountryFlagImage() {
        return countryFlagImage;
    }

    public void setCountryFlagImage(int countryFlagImage) {
        this.countryFlagImage = countryFlagImage;
    }

    public boolean isShowManageIcons() {
        return showManageIcons;
    }

    public void setShowManageIcons(boolean showManageIcons) {
        this.showManageIcons = showManageIcons;
    }

    public boolean isPreferred() {
        return isPreferred;
    }

    public void setPreferred(boolean preferred) {
        isPreferred = preferred;
    }

    public boolean isEdited() {
        return isEdited;
    }

    public void setEdited(boolean edited) {
        isEdited = edited;
    }
}
