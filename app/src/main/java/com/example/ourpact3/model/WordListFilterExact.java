package com.example.ourpact3.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WordListFilterExact extends WordProcessorFilterBase
{
    public WordListFilterExact(String name, ArrayList<String> listOfWords, boolean ignoreCase, PipelineResultKeywordFilter result, boolean searchForEditable)
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

    private final boolean editable;
    private final boolean ignoreCase;
    private final HashMap<String, Integer> wordToHits = new HashMap<>();

    public PipelineResultBase feedWord(ScreenTextExtractor.Screen.Node node)
    {
        //Only process readonly fields
        if (node.editable != this.editable)
        {
            return null;
        }
        String text = ignoreCase ? node.textInLowerCase : node.text;

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
