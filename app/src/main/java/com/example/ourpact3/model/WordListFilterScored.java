package com.example.ourpact3.model;

import java.util.ArrayList;

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

    public WordListFilterScored(String name,ArrayList<TopicScoring> topicScorings, boolean ignoreCase, TopicManager topicManager, PipelineResult result)
    {
        super(result,name);
        this.ignoreCase = ignoreCase;
        this.topicManager = topicManager;
        this.topicScorings = topicScorings;
        assert topicManager != null;
    }
    private ArrayList<TopicScoring>  topicScorings;
    private TopicManager topicManager;
    private boolean ignoreCase;
    private int currentScore;
    private final int MAX_SCORE = 100;
    public PipelineResult feedWord(String text, boolean editable)
    {
        if(ignoreCase)
        {
            text = text.toLowerCase();
        }

        for(TopicScoring scoring : this.topicScorings)
        {
            if((editable && scoring.writeScore == 0) || (!editable && scoring.readScore == 0))
            {
                continue;
            }

            if(topicManager.isStringInTopic(text,scoring.topicId, TopicManager.TopicMatchMode.TOPIC_WORD_IS_INFIX,ignoreCase))
            {
                currentScore += editable? scoring.writeScore : scoring.readScore;
                if(currentScore >= MAX_SCORE)
                {
                    result.triggerWord = text;
                    return result;
                }
            }
        }
        return null;
    }
    public void reset()
    {
        currentScore = 0;
    }
}
