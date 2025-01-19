package com.example.ourpact3.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface AppDao
{
    @Insert
    void insertApp(AppEntity app);

    @Query("SELECT * FROM AppEntity")
    List<AppEntity> getAllApps();

    @Query("SELECT * FROM AppEntity WHERE package_name = :packageName")
    AppEntity getAppByPackageName(String packageName);

    @Update
    void updateApp(AppEntity app);

    @Delete
    void deleteApp(AppEntity app);
}

