package com.ipid.demo.db.entity;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "payment_details",
        foreignKeys = {
            @ForeignKey(
                entity = BankAccount.class,
                parentColumns = "id",
                childColumns = "bank_account_from"
        ),
            @ForeignKey(
                entity = LookupCurrency.class,
                parentColumns = "id",
                childColumns = "currency_from"
        )},
        indices = {
            @Index("bank_account_from"),
            @Index("currency_from")
        }
)
public class PaymentDetails {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "payment_id")
    public int paymentId;

    @ColumnInfo(name = "bank_account_from")
    @Nullable
    public int bankAccountFrom;

    @ColumnInfo(name = "bank_account_to")
    @Nullable
    public int bankAccountTo;

    @ColumnInfo(name = "currency_from")
    @Nullable
    public int currencyFrom;

    @ColumnInfo(name = "currency_to")
    @Nullable
    public int currencyTo;

    @ColumnInfo(name = "amount_from")
    @Nullable
    public double amountFrom;

    @ColumnInfo(name = "amount_to")
    @Nullable
    public double amountTo;

    @ColumnInfo(name = "exchange_rate")
    @Nullable
    public double exchangeRate;

    @ColumnInfo(name = "fees")
    @Nullable
    public double fees;
}