package com.example.ourpact3.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WordListFilterExact extends WordProcessorFilterBase
{
    public WordListFilterExact(String name,ArrayList<String> listOfWords, boolean ignoreCase, PipelineResult result)
    {
        super(result, name);
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

    private final boolean ignoreCase;
    private final HashMap<String, Integer> wordToHits = new HashMap<>();

    public PipelineResult feedWord(String text, boolean editable)
    {
        //Only process readonly fields
        if (editable)
        {
            return null;
        }

        if (ignoreCase)
        {
            text = text.toLowerCase();
        }

        Integer hits = this.wordToHits.get(text);
        if (hits != null)
        {
            // Update
            wordToHits.put(text, hits + 1);
            return this.isFinished() ? this.result : null;

        }


        return null;
    }

    public boolean isFinished()
    {
        for (Map.Entry<String, Integer> entry : wordToHits.entrySet())
        {
            if (entry.getValue() != 1)
            {
                return true;
            }
        }
        return false;
    }

    public void reset()
    {
        wordToHits.keySet().forEach(key -> wordToHits.put(key, 0));
    }
}
