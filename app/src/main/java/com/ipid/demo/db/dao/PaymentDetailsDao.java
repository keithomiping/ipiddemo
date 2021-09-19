package com.ipid.demo.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.ipid.demo.db.entity.PaymentDetails;

import java.util.List;

@Dao
public interface PaymentDetailsDao {
    @Query("SELECT * FROM payment_details")
    List<PaymentDetails> getAllPaymentDetails();

    @Query("SELECT * FROM payment_details WHERE payment_id = :paymentId LIMIT 1")
    PaymentDetails findByPaymentId(int paymentId);

    @Insert
    void insertPaymentDetails(PaymentDetails... paymentDetails);

    @Delete
    void delete(PaymentDetails paymentDetail);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update(PaymentDetails paymentDetail);
}
