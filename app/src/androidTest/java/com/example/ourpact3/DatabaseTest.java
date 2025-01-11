package com.example.ourpact3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.ourpact3.db.DatabaseHelper;
import com.example.ourpact3.db.DatabaseManager;
import com.example.ourpact3.db.TopicManagerDB;
import com.example.ourpact3.topics.Topic;

import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;


/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class DatabaseTest
{

    public Topic.ScoredWordEntry createSampleScoredWord(String word)
    {
        Topic.ScoredWordEntry entry = new Topic.ScoredWordEntry();
        entry.read = 10;
        entry.write = 10;
        entry.word = word;
        entry.languageId = 0;
        entry.isRegex = false;
        return entry;

    }

    @Test
    public void testSetManyTopics() throws JSONException
    {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        Topic testTopic1 = new Topic("NSFW");
        ArrayList<Topic.ScoredWordEntry> words = new ArrayList<>();
        // Create word

        words.add(createSampleScoredWord("girl"));
        words.add(createSampleScoredWord("ass"));
        testTopic1.setScoredWords(words);


        Topic testTopic2 = new Topic("animals");
        // Create word
        ArrayList<Topic.ScoredWordEntry> words2 = new ArrayList<>();

        words2.add(createSampleScoredWord("dog"));
        words2.add(createSampleScoredWord("catfish"));
        testTopic2.setScoredWords(words2);

        ArrayList<Topic> manyTopics = new ArrayList<>();
        manyTopics.add(testTopic2);
        manyTopics.add(testTopic1);
        DatabaseManager.dbHelper = new DatabaseHelper(appContext);
        DatabaseManager.open();
        TopicManagerDB.setScoredTopics(manyTopics);
        ArrayList<Topic> topicsFromDB = TopicManagerDB.getAllScoredTopics();
        assertEquals(topicsFromDB.size(),2);
        Topic nsfwTopic = topicsFromDB.get(0);
        assertEquals(nsfwTopic.getTopicName(),"NSFW");

        assertEquals(nsfwTopic.getScoredWords().size(),2);
        DatabaseManager.close();
    }


}