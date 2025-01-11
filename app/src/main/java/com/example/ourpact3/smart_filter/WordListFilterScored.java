package com.example.ourpact3.smart_filter;

import com.example.ourpact3.pipeline.PipelineResultBase;
import com.example.ourpact3.pipeline.PipelineResultKeywordFilter;
import com.example.ourpact3.service.ScreenInfoExtractor;
import com.example.ourpact3.topics.TopicManager;
import com.example.ourpact3.topics.TopicMissingException;

import java.util.ArrayList;
import java.util.TreeSet;

public class WordListFilterScored extends ContentSmartFilterBase
{
    public static class TopicScoring
    {
        public TopicScoring(String topicId, int read, int write)
        {
            this.topicId = topicId;
            this.readScore = read;
            this.writeScore = write;
        }

        final String topicId;
        final int readScore;
        final int writeScore;
    }

    public WordListFilterScored(int topicId, boolean ignoreCase, TopicManager topicManager, PipelineResultKeywordFilter result) throws TopicMissingException, CloneNotSupportedException
    {
        super(result);
        this.ignoreCase = ignoreCase;
        this.topicManager = topicManager;
        this.topicId = topicId;
        assert topicManager != null;
    }

    private int topicId;
    public TopicManager topicManager;
    private final boolean ignoreCase;
    private int currentScore;
    private final int MAX_SCORE = 100;
    private TreeSet<String> triggerWordsFromTopic = new TreeSet<>();
    // Debug function
    public  TreeSet<String> getTriggerWordsInTopic()
    {
        return triggerWordsFromTopic;
    }
    public int getCurrentScore()
    {
        return currentScore;
    }
    public int getTopicId(){return topicId;}
    public PipelineResultBase feedWord(ScreenInfoExtractor.Screen.TextNode textNode)
    {
        String text = ignoreCase ? textNode.textInLowerCase : textNode.text;
        TopicManager.SearchResult bestSearchResult = null;
        int scoringChange = 0;
        for (TopicScoring scoring : this.topicScorings)
        {
            if ((textNode.editable && scoring.writeScore == 0) || (!textNode.editable && scoring.readScore == 0))
            {
                continue;
            }

            TopicManager.SearchResult result = topicManager.isStringInTopic(text, scoring.topicId, TopicManager.TopicMatchMode.TOPIC_WORD_IS_INFIX, ignoreCase, TopicManager.ALL_LANGUAGE_CODE, 0);
            // Pick the topic with which is the directest e.g All contains porn and clothing. We want to count clothing with its own score
            // long text blocks make problems so we sum the score here
            if (result.found)
            {
                scoringChange += textNode.editable ? scoring.writeScore : scoring.readScore;
                if(bestSearchResult == null || result.deep < bestSearchResult.deep)
                {
                    bestSearchResult = result;
                }
            }
        }
        if (bestSearchResult != null)
        {
            if(bestSearchResult.trigger != null)
            {
                triggerWordsFromTopic.add(bestSearchResult.trigger);
            }
            currentScore += scoringChange;
            if (currentScore >= MAX_SCORE)
            {
                PipelineResultBase copy = this.getConstResult();
                return copy;
            }
        }

        return null;
    }

    public void reset()
    {
        currentScore = 0;
        triggerWordsFromTopic.clear();
    }
}
