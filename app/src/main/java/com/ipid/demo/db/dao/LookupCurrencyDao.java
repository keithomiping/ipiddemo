package com.ipid.demo.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.ipid.demo.db.entity.LookupCurrency;

import java.util.List;

@Dao
public interface LookupCurrencyDao {
    @Query("SELECT * FROM lookup_currency")
    List<LookupCurrency> getAllCurrencies();

    @Query("SELECT * FROM lookup_currency WHERE id = :id AND status = 1 LIMIT 1")
    LookupCurrency findById(int id);

    @Query("SELECT * FROM lookup_currency WHERE description LIKE :currency AND status = 1 LIMIT 1")
    LookupCurrency findByCurrency(String currency);

    @Query("SELECT * FROM lookup_currency WHERE country LIKE :country AND status = 1 LIMIT 1")
    LookupCurrency findByCountry(String country);

    @Insert
    void insertCurrency(LookupCurrency... currencies);

    @Delete
    void delete(LookupCurrency lookupCurrency);

    @Query("DELETE FROM lookup_currency")
    void deleteTable();
}
