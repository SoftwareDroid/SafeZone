package com.example.ourpact3.topics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class WordListExact
{
    public class Entry
    {
        public String word;
        public int group;
        public String language;
    }

    private boolean ignoreLoading;
    private String name;
    private String description;
    private Map<Entry, Pattern> compiledPatterns = new HashMap<>();
    private ArrayList<Entry> scoredWords;
//    private ArrayList<String> includedTopics;   //List of topics ids
    private boolean allWordsInLowerCase;

    public Map<Entry, Pattern> getCompiledPatterns()
    {
        return compiledPatterns;
    }

    public void setLowerCaseTopic(boolean allWordsLowerCase)
    {
        this.allWordsInLowerCase = allWordsLowerCase;
    }

//    public boolean isLowerCaseTopic()
//    {
//        return allWordsInLowerCase;
//    }

    // Constructor with id and language
    public WordListExact(String name)
    {
        this.name = name;
        this.scoredWords = new ArrayList<>();
        this.compiledPatterns = new HashMap<>();
    }


    // Public getters for id and lang
    public String getTopicName()
    {
        return name;
    }

    public ArrayList<Entry> getWords()
    {
        return scoredWords;
    }

//    public ArrayList<String> getIncludedTopics()
//    {
//        return includedTopics;
//    }

    // Setter for words
    public void setWords(ArrayList<Entry> words)
    {
        this.scoredWords = words;
    }

    // Method to add a single word
    public void addWord(Entry word)
    {
        this.scoredWords.add(word);
    }
    public void addRegExpWord(Entry regExp)
    {
        if (!this.compiledPatterns.containsKey(regExp))
        {
            Pattern pattern = Pattern.compile(regExp.word);
            this.compiledPatterns.put(regExp, pattern);
        }
    }
}
