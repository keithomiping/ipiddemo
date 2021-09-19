package com.ipid.demo.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.ipid.demo.db.entity.BankAccount;

import java.util.List;

@Dao
public interface BankAccountDao {
    @Query("SELECT * FROM bank_account")
    List<BankAccount> getAllBankAccounts();

    @Query("SELECT * FROM bank_account WHERE id = :id AND status = 1")
    BankAccount findById(int id);

    @Query("SELECT * FROM bank_account WHERE customer_id = :customerId AND preferred = 1 AND status = 1 LIMIT 1")
    BankAccount findPreferredBank(int customerId);

    @Query("SELECT * FROM bank_account WHERE customer_id = :customerId AND status = 1 ORDER BY preferred DESC")
    List<BankAccount> findByCustomerId(int customerId);

    @Query("UPDATE bank_account SET preferred = 0 WHERE customer_id = :customerId")
    void resetPreferredBankAccounts(int customerId);

    @Insert
    void insertBankAccount(BankAccount... bankAccount);

    @Query("UPDATE bank_account SET status = 0 WHERE id = :id")
    void softDeleteBankAccount(int id);

    @Delete
    void delete(BankAccount bankAccount);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update(BankAccount bankAccount);
}