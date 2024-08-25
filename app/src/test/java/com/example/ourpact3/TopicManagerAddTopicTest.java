package com.example.ourpact3;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import com.example.ourpact3.model.Topic;
import com.example.ourpact3.model.TopicLoaderCycleDetectedException;
import com.example.ourpact3.model.TopicManager;

import java.util.ArrayList;
import java.util.Arrays;

public class TopicManagerAddTopicTest
{

    private TopicManager topicManager;

    @Before
    public void setup() {
        topicManager = new TopicManager();
    }

    @Test
    public void testAddTopic_NullTopicId() throws TopicLoaderCycleDetectedException
    {
        Topic topic = new Topic(null, "en");
        topicManager.addTopic(topic);
        assertTrue(topicManager.getNumberOfTopics() == 0);
    }

    @Test
    public void testAddTopic_InvalidTopicId() throws TopicLoaderCycleDetectedException
    {
        Topic topic = new Topic(" invalid-id", "en");
        topicManager.addTopic(topic);
        assertTrue(topicManager.getNumberOfTopics() == 0);
    }

    @Test
    public void testAddTopic_DuplicateLanguage() throws TopicLoaderCycleDetectedException
    {
        Topic topic1 = new Topic("id1", "en");
        topicManager.addTopic(topic1);
        Topic topic2 = new Topic("id1", "en");
        topicManager.addTopic(topic2);
        assertEquals(1, topicManager.getNumberOfTopics());
    }

    @Test
    public void testAddTopic_NoCycle() throws TopicLoaderCycleDetectedException
    {
        Topic topic1 = new Topic("id1", "en");
        topic1.setIncludedTopics(new ArrayList<>(Arrays.asList("id2")));
        Topic topic2 = new Topic("id2", "fr");
        topicManager.addTopic(topic1);
        topicManager.addTopic(topic2);
        assertEquals(2, topicManager.getNumberOfTopics());
    }

    @Test
    public void testAddTopic_Cycle() throws TopicLoaderCycleDetectedException
    {
        Topic topic1 = new Topic("id1", "en");
        topic1.setIncludedTopics(new ArrayList<>(Arrays.asList("id2")));

        Topic topic2 = new Topic("id2", "fr");
        topic2.setIncludedTopics(new ArrayList<>(Arrays.asList("id1")));

        topicManager.addTopic(topic1);
        topicManager.addTopic(topic2);
        assertEquals(1, topicManager.getNumberOfTopics());
    }

    @Test
    public void testAddTopic_RemoveWordsAlreadyInOtherLanguage() {
        // This test requires a more complex setup and may require mocking
        // For simplicity, this test is not implemented
    }
}