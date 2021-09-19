package com.ipid.demo.db.entity;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "customer")
public class Customer {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "customer_id")
    public String customerId;

    @ColumnInfo(name = "first_name")
    @Nullable
    public String firstName;

    @ColumnInfo(name = "last_name")
    @Nullable
    public String lastName;

    @ColumnInfo(name = "address")
    @Nullable
    public String address;

    @ColumnInfo(name = "phone_number")
    @Nullable
    public String phoneNumber;

    @ColumnInfo(name = "email_address")
    public String emailAddress;

    @ColumnInfo(name = "password")
    public String password;

    @ColumnInfo(name = "status")
    public boolean status;

    @ColumnInfo(name = "created_date")
    @Nullable
    public String createdDate;

    @ColumnInfo(name = "last_modified_date")
    @Nullable
    public String lastModifiedDate;
}
