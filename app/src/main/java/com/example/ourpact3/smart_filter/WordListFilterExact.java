package com.example.ourpact3.smart_filter;

import androidx.annotation.NonNull;

import com.example.ourpact3.pipeline.PipelineResultBase;
import com.example.ourpact3.pipeline.PipelineResultKeywordFilter;
import com.example.ourpact3.service.ScreenInfoExtractor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WordListFilterExact extends ContentSmartFilterBase
{
    /*
    listofWords (w and w2) | (w and w2) | (w and w2 and w3 )
     */
    public WordListFilterExact(ArrayList<ArrayList<String>> listOfWords, PipelineResultKeywordFilter result) throws CloneNotSupportedException
    {
        super(result);
//    this.editable = searchForEditable;
        for (ArrayList<String> wordGroup : listOfWords)
        {
            HashMap<String, Integer> groupHits = new HashMap<>();
            for (String word : wordGroup)
            {
                if (ignoreCase)
                {
                    word = word.toLowerCase();
                }
                groupHits.put(word, 0);
            }
            wordToHits.add(groupHits);
        }
    }

    @NonNull
    @Override
    public WordListFilterExact clone()
    {
        WordListFilterExact clone = (WordListFilterExact) super.clone();
        // Deep copy the mutable HashMap
        clone.wordToHits.addAll(this.wordToHits);
        return clone;
    }

    //private final boolean editable;
//private final boolean ignoreCase;
    private final ArrayList<HashMap<String, Integer>> wordToHits = new ArrayList<HashMap<String, Integer>>();

    public PipelineResultBase feedWord(ScreenInfoExtractor.Screen.TextNode textNode)
    {
        switch (nodeCheckStrategyType)
        {
            case BOTH:
                break;
            case EDITABLE_ONLY:
                if (textNode.editable)
                {
                    return null;
                }
                break;
            case NONE_EDITABLE_ONLY:
                if (!textNode.editable)
                {
                    return null;
                }
                break;
        }
        //Only process readonly fields
//        if (textNode.editable != this.editable)
//        {
//            return null;
//        }
        String text = ignoreCase ? textNode.textInLowerCase : textNode.text;

        for (HashMap<String, Integer> group : this.wordToHits)
        {
            Integer hits = group.get(text);
            if (hits != null)
            {
                group.put(text, hits + 1);
                if (isFinished())
                {
                    PipelineResultKeywordFilter copy = (PipelineResultKeywordFilter) getConstResult().clone();
                    return copy;
                }
            }
        }


        return null;
    }

    public boolean isFinished()
    {
        boolean oneGroupHit = false;
        for (HashMap<String, Integer> group : this.wordToHits)
        {
            boolean allHit = true;
            for (Map.Entry<String, Integer> entry : group.entrySet())
            {
                if (entry.getValue() == 0)
                {
                    allHit = false;
                    break;
                }
            }
            if (allHit)
            {
                return true;
            }
        }
        return false;
    }

    public void reset()
    {
        for (HashMap<String, Integer> groupHits : wordToHits)
        {
            groupHits.replaceAll((k, v) -> 0); // Reset each entry to 0
        }
    }
}
