package com.example.ourpact3.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface UsageFiltersDao {
    /**
     *
     * @param usageFilters
     * @return the inserted id (the code is automatically generated
     */
    @Insert
    long insert(UsageFiltersEntity usageFilters);

    @Query("SELECT * FROM usage_filters")
    List<UsageFiltersEntity> getAllUsageFilters();

    @Query("SELECT * FROM usage_filters WHERE id = :id")
    UsageFiltersEntity getUsageFilterById(int id);

    @Update
    void updateUsageFilter(UsageFiltersEntity usageFilter);

    @Delete
    void deleteUsageFilter(UsageFiltersEntity usageFilter);

}
