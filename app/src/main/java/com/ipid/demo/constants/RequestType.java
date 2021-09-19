package com.ipid.demo.constants;

import android.app.DownloadManager;

public enum RequestType {
    PAY("Pay"),
    GET_PAID("Get paid"),
    INVITATION("Invitation");

    private String displayName;

    RequestType(String displayName) {
        this.displayName = displayName;
    }

    public String displayName() { return displayName; }

    // Optionally and/or additionally, toString.
    @Override public String toString() { return displayName; }
}

