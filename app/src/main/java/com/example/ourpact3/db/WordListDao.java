package com.example.ourpact3.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface WordListDao {
    @Insert
    void insertWordList(WordListEntity wordList);

    @Query("SELECT * FROM word_list")
    List<WordListEntity> getAllWordLists();

    @Query("SELECT * FROM word_list WHERE id = :id")
    WordListEntity getWordListById(int id);

    @Update
    void updateWordList(WordListEntity wordList);

    @Delete
    void deleteWordList(WordListEntity wordList);
}

