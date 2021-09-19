package com.ipid.demo.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.ipid.demo.db.entity.Payment;

import java.util.List;

@Dao
public interface PaymentDao {
    @Query("SELECT * FROM payment")
    List<Payment> getAllPayments();

    @Query("SELECT * FROM payment WHERE id = :id AND status = 1 LIMIT 1")
    Payment findById(int id);

    @Query("SELECT * FROM payment WHERE customer_to = :customerId AND status = 1")
    List<Payment> findByCustomerId(int customerId);

    @Query("SELECT * FROM payment WHERE customer_from = :customerId AND status = 1 ORDER BY id DESC LIMIT 1")
    Payment findLatestPaymentByCustomer(int customerId);

    @Insert
    void insertPayment(Payment... payments);

    @Delete
    void delete(Payment payment);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update(Payment payment);
}