package com.example.ourpact3.smart_filter;

import androidx.annotation.NonNull;

import com.example.ourpact3.pipeline.PipelineResultBase;
import com.example.ourpact3.pipeline.PipelineResultKeywordFilter;
import com.example.ourpact3.service.ScreenInfoExtractor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WordListFilterExact extends WordProcessorSmartFilterBase
{
    public WordListFilterExact(String name, ArrayList<String> listOfWords, boolean ignoreCase, PipelineResultKeywordFilter result, boolean searchForEditable) throws CloneNotSupportedException
    {
        super(result, name);
        this.editable = searchForEditable;
        this.ignoreCase = ignoreCase;
        for (String word : listOfWords)
        {
            if (ignoreCase)
            {
                word = word.toLowerCase();
            }
            this.wordToHits.put(word, 0);

        }
    }

    @NonNull
    @Override
    public WordListFilterExact clone() {
            WordListFilterExact clone = (WordListFilterExact) super.clone();
            // Deep copy the mutable HashMap
            clone.wordToHits.putAll(new HashMap<>(this.wordToHits));
            return clone;
    }
    private final boolean editable;
    private final boolean ignoreCase;
    private final HashMap<String, Integer> wordToHits = new HashMap<>();

    public PipelineResultBase feedWord(ScreenInfoExtractor.Screen.TextNode textNode)
    {
        //Only process readonly fields
        if (textNode.editable != this.editable)
        {
            return null;
        }
        String text = ignoreCase ? textNode.textInLowerCase : textNode.text;

        Integer hits = this.wordToHits.get(text);
        if (hits != null)
        {
            // Update
            wordToHits.put(text, hits + 1);
            PipelineResultKeywordFilter copy = (PipelineResultKeywordFilter) getConstResult().clone();
            return this.isFinished() ? copy : null;

        }


        return null;
    }

    public boolean isFinished()
    {
        for (Map.Entry<String, Integer> entry : wordToHits.entrySet())
        {
            if (entry.getValue() == 0)
            {
                return false;
            }
        }
        return true;
    }

    public void reset()
    {
        wordToHits.replaceAll((k, v) -> 0);
    }
}
