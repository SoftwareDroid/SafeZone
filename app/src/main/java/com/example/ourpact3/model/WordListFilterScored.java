package com.example.ourpact3.model;

import java.util.ArrayList;
import java.util.TreeSet;

public class WordListFilterScored extends WordProcessorFilterBase
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

    public WordListFilterScored(String name, ArrayList<TopicScoring> topicScorings, boolean ignoreCase, TopicManager topicManager, PipelineResultKeywordFilter result) throws TopicMissingException
    {
        super(result, name);
        this.ignoreCase = ignoreCase;
        this.topicManager = topicManager;
        this.topicScorings = topicScorings;
        assert topicManager != null;
        // catch typos in scorings ids
        for (TopicScoring scoring : topicScorings)
        {
            if (!topicManager.isTopicIdLoaded(scoring.topicId))
            {
                throw new TopicMissingException("scoring " + name + " need topic" + scoring.topicId + " but it is missing");
            }
        }
    }

    private final ArrayList<TopicScoring> topicScorings;
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

    public PipelineResultBase feedWord(ScreenTextExtractor.Screen.Node node)
    {
        String text = ignoreCase ? node.textInLowerCase : node.text;

        TopicManager.SearchResult bestSearchResult = null;
        int scoringChange = 0;
        for (TopicScoring scoring : this.topicScorings)
        {
            if ((node.editable && scoring.writeScore == 0) || (!node.editable && scoring.readScore == 0))
            {
                continue;
            }

            TopicManager.SearchResult result = topicManager.isStringInTopic(text, scoring.topicId, TopicManager.TopicMatchMode.TOPIC_WORD_IS_INFIX, ignoreCase, TopicManager.ALL_LANGUAGE_CODE, 0);
            // Pick the topic with which is the directest e.g All contains porn and clothing. We want to count clothing with its own score
            if (result.found && (bestSearchResult == null || result.deep < bestSearchResult.deep))
            {
                bestSearchResult = result;
                scoringChange = node.editable ? scoring.writeScore : scoring.readScore;

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
                result.inputTriggerWord = text;
                return result;
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
