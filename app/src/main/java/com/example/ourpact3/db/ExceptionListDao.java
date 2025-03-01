package com.example.ourpact3.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ExceptionListDao {
    @Insert
    void insert(ExceptionListEntity... exceptions);

    @Insert
    void insert(List<ExceptionListEntity> exceptions);

    @Delete
    void delete(ExceptionListEntity user);

    @Query("SELECT * FROM exception_list WHERE packageName = :appName")
    ExceptionListEntity getExceptionListByAppName(String appName);

    @Query("SELECT * FROM exception_list")
    List<ExceptionListEntity> getAll();
}
