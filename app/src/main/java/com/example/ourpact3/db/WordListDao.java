package com.example.ourpact3.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface WordListDao {
    // Insert a single WordListEntity
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertWordList(WordListEntity wordListEntity);

    // Insert multiple WordListEntity
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertWordLists(List<WordListEntity> wordListEntities);

    // Update a single WordListEntity
    @Update
    void updateWordList(WordListEntity wordListEntity);

    // Delete a single WordListEntity
    @Delete
    void deleteWordList(WordListEntity wordListEntity);

    // Delete multiple WordListEntity
    @Delete
    void deleteWordLists(List<WordListEntity> wordListEntities);

    // Get all WordListEntity
    @Query("SELECT * FROM word_list")
    List<WordListEntity> getAllWordLists();

    // Get a WordListEntity by id
    @Query("SELECT * FROM word_list WHERE id = :id")
    WordListEntity getWordListById(int id);

    // Get WordListEntity by language id
    @Query("SELECT * FROM word_list WHERE language_id = :languageId")
    List<WordListEntity> getWordListsByLanguageId(int languageId);

    // Get WordListEntity by topic type and topic id
    @Query("SELECT * FROM word_list WHERE topic_type = :topicType AND topic_id = :topicId")
    List<WordListEntity> getWordListsByTopicTypeAndId(int topicType, int topicId);
}
