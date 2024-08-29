package com.example.ourpact3.model;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;

public class TopicManager
{
    private HashMap<String, ArrayList<Topic>> topics = new HashMap<String, ArrayList<Topic>>(); // topic id -> topics for all languages

    /**
     * Removes redundant words for faster filtering later
     *
     * @param topic  new topic to add
     * @param bucket of on topic
     */
    private void removeWordsAlreadyInOtherLanguage(Topic topic, ArrayList<Topic> bucket)
    {
        ArrayList<String> wordsInTopic = topic.getWords();
        if (bucket == null || topic == null || wordsInTopic == null)
        {
            return;
        }
        // Iterate through existing topics to collect existing words
        HashSet<String> existingWords = new HashSet<>();
        for (Topic existingTopic : bucket)
        {
            existingWords.addAll(existingTopic.getWords());
        }

        // Filter the new topic's words and create a new Topic with the filtered words
        ArrayList<String> filteredWords = new ArrayList<>();
        for (String word : wordsInTopic)
        {
            if (!existingWords.contains(word))
            {
                filteredWords.add(word);
            }
        }

        if (filteredWords.size() != wordsInTopic.size())
        {
            topic.setWords(filteredWords);
        }
    }
    public boolean isTopicIdLoaded(String topicId)
    {
        return this.topics.get(topicId) != null;
    }
    public Topic getTopic(String topicId, String language)
    {
        ArrayList<Topic> siblingTopics = topics.get(topicId);
        if (siblingTopics != null)
        {
            for (Topic topic : siblingTopics)
            {
                if (topic.getLanguage().equals(language))
                {
                    return topic;
                }
            }
        }
        return null;
    }

    public int getNumberOfTopics()
    {
        int count = 0;
        for (ArrayList<Topic> siblingTopics : topics.values())
        {
            count += siblingTopics.size();
        }
        return count;
    }

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

    public void addTopic(Topic topic) throws TopicLoaderCycleDetectedException, InvalidTopicIDException, TopicAlreadyExistsException
    {
        String topicId = topic.getTopicId();
        if (!TopicManager.isValidTopicID(topicId))
        {
            throw new InvalidTopicIDException(topicId);
        }
        if (topic.isIgnoreLoading())
        {
            return;
        }

        ArrayList<Topic> siblingTopics = topics.get(topicId);
        if (siblingTopics == null)
        {
            topics.put(topicId, new ArrayList<Topic>());
        } else
        {
            Topic search = this.getTopicInLang(topicId, topic.getLanguage());
            if (search != null)
            {
                throw new TopicAlreadyExistsException(topicId);
            }
        }
        removeWordsAlreadyInOtherLanguage(topic, this.topics.get(topic.getTopicId()));
        topics.get(topicId).add(topic);
    }

    public void checkAllTopics() throws TopicLoaderCycleDetectedException, TopicMissingException
    {
        for (Map.Entry<String, ArrayList<Topic>> entry : topics.entrySet())
        {
            for (Topic topic : entry.getValue())
            {
                if (topic.getIncludedTopics() != null)
                {
                    // Check for cyles
                    HashSet<String> visited = new HashSet<>();
                    HashSet<String> recursionStack = new HashSet<>();

                    // Check for cycles in the new topic's included topics
                    for (String includedTopicId : topic.getIncludedTopics())
                    {
                        Topic inOneLang = getTopicInLang(includedTopicId, topic.getLanguage());
                        if(inOneLang == null)
                        {
                            throw new TopicMissingException("Topic " + includedTopicId + " included in "+ topic.getTopicUID() +" is missing in language " + topic.getLanguage());
                        }
                        checkForCycles(inOneLang, visited, recursionStack, topics);

                    }
                }
            }
        }
    }

    public enum TopicMatchMode
    {
        EQUAL,
        TOPIC_WORD_IS_INFIX,
        TOPIC_WORD_IS_PREFIX,
    }

    public Topic getTopicInLang(String topicId, String language)
    {
        ArrayList<Topic> siblings = topics.get(topicId);
        if (siblings != null && language != null)
        {
            for (Topic sibling : siblings)
            {
                if (sibling.getLanguage().equals(language))
                {
                    return sibling;
                }

            }
        }
        return null;
    }

    // Function to check for cycles
    private void checkForCycles(Topic topic, HashSet<String> visited, HashSet<String> recursionStack, HashMap<String, ArrayList<Topic>> topics) throws TopicLoaderCycleDetectedException
    {
        if (topic == null)
        {
            return;
        }
        // Mark the current node as visited and add to recursion stack
        visited.add(topic.getTopicUID());
        recursionStack.add(topic.getTopicUID());

        ArrayList<String> includedTopics = topic.getIncludedTopics();
        if (includedTopics != null && !includedTopics.isEmpty())
        {
            for (String includedTopicID : includedTopics)
            {
                Topic includedTopic = getTopicInLang(includedTopicID, topic.getLanguage());
                if (includedTopic != null)
                {
                    String uid = includedTopic.getTopicUID();
                    if (!visited.contains(uid))
                    {
                        checkForCycles(includedTopic, visited, recursionStack, topics);
                    } else if (recursionStack.contains(uid))
                    {
                        // If the included topic is in the recursion stack, we found a cycle
                        throw new TopicLoaderCycleDetectedException("Cycle detected in Topic " + topic.getTopicUID() + " with topic: " + uid);
                    }

                }
            }
        }
        recursionStack.remove(topic.getTopicUID());
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
        SearchResult searchResult = new SearchResult(false, currentDeepness);
        ArrayList<Topic> topicsInAllLanguages = topics.get(topicId);
        if (topicsInAllLanguages == null || language == null)
        {
            return searchResult;
        }

        for (Topic topicInOneLang : topicsInAllLanguages)
        {
            ArrayList<String> words = topicInOneLang.getWords();
            if (words == null || words.isEmpty())
            {
                continue;
            }
            // only check for same language for if check for all is on
            if (!language.equals(ALL_LANGUAGE_CODE) && !language.equals(topicInOneLang.getLanguage()))
            {
                continue;
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
                            return searchResult;
                        }
                        break;
                    }
                    case TOPIC_WORD_IS_INFIX:
                    {
                        if (text.contains(word))
                        {
                            searchResult.found = true;
                            return searchResult;
                        }
                        break;
                    }
                    case TOPIC_WORD_IS_PREFIX:
                    {
                        if (text.startsWith(word))
                        {
                            searchResult.found = true;
                            return searchResult;
                        }
                        break;
                    }
                }
                // Check against patterns
                for(Map.Entry<String, Pattern> compiledPattern : topicInOneLang.getCompiledPatterns().entrySet())
                {
                    Matcher matcher = compiledPattern.getValue().matcher(text);
                    if (matcher.find()) {
                        searchResult.found = true;
                        return  searchResult;
                    }
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
        }
        return searchResult;
    }

}
