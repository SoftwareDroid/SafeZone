package com.example.ourpact3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.ourpact3.model.InvalidTopicIDException;
import com.example.ourpact3.model.Topic;
import com.example.ourpact3.model.TopicAlreadyExistsException;
import com.example.ourpact3.model.TopicLoader;
import com.example.ourpact3.model.TopicLoaderCycleDetectedException;
import com.example.ourpact3.model.TopicLoaderException;
import com.example.ourpact3.model.TopicManager;
import com.example.ourpact3.model.TopicMissingException;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Set;


/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class TopicLoaderTest
{
    @Test
    public void testSerialization() throws JSONException
    {
        Topic topicOld = new Topic("a", "b");
        topicOld.setDescription("cd");
        ArrayList<String> words = new ArrayList<>();
        words.add("foo");
        words.add("bar");
        ArrayList<String> includes = new ArrayList<>();
        includes.add("hallo");
        topicOld.setWords(words);
        topicOld.setIncludedTopics(includes);
        JSONObject obj = topicOld.toJson();
        Topic topicNew = Topic.fromJson(obj);
        assertEquals(topicOld.getTopicId(), topicNew.getTopicId());
        assertEquals(topicOld.getLanguage(), topicNew.getLanguage());
        assertEquals(topicOld.getDescription(), topicNew.getDescription());
        assertEquals(topicOld.getWords(), topicNew.getWords());
        assertEquals(topicOld.getIncludedTopics(), topicNew.getIncludedTopics());
    }

    @Test
    public void testSerialization2() throws JSONException
    {
        Topic topicOld = new Topic("a", "b");
        JSONObject obj = topicOld.toJson();
        Topic topicNew = Topic.fromJson(obj);
        assertEquals(topicOld.getTopicId(), topicNew.getTopicId());
        assertEquals(topicOld.getLanguage(), topicNew.getLanguage());
        assertEquals(topicOld.getDescription(), topicNew.getDescription());
        assertEquals(topicOld.getWords(), topicNew.getWords());
        assertEquals(topicOld.getIncludedTopics(), topicNew.getIncludedTopics());
    }


    @Test
    public void testIfAllSystemTopicAreLoadable() throws TopicLoaderCycleDetectedException, TopicAlreadyExistsException, InvalidTopicIDException, TopicLoaderException, TopicMissingException
    {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        TopicLoader topicLoader = new TopicLoader();
        String[] usedLanguages = {"de","en"};
        ArrayList<TopicLoader.TopicDescriptor> allAvailableTopics = topicLoader.getAllLoadableTopics(appContext, Set.of(usedLanguages));
        // Check if there are available topics
        assertNotNull(allAvailableTopics);

        assertFalse(allAvailableTopics.isEmpty());
        Topic sampleTopic = null;
        TopicManager topicManager = new TopicManager();
        // Check if all topics are not null
        for (TopicLoader.TopicDescriptor descriptor : allAvailableTopics)
        {
            Topic topic = topicLoader.loadTopicFile(appContext, descriptor);
            String uid = topic.getTopicUID();
            boolean isLowerCaseTopic = topic.isLowerCaseTopic();
            if (topic.getTopicId().equals("patrick_all_merged"))
            {
                sampleTopic = topic;
            }
            assertNotNull(topic);
            topicManager.addTopic(topic);
        }
        assertNotNull(sampleTopic);
        topicManager.checkAllTopics();
    }



    @Test
    public void loadSampleTopic() throws TopicLoaderException
    {

        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        TopicLoader topicLoader = new TopicLoader();
        String[] usedLanguages = {"de"};
        ArrayList<TopicLoader.TopicDescriptor> allAvailableTopics = topicLoader.getAllLoadableTopics(appContext, Set.of(usedLanguages));
        // Check if there are available topics
        assertNotNull(allAvailableTopics);

        assertFalse(allAvailableTopics.isEmpty());
        Topic sampleTopic = null;

        // Check if all topics are not null
        for (TopicLoader.TopicDescriptor descriptor : allAvailableTopics)
        {
            Topic topic = topicLoader.loadTopicFile(appContext, descriptor);
            if (topic.getTopicId().equals("sample_topic"))
            {
                sampleTopic = topic;
            }
            assertNotNull(topic);
        }

        // Check if there is a topic with id "xy"
        assertNotNull(sampleTopic);
        // Check the language
        assertEquals("de", sampleTopic.getLanguage());

        // Check the ID
        assertEquals("sample_topic", sampleTopic.getTopicId());

        // Check the description
        assertEquals("This is a sample topic", sampleTopic.getDescription());

        // Check the words
        assertEquals(3, sampleTopic.getWords().size());
        assertTrue(sampleTopic.getWords().contains("word1"));
        assertTrue(sampleTopic.getWords().contains("word2"));
        assertTrue(sampleTopic.getWords().contains("word3"));

        // Check the included topics
        assertEquals(3, sampleTopic.getIncludedTopics().size());
        assertTrue(sampleTopic.getIncludedTopics().contains("topic1"));
        assertTrue(sampleTopic.getIncludedTopics().contains("topic2"));
        assertTrue(sampleTopic.getIncludedTopics().contains("topic3"));
        assertEquals("com.example.ourpact3", appContext.getPackageName());
    }


}