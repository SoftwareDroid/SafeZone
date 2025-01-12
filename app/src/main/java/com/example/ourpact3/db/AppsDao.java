package com.example.ourpact3.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface AppsDao {
    @Insert
    void insertApp(AppsEntity app);

    @Query("SELECT * FROM apps")
    List<AppsEntity> getAllApps();

    @Query("SELECT * FROM apps WHERE package_name = :packageName")
    AppsEntity getAppByPackageName(String packageName);

    @Update
    void updateApp(AppsEntity app);

    @Delete
    void deleteApp(AppsEntity app);
}

