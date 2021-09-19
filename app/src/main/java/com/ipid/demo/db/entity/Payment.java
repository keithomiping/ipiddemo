package com.ipid.demo.db.entity;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "payment",
        foreignKeys = {
            @ForeignKey(
                entity = Customer.class,
                parentColumns = "id",
                childColumns = "customer_from"
        ),
            @ForeignKey(
                entity = Customer.class,
                parentColumns = "id",
                childColumns = "customer_to"
        )},
        indices = {
                @Index("customer_from"),
                @Index("customer_to")
        }
)
public class Payment {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "reference_number")
    @Nullable
    public String referenceNumber;

    @ColumnInfo(name = "customer_from")
    public int customerFrom;

    @ColumnInfo(name = "customer_to")
    public int customerTo;

    @ColumnInfo(name = "remarks")
    @Nullable
    public String remarks;

    @ColumnInfo(name = "status")
    public boolean status;

    @ColumnInfo(name = "created_date")
    @Nullable
    public String createdDate;

    @ColumnInfo(name = "last_modified_date")
    @Nullable
    public String lastModifiedDate;
}
