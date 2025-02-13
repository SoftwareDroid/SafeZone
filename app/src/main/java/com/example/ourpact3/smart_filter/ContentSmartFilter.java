package com.example.ourpact3.smart_filter;

import com.example.ourpact3.db.ContentFilterEntity;
import com.example.ourpact3.db.WordEntity;
import com.example.ourpact3.pipeline.CounterAction;
import com.example.ourpact3.pipeline.PipelineResultBase;
import com.example.ourpact3.pipeline.PipelineResultKeywordFilter;
import com.example.ourpact3.service.ScreenInfoExtractor;
import com.example.ourpact3.topics.TopicManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * wrapper around ContentFilterEntities to give it a state
 */
public class ContentSmartFilter
{
    public static int IGNORED_GROUP_ID = -1;
    public static final int TYPE_SCORED = 5;
    public static final int TYPE_EXACT = 10;
    private final ContentFilterEntity contentFilterEntity;
    private final TopicManager topicManager;
    private int accumulatedScore = 0;
    private PipelineResultKeywordFilter filterResult;

    public static class ExpressionGroup
    {
        public HashSet<Long> foundIDs = new HashSet<>();
        public int numberOfIDs = 0;
    }

    ;

    private final Map<Integer, ExpressionGroup> expressionGroupIDToExpressionGroup = new HashMap<>();

    public ContentFilterEntity getContentFilterEntity()
    {
        return this.contentFilterEntity;
    }

    ContentSmartFilter(ContentFilterEntity contentFilterEntity, TopicManager topicManager)
    {
        this.contentFilterEntity = contentFilterEntity;
        this.topicManager = topicManager;
        initExpressionGroups();
        // create static filter effect
        filterResult = new PipelineResultKeywordFilter(contentFilterEntity);
        filterResult.setCounterAction(new CounterAction(contentFilterEntity));
    }

    private void initExpressionGroups()
    {
        List<WordEntity> wordsInList = topicManager.db.wordDao().getAllWordsInList(contentFilterEntity.getId());
        for (WordEntity word : wordsInList)
        {
            int groupID = word.getExpressionGroupNumber();
            if (groupID != IGNORED_GROUP_ID)
            {
                if (!expressionGroupIDToExpressionGroup.containsKey(groupID))
                {
                    expressionGroupIDToExpressionGroup.put(groupID, new ExpressionGroup());
                }
                ExpressionGroup currentGroup = expressionGroupIDToExpressionGroup.get(groupID);
                assert currentGroup != null;
                currentGroup.foundIDs.add((long) groupID);

            }
        }
        for (int groupID : expressionGroupIDToExpressionGroup.keySet())
        {
            ExpressionGroup group = expressionGroupIDToExpressionGroup.get(groupID);
            assert group != null;
            group.numberOfIDs = group.foundIDs.size();
            group.foundIDs.clear();
        }


    }

    public PipelineResultBase feedWord(ScreenInfoExtractor.Screen.TextNode textNode)
    {
        // one search can result in server findings
        TopicManager.SearchResult2 result = this.topicManager.isStringInWordList(textNode.text, textNode.editable, contentFilterEntity);
        this.accumulatedScore += result.accumulatedScore;
        if (this.accumulatedScore >= 100)
        {
            // early abort exceeds 100
            this.filterResult.triggerWords.addAll(result.matches);
            return filterResult;
        }
        // process groups
        for (WordEntity word : result.matches)
        {
            int expressionGroup = word.getExpressionGroupNumber();
            // add word to triggered words
            this.filterResult.triggerWords.add(word);

            if (expressionGroup != IGNORED_GROUP_ID)
            {
                ExpressionGroup group = this.expressionGroupIDToExpressionGroup.get(expressionGroup);
                assert group != null;
                group.foundIDs.add(word.getId());
                if (group.foundIDs.size() == group.numberOfIDs)
                {
                    // early abort
                    return this.filterResult;

                }
            }
        }
        return null;

    }

    public void reset()
    {
        this.filterResult.triggerWords.clear();
        this.accumulatedScore = 0;
        for (ExpressionGroup group : expressionGroupIDToExpressionGroup.values())
        {
            group.foundIDs.clear();
        }
    }
}
