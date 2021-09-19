package com.ipid.demo.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.ipid.demo.db.entity.Customer;

@Dao
public interface CustomerDao {

    @Query("SELECT * FROM customer WHERE email_address LIKE :email LIMIT 1")
    Customer findByEmail(String email);

    @Query("SELECT * FROM customer WHERE (REPLACE(phone_number, ' ', '') = REPLACE(:phoneNumber, ' ', '')) AND customer_id IS NULL LIMIT 1")
    Customer findNonMemberByPhone(String phoneNumber);

    @Query("SELECT * FROM customer WHERE (REPLACE(phone_number, ' ', '') = REPLACE(:phoneNumber, ' ', '')) LIMIT 1")
    Customer findByPhoneNumber(String phoneNumber);

    @Query("SELECT * FROM customer WHERE email_address LIKE :email AND password LIKE :password LIMIT 1")
    Customer findByEmailAndPassword(String email, String password);

    @Query("SELECT * FROM customer WHERE first_name LIKE :firstName AND last_name LIKE :lastName LIMIT 1")
    Customer findByFirstLastName(String firstName, String lastName);

    @Query("SELECT * FROM customer WHERE first_name LIKE :firstName AND last_name LIKE :lastName AND phone_number LIKE :phoneNumber LIMIT 1")
    Customer findByFirstLastNameAndPhone(String firstName, String lastName, String phoneNumber);

    @Query("SELECT * FROM customer WHERE id = :id LIMIT 1")
    Customer findById(int id);

    @Insert
    void insertCustomer(Customer... customers);

    @Delete
    void delete(Customer customer);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update(Customer customer);
}
