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
        entry.write = 15;
        entry.word = word;
        entry.languageId = 3;
        entry.isRegex = false;
        return entry;

    }

    @Test
    public void updateTopic() throws JSONException
    {
       Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        // enforce recreation of database at open
        appContext.deleteDatabase(DatabaseHelper.DATABASE_NAME);
        Topic testTopic1 = new Topic("test");
        ArrayList<Topic.ScoredWordEntry> words = new ArrayList<>();
        // Create word

        words.add(createSampleScoredWord("bar"));
        words.add(createSampleScoredWord("foo"));
        testTopic1.setScoredWords(words);

        DatabaseManager.dbHelper = new DatabaseHelper(appContext);

        DatabaseManager.open();
        TopicManagerDB.createOrUpdateTopic(testTopic1);

        Topic testTopicFromDb = TopicManagerDB.getScoredTopicById(testTopic1.getDatabase_id());
        assertEquals(testTopicFromDb.getScoredWords().size(),2);
        // update topic
        ArrayList<Topic.ScoredWordEntry> words2 = new ArrayList<>();
        words2.add(createSampleScoredWord("cat"));
        assertEquals(1,words2.size());
        testTopic1.setScoredWords(words2);
        assertEquals(1,testTopic1.getScoredWords().size());
        TopicManagerDB.deleteTopic(testTopic1);
        ArrayList<Topic.ScoredWordEntry> words3 = TopicManagerDB.getWordsForTopic(1l, TopicManagerDB.TOPIC_TYPE_SCORED);
        assertEquals(0,words3.size());
        TopicManagerDB.createOrUpdateTopic(testTopic1);
        //
        {
            Topic testTopicFromDb2 = TopicManagerDB.getScoredTopicById(testTopic1.getDatabase_id());
            assertEquals(1, testTopicFromDb2.getScoredWords().size());
//            assertEquals("cat", testTopicFromDb2.getScoredWords().get(0).word);
        }
        DatabaseManager.close();

    }

        @Test
    public void testSetManyTopics() throws JSONException
    {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        // enforce recreation of database at open
        appContext.deleteDatabase(DatabaseHelper.DATABASE_NAME);
        Topic testTopic1 = new Topic("NSFW");
        ArrayList<Topic.ScoredWordEntry> words = new ArrayList<>();
        // Create word

        words.add(createSampleScoredWord("boobs"));
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
        TopicManagerDB.createOrUpdateTopic(testTopic2);
        TopicManagerDB.createOrUpdateTopic(testTopic1);
//        TopicManagerDB.setScoredTopics(manyTopics);
        ArrayList<Topic> topicsFromDB = TopicManagerDB.getAllScoredTopics();
        assertEquals(topicsFromDB.size(),2);
        Topic nsfwTopic = topicsFromDB.get(1);
        assertEquals("NSFW",nsfwTopic.getTopicName());
        ArrayList<Topic.ScoredWordEntry> scoredWords = nsfwTopic.getScoredWords();
        // Check one word
        assertEquals(scoredWords.size(),2);
        assertEquals(scoredWords.get(0).word,"boobs");
        assertEquals(scoredWords.get(0).read,10);
        assertEquals(scoredWords.get(0).write,15);
        assertEquals(scoredWords.get(0).languageId,3);
        assertFalse(scoredWords.get(0).isRegex);
        DatabaseManager.close();
        // CRUD , CreateOrUpdate a single Topic, Read, Update, Delete
        //TODO: delete topic by id

        //TODO: change scored topic word list// it is enough to overwrite exising topic and use code from add topics

    }
}