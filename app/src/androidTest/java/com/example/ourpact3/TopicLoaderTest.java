package com.example.ourpact3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.ourpact3.model.Topic;
import com.example.ourpact3.model.TopicLoader;

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
    public void loadTopic()
    {

        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        TopicLoader topicLoader = new TopicLoader();
        String[] usedLanguages = {"de"};
        ArrayList<TopicLoader.TopicDescriptor> allAvailableTopics = topicLoader.getAllLoadableTopics(appContext, Set.of(usedLanguages));
        // Check if there are available topics
        assertTrue(allAvailableTopics.size() > 1);

        // Check if all topics are not null
        for (TopicLoader.TopicDescriptor descriptor : allAvailableTopics)
        {
            Topic topic = topicLoader.loadTopicFile(appContext, descriptor);
            assertNotNull(topic);
        }

        // Check if there is a topic with id "xy"
        boolean hasSampleTopic = false;
        Topic topic = null;
        for (TopicLoader.TopicDescriptor descriptor : allAvailableTopics)
        {
            if (descriptor.id.equals("sample_topic"))
            {
                hasSampleTopic = true;
                break;
            }
        }
        assertTrue(hasSampleTopic);
        // Check the language
        assertEquals("de", topic.getLanguage());

        // Check the ID
        assertEquals("sample_topic", topic.getTopicId());

        // Check the description
        assertEquals("This is a sample topic", topic.getDescription());

        // Check the words
        assertEquals(3, topic.getWords().size());
        assertTrue(topic.getWords().contains("word1"));
        assertTrue(topic.getWords().contains("word2"));
        assertTrue(topic.getWords().contains("word3"));

        // Check the included topics
        assertEquals(3, topic.getIncludedTopics().size());
        assertTrue(topic.getIncludedTopics().contains("topic1"));
        assertTrue(topic.getIncludedTopics().contains("topic2"));
        assertTrue(topic.getIncludedTopics().contains("topic3"));
        assertEquals("com.example.ourpact3", appContext.getPackageName());
    }


}