package com.ipid.demo.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.ipid.demo.db.entity.Notification;

import java.util.List;

@Dao
public interface NotificationDao {
    @Query("SELECT * FROM notification")
    List<Notification> getAllNotifications();

    @Query("SELECT * FROM notification WHERE id = :id AND status = 1 LIMIT 1")
    Notification findById(int id);

    @Query("SELECT * FROM notification WHERE customer_to = :customerId AND status = 1 AND pending = 1 AND resent = 0 ORDER BY id DESC")
    List<Notification> findAllPendingByCustomerId(int customerId);

    @Query("SELECT * FROM notification WHERE customer_to = :customerId AND status = 1 AND pending = 1 AND resent = 0 ORDER BY id DESC LIMIT 1")
    Notification findLatestPendingByCustomerId(int customerId);

    @Query("SELECT * FROM notification WHERE customer_to = :customerId AND status = 1 ORDER BY id DESC")
    List<Notification> findAllByCustomerId(int customerId);

    @Query("SELECT * FROM notification WHERE customer_to = :customerId AND status = 1 ORDER BY id DESC LIMIT 1")
    Notification findLatestByCustomerId(int customerId);

    @Query("SELECT * FROM notification WHERE payment_id = :paymentId AND status = 1")
    List<Notification> findByPaymentId(int paymentId);

    @Query("SELECT * FROM notification WHERE payment_id = :paymentId AND status = 1 AND pending = 1 AND resent = 0")
    List<Notification> findPendingByPaymentId(int paymentId);

    @Insert
    void insertNotification(Notification... notifications);

    @Delete
    void delete(Notification notification);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update(Notification notification);
}
