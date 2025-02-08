package com.example.ourpact3.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ContentFilterToAppDao
{

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ContentFilterToAppEntity contentFilterToAppEntity);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<ContentFilterToAppEntity> contentFilterToAppInfoEntities);

    @Update
    void update(ContentFilterToAppEntity contentFilterToAppEntity);

    @Delete
    void delete(ContentFilterToAppEntity contentFilterToAppEntity);

    @Query("SELECT * FROM ContentFilterToAppEntity")
    List<ContentFilterToAppEntity> getAll();
    /*
    Priority 0 is always System
     */
    @Query("SELECT * FROM ContentFilterToAppEntity WHERE priority = :priority")
    ContentFilterToAppEntity getByPriority(int priority);


    @Query("SELECT * FROM ContentFilterToAppEntity WHERE id = :id")
    ContentFilterToAppEntity getById(int id);

    @Query("SELECT * FROM ContentFilterToAppEntity WHERE content_filter_id = :contentFilterID")
    List<ContentFilterToAppEntity> getByContentFilterID(long contentFilterID);

    @Query("SELECT * FROM ContentFilterToAppEntity WHERE package_name = :packageName ORDER BY priority ASC")
    List<ContentFilterToAppEntity> getByPackageName(String packageName);

    @Query("DELETE FROM ContentFilterToAppEntity")
    void deleteAll();
}

