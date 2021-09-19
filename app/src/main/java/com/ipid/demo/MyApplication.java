package com.ipid.demo;

import android.app.Application;

public class MyApplication extends Application {

    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}