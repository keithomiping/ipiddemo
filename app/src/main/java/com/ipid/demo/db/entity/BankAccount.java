package com.ipid.demo.db.entity;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "bank_account",
        foreignKeys = {
            @ForeignKey(
                entity = Customer.class,
                parentColumns = "id",
                childColumns = "customer_id"
        ),
            @ForeignKey(
                entity = LookupBank.class,
                parentColumns = "id",
                childColumns = "bank_id"
        )},
        indices = {
            @Index("customer_id"),
            @Index("bank_id")
        }
)
public class BankAccount {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "customer_id")
    public int customerId;

    @ColumnInfo(name = "bank_id")
    public int bankId;

    @ColumnInfo(name = "account_name")
    public String accountName;

    @ColumnInfo(name = "account_number")
    public String accountNumber;

    @ColumnInfo(name = "country")
    public String country;

    @ColumnInfo(name = "preferred")
    public boolean preferred;

    @ColumnInfo(name = "status")
    public boolean status;

    @ColumnInfo(name = "created_date")
    @Nullable
    public String createdDate;

    @ColumnInfo(name = "last_modified_date")
    @Nullable
    public String lastModifiedDate;
}
