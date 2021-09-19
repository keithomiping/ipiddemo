package com.ipid.demo.db.entity;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "notification",
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
        ),
            @ForeignKey(
                entity = Payment.class,
                parentColumns = "id",
                childColumns = "payment_id"
        )},
        indices = {
            @Index("customer_from"),
            @Index("customer_to"),
            @Index("payment_id")
        }
)
public class Notification {

    @PrimaryKey(autoGenerate = true)
    public int id;

    /**
     * This column will contain the customer ID clicking the request button.
     */
    @ColumnInfo(name = "customer_from")
    public int customerFrom;

    /**
     * This column will contain the customer ID specified in FROM field.
     */
    @ColumnInfo(name = "customer_to")
    public int customerTo;

    @ColumnInfo(name = "payment_id")
    public Integer paymentId;

    @ColumnInfo(name = "type")
    public String type; // RequestType

    @ColumnInfo(name = "description")
    public String description;

    @ColumnInfo(name = "status")
    public boolean status;

    @ColumnInfo(name = "created_date")
    @Nullable
    public String createdDate;

    @ColumnInfo(name = "last_modified_date")
    @Nullable
    public String lastModifiedDate;

    @ColumnInfo(name = "resent")
    public boolean resent;

    @ColumnInfo(name = "pending")
    public boolean pending;
}
