package com.example.ourpact3.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface WordDao
{
    // Insert a single WordListEntity
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(WordEntity... wordEntity);

    // Insert multiple WordListEntity
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertWordLists(List<WordEntity> wordListEntities);

    // Update a single WordListEntity
    @Update
    void updateWordList(WordEntity wordEntity);

    // Delete a single WordListEntity
    @Delete
    void deleteWordList(WordEntity wordEntity);

    // Delete multiple WordListEntity
    @Delete
    void deleteWordLists(List<WordEntity> wordListEntities);

    // Get all WordListEntity
    @Query("SELECT * FROM WordEntity WHERE word_list_id = :word_list_id")
    List<WordEntity> getAllWordsInList(long word_list_id);

    @Query("SELECT * FROM WordEntity")
    List<WordEntity> getAllWords();


    // Get a WordListEntity by id
    @Query("SELECT * FROM WordEntity WHERE id = :id")
    WordEntity getWordListById(int id);

    // Get WordListEntity by language id
    @Query("SELECT * FROM WordEntity WHERE language_id = :languageId")
    List<WordEntity> getWordListsByLanguageId(int languageId);

}
