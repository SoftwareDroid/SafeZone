package com.example.ourpact3.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface AppDao {

    @Insert
    void insert(AppInfo appInfo);

    @Update
    void update(AppInfo appInfo);

    @Delete
    void delete(AppInfo appInfo);

    @Query("SELECT * FROM app_info")
    List<AppInfo> getAllPackages();

    @Query("SELECT * FROM app_info WHERE packageId = :packageId")
    AppInfo getPackageById(String packageId);
}
