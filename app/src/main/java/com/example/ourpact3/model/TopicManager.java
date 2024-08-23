package com.example.ourpact3.model;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

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

    public void addTopic(Topic topic)
    {
        String topicId = topic.getTopicId();
        if (!TopicManager.isValidTopicID(topicId))
        {
            return;
        }
        ArrayList<Topic> siblingTopics = topics.get(topicId);
        if (siblingTopics == null)
        {
            topics.put(topicId, new ArrayList<Topic>());
        } else
        {
            for (Topic existingTopic : siblingTopics)
            {
                if (existingTopic.getLanguage().equals(topic.getLanguage()))
                {
//                   Log.i("TopicManager", " topic with same language already exists");
                    return; // topic with same id and language already exists, do nothing
                }
            }
        }

        if (topic.getIncludedTopics() != null)
        {
            // Check for cyles
            HashSet<String> visited = new HashSet<>();
            HashSet<String> recursionStack = new HashSet<>();

            // Check for cycles in the new topic's included topics
            for (String includedTopicId : topic.getIncludedTopics())
            {
                if (hasIncludeCycle(includedTopicId, visited, recursionStack, topics))
                {
//                    Log.i("TopicManger", "Cycle detected when adding topic: " + topic.getTopicId());
                    return; // Cycle detected, do not add the topic
                }
            }
        }
        removeWordsAlreadyInOtherLanguage(topic, this.topics.get(topic.getTopicId()));
        topics.get(topicId).add(topic);
    }


    public enum TopicMatchMode
    {
        EQUAL,
        TOPIC_WORD_IS_INFIX,
        TOPIC_WORD_IS_PREFIX,
    }


    // Function to check for cycles
    private boolean hasIncludeCycle(String topicId, HashSet<String> visited, HashSet<String> recursionStack, HashMap<String, ArrayList<Topic>> topics)
    {
        // Mark the current node as visited and add to recursion stack
        visited.add(topicId);
        recursionStack.add(topicId);

        // Get the included topics for the current topic
        ArrayList<Topic> includedTopics = topics.get(topicId);
        if (includedTopics != null)
        {
            for (Topic includedTopic : includedTopics)
            {
                String includedTopicId = includedTopic.getTopicId();
                // If the included topic is not visited, recurse on it
                if (!visited.contains(includedTopicId))
                {
                    if (hasIncludeCycle(includedTopicId, visited, recursionStack, topics))
                    {
                        return true;
                    }
                } else if (recursionStack.contains(includedTopicId))
                {
                    // If the included topic is in the recursion stack, we found a cycle
                    return true;
                }
            }
        }

        // Remove the topic from recursion stack
        recursionStack.remove(topicId);
        return false;
    }

    public static final String ALL_LANGUAGE_CODE = "*";

    public boolean isStringInTopic(String text, String topicId, TopicMatchMode mode, boolean checkAgainstLowerCase, String language)
    {
        ArrayList<Topic> topicsInAllLanguages = topics.get(topicId);
        if (topicsInAllLanguages == null || language == null)
        {
//            Log.d("TopicManager", "search error " + topicId);
            return false;
        }

        for (Topic topicInOneLang : topicsInAllLanguages)
        {
            ArrayList<String> words = topicInOneLang.getWords();
            if (words == null || words.isEmpty())
            {
//                Log.d("TopicManager", " topic " + topicInOneLang.getTopicId() + " has no words");
                continue;
            }
            // only check for same language for if check for all is on
            if (!language.equals(ALL_LANGUAGE_CODE) && !language.equals(topicInOneLang.getLanguage()))
            {
                continue;
            }

            for (String word : words)
            {
                if (checkAgainstLowerCase)
                {
                    word = word.toLowerCase();
                }
                switch (mode)
                {
                    case EQUAL:
                    {
                        if (word.equals(text))
                        {
                            return true;
                        }
                        break;
                    }
                    case TOPIC_WORD_IS_INFIX:
                    {
                        if (text.contains(word))
                        {
                            return true;
                        }
                        break;
                    }
                    case TOPIC_WORD_IS_PREFIX:
                    {
                        if (text.startsWith(word))
                        {
                            return true;
                        }
                        break;
                    }
                }
                // Check child topic if existing
                ArrayList<String> includedTopics = topicInOneLang.getIncludedTopics();
                if (includedTopics != null)
                {
                    for (String childrenTopicIds : includedTopics)
                    {
                        // only check same language recursively. To prevent redundant checks
                        if (this.isStringInTopic(text, childrenTopicIds, mode, checkAgainstLowerCase, topicInOneLang.getLanguage()))
                        {
                            return true;
                        }

                    }
                }
            }
        }
        return false;
    }

}
