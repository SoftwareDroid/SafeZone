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
    /**
     * overwrites all time restrictions for an app which has exactly use usage_filter_id
     */
    public static void setScoredTopics(ArrayList<Topic> allTopics)
    {
        // Start a transaction for safety
        DatabaseManager.db.beginTransaction();
        try
        {
            // Step 1: clear topic
            DatabaseManager.db.delete(topicTableScored, null, null);
            // Step 2: Insert new rows from the array
            for (Topic topic : allTopics)
            {
                ContentValues values = new ContentValues();
                // Ids are auto generated here
                values.put("name", topic.getTopicName());
                values.put("lower_case_topic", topic.isLowerCaseTopic());
                // Insert the new topic row
                long newTopicID = DatabaseManager.db.insert(topicTableScored, null, values);
                // insert all entries
                for (Topic.ScoredWordEntry entry : topic.getScoredWords())
                {
                    ContentValues valuesForWord = new ContentValues();
                    values.put("text", entry.word);
                    values.put("language_id", entry.languageId);
                    values.put("is_regex", entry.isRegex ? 1 : 0);
                    values.put("language_id", entry.languageId);
                    values.put("topic_id", newTopicID);
                    values.put("topic_type",TOPIC_TYPE_SCORED);
                    long newWordId = DatabaseManager.db.insert("word_list", null, valuesForWord);
                    // add scoring
                    ContentValues valuesForScoring = new ContentValues();
                    values.put("word_id", newWordId);
                    values.put("read", entry.read);
                    values.put("write", entry.write);
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
    public static ArrayList<Topic> getAllTopics(int topicType) {
        ArrayList<Topic> topics = new ArrayList<>();
        String tableName = topicType == TOPIC_TYPE_SCORED ? topicTableScored : topicTableExact;
        String query = "SELECT * FROM " + tableName;
        Cursor cursor = DatabaseManager.db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                long id = cursor.getInt(0);
                String name = cursor.getString(1);
                Topic topic = new Topic(name);
                topic.database_id = id;
                if (topicType == TOPIC_TYPE_SCORED) {
                    topic.setLowerCaseTopic(cursor.getInt(2) == 1);
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

    public static Topic getTopicById(int topicId, int topicType) {
        String tableName = topicType == TOPIC_TYPE_SCORED ? topicTableScored : topicTableExact;
        String query = "SELECT * FROM " + tableName + " WHERE id = ?";
        Cursor cursor = DatabaseManager.db.rawQuery(query, new String[]{String.valueOf(topicId)});
        if (cursor.moveToFirst()) {
            long id = cursor.getInt(0);
            String name = cursor.getString(1);
            Topic topic = new Topic(name);
            topic.database_id = id;
            if (topicType == TOPIC_TYPE_SCORED) {
                topic.setLowerCaseTopic(cursor.getInt(2) == 1);
            }
            // Get words for this topic
            ArrayList<Topic.ScoredWordEntry> words = getWordsForTopic(topic.getDatabase_id(), topicType);
            topic.setScoredWords(words);
            cursor.close();
            return topic;
        }
        cursor.close();
        return null;
    }

    private static ArrayList<Topic.ScoredWordEntry> getWordsForTopic(long topicId, int topicType) {
        ArrayList<Topic.ScoredWordEntry> words = new ArrayList<>();
        String query = "SELECT wl.id, wl.text, wl.language_id, wl.is_regex, ws.read, ws.write " +
                "FROM word_list wl " +
                "JOIN word_scores ws ON wl.id = ws.word_id " +
                "WHERE wl.topic_id = ? AND wl.topic_type = ?";
        Cursor cursor = DatabaseManager.db.rawQuery(query, new String[]{String.valueOf(topicId), String.valueOf(topicType)});
        if (cursor.moveToFirst()) {
            do {
                Topic.ScoredWordEntry word = new Topic.ScoredWordEntry();
                word.id = cursor.getInt(0);
                word.word = cursor.getString(1);
                word.languageId = cursor.getInt(2);
                word.isRegex = cursor.getInt(3) == 1;
                word.read = cursor.getInt(4);
                word.write = cursor.getInt(5);
                words.add(word);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return words;
    }


}
