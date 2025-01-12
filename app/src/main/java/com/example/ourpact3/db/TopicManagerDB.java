package com.example.ourpact3.db;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;

import com.example.ourpact3.model.PipelineButtonAction;
import com.example.ourpact3.model.PipelineWindowAction;
import com.example.ourpact3.pipeline.CounterAction;
import com.example.ourpact3.smart_filter.ProductivityTimeRule;
import com.example.ourpact3.smart_filter.UsageRestrictionsFilter;
import com.example.ourpact3.topics.Topic;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.EnumSet;

public class TopicManagerDB
{
    public static final int TOPIC_TYPE_SCORED = 1;
    public static final int TOPIC_TYPE_EXACT = 0;
    public static final String topicTableScored = "topic_scored";
    public static final String topicTableExact = "topic_exact";

    public static void deleteTopic(Topic topic)
    {
        if (topic.database_id != null)
        {
            DatabaseManager.db.beginTransaction();

            long topicType = topic.getTopicType();
            String tableName = topicType == TOPIC_TYPE_SCORED ? topicTableScored : topicTableExact;
            DatabaseManager.db.delete(tableName, "id = ?", new String[]{String.valueOf(topic.database_id)});
            long deletedRows = DatabaseManager.db.delete("word_list", "topic_id = ? AND topic_type = ?", new String[]{String.valueOf(topic.database_id), String.valueOf(topic.getTopicType())});
            DatabaseManager.db.endTransaction();

        }

    }

    /**
     * Add or updates a topic
     *
     * @param topic
     */
    public static void createOrUpdateTopic(Topic topic)
    {
        // Start a transaction for safety
        DatabaseManager.db.beginTransaction();
        try
        {
            // Step 1: clear existing topic if it has a database id
            deleteTopic(topic);
            // Insert new topic row
            ContentValues values = new ContentValues();
            values.put("name", topic.getTopicName());
            if (topic.getTopicType() == TOPIC_TYPE_SCORED)
            {
                values.put("lower_case_topic", topic.isLowerCaseTopic());
            }
            String tableName = topic.getTopicType() == TOPIC_TYPE_SCORED ? topicTableScored : topicTableExact;
            long newTopicID = DatabaseManager.db.insert(tableName, null, values);
            // reflect new id out
            topic.database_id = newTopicID;

            // Delete existing word entries for the topic
            DatabaseManager.db.delete("word_list", "topic_id = ? AND topic_type = ?", new String[]{String.valueOf(topic.database_id), String.valueOf(topic.getTopicType())});
            String query = "SELECT COUNT(*) FROM word_list";
            Cursor cursor = DatabaseManager.db.rawQuery(query, null);
            cursor.moveToFirst();
            int rowCount = cursor.getInt(0);
            // Insert new word entries

            for (Topic.ScoredWordEntry entry : topic.getScoredWords())
            {
                ContentValues valuesForWord = new ContentValues();
                valuesForWord.put("text", entry.word);
                valuesForWord.put("language_id", entry.languageId);
                valuesForWord.put("is_regex", entry.isRegex ? 1 : 0);
                valuesForWord.put("topic_id", newTopicID);
                valuesForWord.put("topic_type", topic.getTopicType());
                long newWordId = DatabaseManager.db.insert("word_list", null, valuesForWord);

                if (topic.getTopicType() == TOPIC_TYPE_SCORED)
                {
                    // Insert scoring
                    ContentValues valuesForScoring = new ContentValues();
                    valuesForScoring.put("word_id", newWordId);
                    valuesForScoring.put("read", entry.read);
                    valuesForScoring.put("write", entry.write);
                    long scoringID = DatabaseManager.db.insert("word_scores", null, valuesForScoring);
                }
            }

            // Mark the transaction as successful
            DatabaseManager.db.setTransactionSuccessful();
        } catch (Exception e)
        {
            // Handle any exceptions
        } finally
        {
            // End the transaction
            DatabaseManager.db.endTransaction();
        }
    }


    //
    public static ArrayList<Topic> getAllScoredTopics()
    {
        return getAllTopics(TOPIC_TYPE_SCORED);
    }

    @SuppressLint("Range")
    private static ArrayList<Topic> getAllTopics(int topicType)
    {
        ArrayList<Topic> topics = new ArrayList<>();
        String tableName = topicType == TOPIC_TYPE_SCORED ? topicTableScored : topicTableExact;
        String query = "SELECT * FROM " + tableName;
        Cursor cursor = DatabaseManager.db.rawQuery(query, null);
        if (cursor.moveToFirst())
        {
            do
            {
                long id = cursor.getInt(cursor.getColumnIndex("id"));
                String name = cursor.getString(cursor.getColumnIndex("name"));
                Topic topic = new Topic(name);
                topic.database_id = id;
                if (topicType == TOPIC_TYPE_SCORED)
                {
                    topic.setLowerCaseTopic(cursor.getInt(cursor.getColumnIndex("lower_case_topic")) == 1);
                }
                // Get words for this topic
                ArrayList<Topic.ScoredWordEntry> words = getWordsForTopic(topic.getDatabase_id(), topicType);
                topic.setScoredWords(words);
                topics.add(topic);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return topics;
    }

    public static Topic getScoredTopicById(long topicId)
    {
        return getTopicById(topicId, TOPIC_TYPE_SCORED);
    }

    public static Topic getExactTopicById(long topicId)
    {
        return getTopicById(topicId, TOPIC_TYPE_EXACT);
    }

    @SuppressLint("Range")
    private static Topic getTopicById(long topicId, int topicType)
    {
        String tableName = topicType == TOPIC_TYPE_SCORED ? topicTableScored : topicTableExact;
        String query = "SELECT * FROM " + tableName + " WHERE id = ?";
        Cursor cursor = DatabaseManager.db.rawQuery(query, new String[]{String.valueOf(topicId)});
        if (cursor.moveToFirst())
        {
            long id = cursor.getInt(cursor.getColumnIndex("id"));
            String name = cursor.getString(cursor.getColumnIndex("name"));
            Topic topic = new Topic(name);
            topic.database_id = id;
            if (topicType == TOPIC_TYPE_SCORED)
            {
                topic.setLowerCaseTopic(cursor.getInt(cursor.getColumnIndex("lower_case_topic")) == 1);
                // Get words for this topic
                ArrayList<Topic.ScoredWordEntry> words = getWordsForTopic(topic.getDatabase_id(), topicType);
                topic.setScoredWords(words);
            } else
            {
                //TODO:
            }

            cursor.close();
            return topic;
        }
        cursor.close();
        return null;
    }

    @SuppressLint("Range")
    private static ArrayList<Topic.ScoredWordEntry> getWordsForTopic(long topicId, int topicType)
    {
        ArrayList<Topic.ScoredWordEntry> words = new ArrayList<>();
        String query = "SELECT wl.id, wl.text, wl.language_id, wl.is_regex, ws.read, ws.write " +
                "FROM word_list wl " +
                "JOIN word_scores ws ON wl.id = ws.word_id " +
                "WHERE wl.topic_id = ? AND wl.topic_type = ?";
        Cursor cursor = DatabaseManager.db.rawQuery(query, new String[]{String.valueOf(topicId), String.valueOf(topicType)});
        if (cursor.moveToFirst())
        {
            do
            {
                Topic.ScoredWordEntry word = new Topic.ScoredWordEntry();
                word.id = cursor.getInt(cursor.getColumnIndex("id"));
                word.word = cursor.getString(cursor.getColumnIndex("text"));
                word.languageId = cursor.getInt(cursor.getColumnIndex("language_id"));
                word.isRegex = cursor.getInt(cursor.getColumnIndex("is_regex")) == 1;
                word.read = cursor.getInt(cursor.getColumnIndex("read"));
                word.write = cursor.getInt(cursor.getColumnIndex("write"));
                words.add(word);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return words;
    }


}
