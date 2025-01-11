package com.example.ourpact3.topics;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.util.ArrayList;
import java.util.Map;

public class TopicManager
{
//    private HashMap<String, ArrayList<Topic>> topics = new HashMap<String, ArrayList<Topic>>(); // topic id -> topics for all languages



    /**
     * only small letters a-z, numbers and underscore is allowd
     *
     * @param topicID
     * @return if valid
     */
    public static boolean isValidTopicID(String topicID)
    {
        if (topicID != null)
        {
            return topicID.matches("^[a-z0-9_]+$");
        }
        return false;
    }

    public enum TopicMatchMode
    {
        EQUAL,
        TOPIC_WORD_IS_INFIX,
        TOPIC_WORD_IS_PREFIX,
    }

    public static final String ALL_LANGUAGE_CODE = "*";

    public static class SearchResult
    {
        public SearchResult(boolean found, int deep)
        {
            this.found = found;
            this.deep = deep;
        }

        public boolean found;
        public int deep;
        public String trigger;
    }

    public SearchResult isStringInTopic(String text, String topicId, TopicMatchMode mode, boolean checkAgainstLowerCase, String language, int currentDeepness)
    {
        //TODO: fix
        return null;
        /*SearchResult searchResult = new SearchResult(false, currentDeepness);
        ArrayList<Topic> topicsInAllLanguages = topics.get(topicId);
        if (topicsInAllLanguages == null || language == null)
        {
            return searchResult;
        }

        for (Topic topicInOneLang : topicsInAllLanguages)
        {

            // only check for same language for if check for all is on
            if (!language.equals(ALL_LANGUAGE_CODE) && !language.equals(topicInOneLang.getLanguage()))
            {
                continue;
            }
            ArrayList<String> words = topicInOneLang.getScoredWords();
            if (checkAgainstWords(text, searchResult, topicInOneLang, words, checkAgainstLowerCase, mode))
            {
                return searchResult;
            }
            if (checkAgainstRegExp(text, searchResult, topicInOneLang))
            {
                return searchResult;
            }

            // Check child topic if existing
            ArrayList<String> includedTopics = topicInOneLang.getIncludedTopics();
            if (includedTopics != null)
            {
                for (String childrenTopicIds : includedTopics)
                {
                    // only check same language recursively. To prevent redundant checks
                    SearchResult childResult = this.isStringInTopic(text, childrenTopicIds, mode, checkAgainstLowerCase, topicInOneLang.getLanguage(), currentDeepness + 1);
                    if (childResult.found)
                    {
                        return childResult;
                    }

                }
            }

        }
        return searchResult;*/
    }
/*
    private boolean checkAgainstRegExp(String text, SearchResult searchResult, Topic topicInOneLang)
    {
        // Check against patterns
        for (Map.Entry<String, Pattern> compiledPattern : topicInOneLang.getCompiledPatterns().entrySet())
        {
            Matcher matcher = compiledPattern.getValue().matcher(text);
            if (matcher.find())
            {
                searchResult.trigger = text;
                searchResult.found = true;
                return true;
            }
        }
        return false;
    }*/

    private boolean checkAgainstWords(String text, SearchResult searchResult, Topic topicInOneLang, ArrayList<String> words, boolean checkAgainstLowerCase, TopicMatchMode mode)
    {
        if (words == null || words.isEmpty())
        {
            return false;
        }
        for (String word : words)
        {
            if (!topicInOneLang.isLowerCaseTopic() && checkAgainstLowerCase)
            {
                word = word.toLowerCase();
            }
            searchResult.trigger = word;
            switch (mode)
            {
                case EQUAL:
                {
                    if (word.equals(text))
                    {
                        searchResult.found = true;
                        return true;
                    }
                    break;
                }
                case TOPIC_WORD_IS_INFIX:
                {
                    if (text.contains(word))
                    {
                        searchResult.found = true;
                        return true;
                    }
                    break;
                }
                case TOPIC_WORD_IS_PREFIX:
                {
                    if (text.startsWith(word))
                    {
                        searchResult.found = true;
                        return true;
                    }
                    break;
                }
            }
        }
        return false;
    }
}
