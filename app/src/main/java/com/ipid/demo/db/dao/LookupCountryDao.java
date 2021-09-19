package com.ipid.demo.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.ipid.demo.db.entity.LookupCountry;

import java.util.List;

@Dao
public interface LookupCountryDao {
    @Query("SELECT * FROM lookup_country")
    List<LookupCountry> getAllCountries();

    @Insert
    void insertCountry(LookupCountry... countries);

    @Delete
    void delete(LookupCountry country);
}
