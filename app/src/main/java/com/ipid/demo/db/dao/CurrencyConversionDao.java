package com.ipid.demo.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.ipid.demo.db.entity.CurrencyConversion;

import java.util.List;

@Dao
public interface CurrencyConversionDao {
    @Query("SELECT * FROM currency_conversion")
    List<CurrencyConversion> getAllCurrencyConversions();

    @Query("SELECT * FROM currency_conversion WHERE currency_from = :currencyFrom AND currency_to = :currencyTo AND status = 1 LIMIT 1")
    CurrencyConversion findByCurrencyFromTo(int currencyFrom, int currencyTo);

    @Insert
    void insertCurrencyConversion(CurrencyConversion... currencyConversions);

    @Delete
    void delete(CurrencyConversion currencyConversion);
}
