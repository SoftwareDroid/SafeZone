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
    void insert(ExceptionListEntity exceptionList);

    @Insert
    void insertAll(List<ExceptionListEntity> exceptionLists);

    @Query("SELECT * FROM exception_list")
    List<ExceptionListEntity> getAll();

    @Query("SELECT * FROM exception_list WHERE appName = :appName LIMIT 1")
    ExceptionListEntity getByAppName(String appName);

    @Update
    void update(ExceptionListEntity exceptionList);

    @Delete
    void delete(ExceptionListEntity exceptionList);

    @Query("DELETE FROM exception_list WHERE appName = :appName")
    void deleteByAppName(String appName);

    @Query("SELECT * FROM exception_list WHERE readable = :readable")
    List<ExceptionListEntity> getAllByReadable(boolean readable);

    // Add more queries as needed
}

