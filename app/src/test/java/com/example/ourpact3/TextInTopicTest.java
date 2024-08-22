package com.example.ourpact3;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import com.example.ourpact3.model.Topic;
import com.example.ourpact3.model.TopicManager;

import java.util.ArrayList;

public class TextInTopicTest {

    private TopicManager topicManager;

    @Before
    public void setup() {
        topicManager = new TopicManager();
        String topicId = "topic1";

        Topic topic = new Topic(topicId, "en");
        topic.addWord("test");
        topicManager.addTopic(topic);

        // Initialize topics and other necessary data for the test
    }

    @Test
    public void testIsStringInTopic_EqualMode_ReturnsTrue() {
        // Arrange
        String text = "test";
        TopicManager.TopicMatchMode mode = TopicManager.TopicMatchMode.EQUAL;
        boolean checkAgainstLowerCase = true;
        String language = "en";

        // Act
        boolean result = topicManager.isStringInTopic(text, "topic1", mode, checkAgainstLowerCase, language);

        // Assert
        assertTrue(result);
    }

    @Test
    public void testIsStringInTopic_InfixMode_ReturnsTrue() {
        // Arrange
        String text = "testing";
        String topicId = "topic1";
        TopicManager.TopicMatchMode mode = TopicManager.TopicMatchMode.TOPIC_WORD_IS_INFIX;
        boolean checkAgainstLowerCase = true;

        // Act
        boolean result = topicManager.isStringInTopic(text, "topic1", mode, checkAgainstLowerCase, "en");

        // Assert
        assertTrue(result);
    }

    @Test
    public void testIsStringInTopic_PrefixMode_ReturnsTrue() {
        // Arrange
        String text = "testing";
        String topicId = "topic1";
        TopicManager.TopicMatchMode mode = TopicManager.TopicMatchMode.TOPIC_WORD_IS_PREFIX;
        boolean checkAgainstLowerCase = true;
        String language = "en";

        // Act
        boolean result = topicManager.isStringInTopic(text, "topic1", mode, checkAgainstLowerCase, language);

        // Assert
        assertTrue(result);
    }

    @Test
    public void testIsStringInTopic_NoMatchingTopic_ReturnsFalse() {
        // Arrange
        String text = "test";
        String topicId = "topic1";
        TopicManager.TopicMatchMode mode = TopicManager.TopicMatchMode.EQUAL;
        boolean checkAgainstLowerCase = true;

        // Act
        boolean result = topicManager.isStringInTopic(text, topicId, mode, checkAgainstLowerCase, "en");

        // Assert
        assertFalse(result);
    }

    @Test
    public void testIsStringInTopic_NullTopicId_ReturnsFalse() {
        // Arrange
        String text = "test";
        String topicId = null;
        TopicManager.TopicMatchMode mode = TopicManager.TopicMatchMode.EQUAL;
        boolean checkAgainstLowerCase = true;
        String language = "en";

        // Act
        boolean result = topicManager.isStringInTopic(text, topicId, mode, checkAgainstLowerCase, language);

        // Assert
        assertFalse(result);
    }

    @Test
    public void testIsStringInTopic_NullLanguage_ReturnsFalse() {
        // Arrange
        String text = "test";
        String topicId = "topic1";
        TopicManager.TopicMatchMode mode = TopicManager.TopicMatchMode.EQUAL;
        boolean checkAgainstLowerCase = true;
        String language = null;

        // Act
        boolean result = topicManager.isStringInTopic(text, topicId, mode, checkAgainstLowerCase, language);

        // Assert
        assertFalse(result);
    }

    @Test
    public void testIsStringInTopic_RecursiveCall_ReturnsTrue() {
        // Arrange
        String text = "test";
        String topicId = "topic1";
        TopicManager.TopicMatchMode mode = TopicManager.TopicMatchMode.EQUAL;
        boolean checkAgainstLowerCase = true;
        String language = "en";

        // Add topic with words and included topics to the topic manager
        Topic topic = new Topic(topicId, language);
        topic.addWord("test");
        topic.addIncludedTopic("topic2");
        Topic childTopic = new Topic("topic2", language);
        childTopic.addWord("test");
        topicManager.addTopic(topic);
        topicManager.addTopic(childTopic);

        // Act
        boolean result = topicManager.isStringInTopic(text, topicId, mode, checkAgainstLowerCase, language);

        // Assert
        assertTrue(result);
    }
}
