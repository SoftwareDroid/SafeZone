package com.example.ourpact3.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface AppContentFilterDao {
    @Insert
    void insertAppContentFilter(AppContentFilterEntity appContentFilter);

    @Query("SELECT * FROM app_content_filter")
    List<AppContentFilterEntity> getAllAppContentFilters();

    @Query("SELECT * FROM app_content_filter WHERE app_package_name = :appPackageName AND content_filter_id = :contentFilterId")
    AppContentFilterEntity getAppContentFilter(String appPackageName, int contentFilterId);

    @Update
    void updateAppContentFilter(AppContentFilterEntity appContentFilter);

    @Delete
    void deleteAppContentFilter(AppContentFilterEntity appContentFilter);
}
