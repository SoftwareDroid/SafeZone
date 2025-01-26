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
    long insertContentFilter(ContentFiltersEntity contentFilter);

    @Query("SELECT * FROM content_filters")
    List<ContentFiltersEntity> getAllContentFilters();

    @Query("SELECT * FROM content_filters WHERE id = :id")
    ContentFiltersEntity getContentFilterById(int id);

    @Update
    void updateContentFilter(ContentFiltersEntity contentFilter);

    @Delete
    void deleteContentFilter(ContentFiltersEntity contentFilters);
}

