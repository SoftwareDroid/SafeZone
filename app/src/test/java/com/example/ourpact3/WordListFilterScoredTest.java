package com.example.ourpact3;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import com.example.ourpact3.model.InvalidTopicIDException;
import com.example.ourpact3.model.PipelineResultBase;
import com.example.ourpact3.model.PipelineResultKeywordFilter;
import com.example.ourpact3.model.ScreenTextExtractor;
import com.example.ourpact3.model.Topic;
import com.example.ourpact3.model.TopicAlreadyExistsException;
import com.example.ourpact3.model.TopicLoaderCycleDetectedException;
import com.example.ourpact3.model.TopicManager;
import com.example.ourpact3.model.TopicMissingException;
import com.example.ourpact3.model.WordListFilterScored;

import java.util.ArrayList;
import java.util.List;

public class WordListFilterScoredTest {

    private WordListFilterScored filter;
    private TopicManager topicManager;
    private PipelineResultBase result;

    ScreenTextExtractor.Screen.Node createSimpleScreen(String text,boolean edit)
    {
        ScreenTextExtractor.Screen.Node n = new ScreenTextExtractor.Screen.Node(true,edit,text);
        return n;
    }

    @Before
    public void setup() throws TopicLoaderCycleDetectedException, TopicAlreadyExistsException, InvalidTopicIDException
    {
        topicManager = new TopicManager(); // assume this is a mock or a test implementation
        Topic a = new Topic("a","de");
        a.addWord("apple");
        a.addWord("an");
        Topic b = new Topic("b","de");
        b.addWord("banana");
        b.addWord("bear");
        topicManager.addTopic(a);
        topicManager.addTopic(b);
    }


    @Test
    public void testFeedWord_IgnoreCase() throws TopicMissingException
    {
        ArrayList<WordListFilterScored.TopicScoring> topicScorings = new ArrayList<>();
        topicScorings.add(new WordListFilterScored.TopicScoring("a", 10, 100));
        filter = new WordListFilterScored("test", topicScorings, true, topicManager, (PipelineResultKeywordFilter) result);
        String text = "Apple";
        PipelineResultBase result = filter.feedWord(createSimpleScreen(text,true));
        assertNotNull(result);
    }

    @Test
    public void testFeedWord_NoMatch() throws TopicMissingException
    {
        ArrayList<WordListFilterScored.TopicScoring> topicScorings = new ArrayList<>();
        topicScorings.add(new WordListFilterScored.TopicScoring("a", 100, 100));
        filter = new WordListFilterScored("test", topicScorings, true, topicManager, (PipelineResultKeywordFilter) result);
        String text = "Hello";
        assertNull(filter.feedWord(new ScreenTextExtractor.Screen.Node(true,true,text)));
    }

    @Test
    public void testFeedWord_Match() throws TopicMissingException
    {
        ArrayList<WordListFilterScored.TopicScoring> topicScorings = new ArrayList<>();
        topicScorings.add(new WordListFilterScored.TopicScoring("a", 100, 100));
        filter = new WordListFilterScored("test", topicScorings, true, topicManager, (PipelineResultKeywordFilter) result);
        PipelineResultBase result = filter.feedWord(new ScreenTextExtractor.Screen.Node(true,true,"apple"));
        assertNotNull(result);
    }

    @Test
    public void testFeedWord_MultipleMatches() throws TopicMissingException
    {
        ArrayList<WordListFilterScored.TopicScoring> topicScorings = new ArrayList<>();
        topicScorings.add(new WordListFilterScored.TopicScoring("a", 34, 34));
        topicScorings.add(new WordListFilterScored.TopicScoring("b", 30, 40));
        filter = new WordListFilterScored("test", topicScorings, true, topicManager, (PipelineResultKeywordFilter) result);
        String text = "apple";
        PipelineResultBase result = filter.feedWord(new ScreenTextExtractor.Screen.Node(true,true,text));
        assertNull(result);
        result = filter.feedWord(new ScreenTextExtractor.Screen.Node(true,true,text));
        assertNull(result);
        result = filter.feedWord(new ScreenTextExtractor.Screen.Node(true,true,text));
        assertNotNull(result);
    }

    @Test
    public void testFeedWord_MaxScoreReached() throws TopicMissingException
    {
        ArrayList<WordListFilterScored.TopicScoring> topicScorings = new ArrayList<>();
        topicScorings.add(new WordListFilterScored.TopicScoring("b", 100, 200));
        filter = new WordListFilterScored("test", topicScorings, true, topicManager, (PipelineResultKeywordFilter) result);
        String text = "bear";
        PipelineResultBase result = filter.feedWord(new ScreenTextExtractor.Screen.Node(true,true,text));
        assertNotNull(result);
        result = filter.feedWord(new ScreenTextExtractor.Screen.Node(true,true,text));
        assertNotNull(result);
    }

    @Test
    public void testReset() throws TopicMissingException
    {
        ArrayList<WordListFilterScored.TopicScoring> topicScorings = new ArrayList<>();
        topicScorings.add(new WordListFilterScored.TopicScoring("topic1", 10, 20));
        filter = new WordListFilterScored("test", topicScorings, true, topicManager, (PipelineResultKeywordFilter) result);
        filter.reset();
    }

    @Test
    public void testNestedTopic() throws TopicLoaderCycleDetectedException, TopicAlreadyExistsException, InvalidTopicIDException, TopicMissingException
    {
        Topic foodTopic = new Topic("food", "en");
        foodTopic.setWords(new ArrayList<String>(List.of("food")));
        foodTopic.setIncludedTopics(new ArrayList<String>(List.of("fruits","desserts")));

        Topic foodChildTopic = new Topic("fruits", "en");
        foodChildTopic.setWords(new ArrayList<String>(List.of("apple")));

        Topic foodChildTopic2 = new Topic("desserts", "en");
        foodChildTopic2.setWords(new ArrayList<String>(List.of("cake")));
        topicManager.addTopic(foodTopic);
        topicManager.addTopic(foodChildTopic2);
        topicManager.addTopic(foodChildTopic);

        ArrayList<WordListFilterScored.TopicScoring> topicScorings = new ArrayList<>();
        topicScorings.add(new WordListFilterScored.TopicScoring("food", 50, 50));
        topicScorings.add(new WordListFilterScored.TopicScoring("fruits", 30, 30));
        filter = new WordListFilterScored("test", topicScorings, true, topicManager, (PipelineResultKeywordFilter) result);
        filter.feedWord(new ScreenTextExtractor.Screen.Node(true,true,"apple"));
        assertEquals(filter.getCurrentScore(),30);//addiert 50+30 ist einfach falsch
        filter.reset();
        filter.feedWord(new ScreenTextExtractor.Screen.Node(true,true,"food"));
        assertEquals(filter.getCurrentScore(),50);
        filter.reset();
// Cake has no scoring so parent scoring is used
        filter.feedWord(new ScreenTextExtractor.Screen.Node(true,true,"cake"));
        assertEquals(filter.getCurrentScore(),50);
    }
}
