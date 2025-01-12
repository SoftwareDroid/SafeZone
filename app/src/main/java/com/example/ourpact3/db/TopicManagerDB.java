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
        DatabaseManager.db.beginTransaction();
        try
        {
            if (topic.database_id != null)
            {
                long topicType = topic.getTopicType();
                String tableName = getTableName((int) topicType);
                DatabaseManager.db.delete(tableName, "id = ?", new String[]{String.valueOf(topic.database_id)});
                long topicID = topic.database_id;
                long deletedRows = DatabaseManager.db.delete("word_list", "topic_id = ? AND topic_type = ?", new String[]{String.valueOf(topicID), String.valueOf(topic.getTopicType())});
                DatabaseManager.db.setTransactionSuccessful();
            }

        } catch (Exception e)
        {
            // Handle any exceptions
        } finally
        {
            // End the transaction
            DatabaseManager.db.endTransaction();
        }

    }

    public static void createOrUpdateTopic(Topic topic)
    {
        if (topic == null)
        {
            throw new NullPointerException("Topic cannot be null");
        }

        DatabaseManager.db.beginTransaction();
        try
        {
            if (topic.database_id != null)
            {
                // Update the topic if it already exists
                updateTopic(topic);
            }
            else
            {
                // Insert the topic if it doesn't exist
                insertTopic(topic);
            }
            DatabaseManager.db.setTransactionSuccessful();
        } catch (Exception e)
        {
//            Log.e("TopicManagerDB", "Error creating or updating topic", e);
        } finally
        {
            DatabaseManager.db.endTransaction();
        }
    }

    private static void updateTopic(Topic topic)
    {
        ContentValues values = new ContentValues();
        values.put("name", topic.getTopicName());
        if (topic.getTopicType() == TOPIC_TYPE_SCORED)
        {
            values.put("lower_case_topic", topic.isLowerCaseTopic());
        }
        String tableName = getTableName(topic.getTopicType());
        DatabaseManager.db.update(tableName, values, "id = ?", new String[]{String.valueOf(topic.database_id)});

        deleteExistingWordEntries(topic);
        insertNewWordEntries(topic);
    }

    private static void insertTopic(Topic topic)
    {
        ContentValues values = new ContentValues();
        values.put("name", topic.getTopicName());
        if (topic.getTopicType() == TOPIC_TYPE_SCORED)
        {
            values.put("lower_case_topic", topic.isLowerCaseTopic());
        }
        String tableName = getTableName(topic.getTopicType());
        long newTopicID = DatabaseManager.db.insert(tableName, null, values);
        topic.database_id = newTopicID;

        insertNewWordEntries(topic);
    }

    private static String getTableName(int topicType)
    {
        return topicType == TOPIC_TYPE_SCORED ? topicTableScored : topicTableExact;
    }

    private static void deleteExistingWordEntries(Topic topic)
    {
        DatabaseManager.db.delete("word_list", "topic_id = ? AND topic_type = ?", new String[]{String.valueOf(topic.database_id), String.valueOf(topic.getTopicType())});
    }

    private static void insertNewWordEntries(Topic topic)
    {
        for (Topic.ScoredWordEntry entry : topic.getScoredWords())
        {
            ContentValues valuesForWord = new ContentValues();
            valuesForWord.put("text", entry.word);
            valuesForWord.put("language_id", entry.languageId);
            valuesForWord.put("is_regex", entry.isRegex ? 1 : 0);
            valuesForWord.put("topic_id", topic.database_id);
            valuesForWord.put("topic_type", topic.getTopicType());
            long newWordId = DatabaseManager.db.insert("word_list", null, valuesForWord);

            if (topic.getTopicType() == TOPIC_TYPE_SCORED)
            {
                ContentValues valuesForScoring = new ContentValues();
                valuesForScoring.put("word_id", newWordId);
                valuesForScoring.put("read", entry.read);
                valuesForScoring.put("write", entry.write);
                long scoringID = DatabaseManager.db.insert("word_scores", null, valuesForScoring);
            }
        }
    }


    //
    public static ArrayList<Topic> getAllScoredTopics()
    {
        return getAllTopics(TOPIC_TYPE_SCORED);
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
    public static ArrayList<Topic.ScoredWordEntry> getWordsForTopic(long topicId, int topicType)
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

    @SuppressLint("Range")
    private static Topic getTopicFromCursor(Cursor cursor, int topicType)
    {
        long id = cursor.getInt(cursor.getColumnIndex("id"));
        String name = cursor.getString(cursor.getColumnIndex("name"));
        Topic topic = new Topic(name);
        topic.database_id = id;
        if (topicType == TOPIC_TYPE_SCORED)
        {
            topic.setLowerCaseTopic(cursor.getInt(cursor.getColumnIndex("lower_case_topic")) == 1);
        }
        return topic;
    }

    public static ArrayList<Topic> getAllTopics(int topicType)
    {
        ArrayList<Topic> topics = new ArrayList<>();
        String tableName = getTableName(topicType);
        String query = "SELECT * FROM " + tableName;
        Cursor cursor = DatabaseManager.db.rawQuery(query, null);
        try
        {
            if (cursor.moveToFirst())
            {
                do
                {
                    Topic topic = getTopicFromCursor(cursor, topicType);
                    // Get words for this topic
                    ArrayList<Topic.ScoredWordEntry> words = getWordsForTopic(topic.getDatabase_id(), topicType);
                    topic.setScoredWords(words);
                    topics.add(topic);
                } while (cursor.moveToNext());
            }
        } finally
        {
            cursor.close();
        }
        return topics;
    }

    public static Topic getTopicById(long topicId, int topicType)
    {
        String tableName = getTableName(topicType);
        String query = "SELECT * FROM " + tableName + " WHERE id = ?";
        Cursor cursor = DatabaseManager.db.rawQuery(query, new String[]{String.valueOf(topicId)});
        try
        {
            if (cursor.moveToFirst())
            {
                Topic topic = getTopicFromCursor(cursor, topicType);
                // Get words for this topic
                ArrayList<Topic.ScoredWordEntry> words = getWordsForTopic(topic.getDatabase_id(), topicType);
                topic.setScoredWords(words);
                return topic;
            }
        } finally
        {
            cursor.close();
        }
        return null;
    }

}
