package com.example.ourpact3;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import com.example.ourpact3.model.PipelineResult;
import com.example.ourpact3.model.Topic;
import com.example.ourpact3.model.TopicManager;
import com.example.ourpact3.model.WordListFilterScored;

import java.util.ArrayList;

public class WordListFilterScoredTest {

    private WordListFilterScored filter;
    private TopicManager topicManager;
    private PipelineResult result;

    @Before
    public void setup() {
        topicManager = new TopicManager(); // assume this is a mock or a test implementation
        Topic a = new Topic("a","de");
        a.addWord("apple");
        a.addWord("an");
        Topic b = new Topic("b","de");
        b.addWord("banana");
        b.addWord("bear");
        topicManager.addTopic(a);
        topicManager.addTopic(b);
        result = new PipelineResult(); // assume this is a mock or a test implementation
    }


    @Test
    public void testFeedWord_IgnoreCase() {
        ArrayList<WordListFilterScored.TopicScoring> topicScorings = new ArrayList<>();
        topicScorings.add(new WordListFilterScored.TopicScoring("a", 10, 100));
        filter = new WordListFilterScored("test", topicScorings, true, topicManager, result);
        String text = "Apple";
        PipelineResult result = filter.feedWord(text, true);
        assertNotNull(result);
    }

    @Test
    public void testFeedWord_NoMatch() {
        ArrayList<WordListFilterScored.TopicScoring> topicScorings = new ArrayList<>();
        topicScorings.add(new WordListFilterScored.TopicScoring("a", 100, 100));
        filter = new WordListFilterScored("test", topicScorings, true, topicManager, result);
        String text = "Hello";
        assertNull(filter.feedWord(text, true));
    }

    @Test
    public void testFeedWord_Match() {
        ArrayList<WordListFilterScored.TopicScoring> topicScorings = new ArrayList<>();
        topicScorings.add(new WordListFilterScored.TopicScoring("a", 100, 100));
        filter = new WordListFilterScored("test", topicScorings, true, topicManager, result);
        PipelineResult result = filter.feedWord("apple", true);
        assertNotNull(result);
    }

    @Test
    public void testFeedWord_MultipleMatches() {
        ArrayList<WordListFilterScored.TopicScoring> topicScorings = new ArrayList<>();
        topicScorings.add(new WordListFilterScored.TopicScoring("a", 34, 34));
        topicScorings.add(new WordListFilterScored.TopicScoring("b", 30, 40));
        filter = new WordListFilterScored("test", topicScorings, true, topicManager, result);
        String text = "apple";
        PipelineResult result = filter.feedWord(text, true);
        assertNull(result);
        result = filter.feedWord(text, true);
        assertNull(result);
        result = filter.feedWord(text, true);
        assertNotNull(result);
    }

    @Test
    public void testFeedWord_MaxScoreReached() {
        ArrayList<WordListFilterScored.TopicScoring> topicScorings = new ArrayList<>();
        topicScorings.add(new WordListFilterScored.TopicScoring("b", 100, 200));
        filter = new WordListFilterScored("test", topicScorings, true, topicManager, result);
        String text = "bear";
        PipelineResult result = filter.feedWord(text, true);
        assertNotNull(result);
        result = filter.feedWord(text, true);
        assertNotNull(result);
    }

    @Test
    public void testReset() {
        ArrayList<WordListFilterScored.TopicScoring> topicScorings = new ArrayList<>();
        topicScorings.add(new WordListFilterScored.TopicScoring("topic1", 10, 20));
        filter = new WordListFilterScored("test", topicScorings, true, topicManager, result);
        filter.reset();
    }
}
