package com.example.ourpact3.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface WordListDao {
    @Insert
    long insert(WordListEntity wordList);

    @Insert
    long[] insertAll(WordListEntity... wordLists);

    @Delete
    void delete(WordListEntity wordList);

    @Query("SELECT * FROM WordListEntity")
    List<WordListEntity> getAllWordLists();

    @Query("SELECT * FROM WordListEntity WHERE id = :id")
    WordListEntity getWordListById(int id);

    @Query("SELECT * FROM WordListEntity WHERE name = :name")
    WordListEntity getWordListByName(String name);
}

