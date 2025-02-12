package com.example.ourpact3.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface LanguageDao
{
    @Insert
    long insertLanguage(LanguageEntity language);

    @Query("SELECT * FROM LanguageEntity")
    List<LanguageEntity> getAllLanguages();

    @Query("SELECT * FROM LanguageEntity WHERE id = :id")
    LanguageEntity getLanguageById(long id);

    @Query("SELECT * FROM LanguageEntity WHERE longLanguageCode = :longLanguageCode")
    LanguageEntity getLanguageByLongCode(String longLanguageCode);

    @Query("SELECT * FROM LanguageEntity WHERE shortLanguageCode = :shortLanguageCode")
    LanguageEntity getLanguageByShortCode(String shortLanguageCode);

    @Update
    void updateLanguage(LanguageEntity language);

    @Delete
    void deleteLanguage(LanguageEntity language);
}

