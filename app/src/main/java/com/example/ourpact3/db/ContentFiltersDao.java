package com.example.ourpact3.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ContentFiltersDao
{
    @Insert
    long insertContentFilter(ContentFilterEntity contentFilter);

    @Query("SELECT * FROM ContentFilterEntity")
    List<ContentFilterEntity> getAllContentFilters();

    @Query("SELECT * FROM ContentFilterEntity WHERE id = :id")
    ContentFilterEntity getContentFilterById(long id);

    @Update
    void updateContentFilter(ContentFilterEntity contentFilter);

    @Delete
    void deleteContentFilter(ContentFilterEntity contentFilters);
}

