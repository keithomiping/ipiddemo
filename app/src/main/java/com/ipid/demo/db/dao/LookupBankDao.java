package com.ipid.demo.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.ipid.demo.db.entity.LookupBank;

import java.util.List;

@Dao
public interface LookupBankDao {
    @Query("SELECT * FROM lookup_bank")
    List<LookupBank> getAllBanks();

    @Query("SELECT * FROM lookup_bank WHERE country LIKE :country AND status = 1")
    List<LookupBank> findByCountry(String country);

    @Query("SELECT * FROM lookup_bank WHERE id = :id AND status = 1 LIMIT 1")
    LookupBank findById(int id);

    @Query("SELECT * FROM lookup_bank WHERE name LIKE :name AND status = 1 LIMIT 1")
    LookupBank findByName(String name);

    @Query("SELECT * FROM lookup_bank WHERE code = :code AND status = 1 LIMIT 1")
    LookupBank findByCode(String code);

    @Insert
    void insertBank(LookupBank... banks);

    @Delete
    void delete(LookupBank bank);
}