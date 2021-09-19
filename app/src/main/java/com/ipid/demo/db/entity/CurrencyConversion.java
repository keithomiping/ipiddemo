package com.ipid.demo.db.entity;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "currency_conversion",
        foreignKeys = {
            @ForeignKey(
                entity = LookupCurrency.class,
                parentColumns = "id",
                childColumns = "currency_from"
        ),
            @ForeignKey(
                entity = LookupCurrency.class,
                parentColumns = "id",
                childColumns = "currency_to"
        )},
        indices = {
                @Index("currency_from"),
                @Index("currency_to")
        }
)
public class CurrencyConversion {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "currency_from")
    public int currencyFrom;

    @ColumnInfo(name = "currency_to")
    public int currencyTo;

    @ColumnInfo(name = "value")
    public double value;

    @ColumnInfo(name = "status")
    public boolean status;

    @ColumnInfo(name = "created_date")
    @Nullable
    public String createdDate;

    @ColumnInfo(name = "last_modified_date")
    @Nullable
    public String lastModifiedDate;
}
