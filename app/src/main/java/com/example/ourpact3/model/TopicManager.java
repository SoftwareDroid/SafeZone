package com.example.ourpact3.model;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class TopicManager
{
    private HashMap<String, ArrayList<Topic>> topics = new HashMap<String, ArrayList<Topic>>(); // topic id -> topics for all languages
    public void addTopic(Topic topic)
    {
        String topicId = topic.getTopicId();
        if (topicId == null)
        {
            return;
        }
        if (!topics.containsKey(topicId))
        {
            topics.put(topicId, new ArrayList<Topic>());
        }
        for (Topic existingTopic : topics.get(topicId))
        {
            if (existingTopic.getLanguage().equals(topic.getLanguage()))
            {
                Log.i("TopicManager", " topic with same language already exists");
                return; // topic with same id and language already exists, do nothing
            }
        }

        if(topic.includedTopics != null)
        {
            // Check for cyles
            HashSet<String> visited = new HashSet<>();
            HashSet<String> recursionStack = new HashSet<>();

            // Check for cycles in the new topic's included topics
            for (String includedTopicId : topic.includedTopics)
            {
                if (hasCycle(includedTopicId, visited, recursionStack, topics))
                {
                    Log.i("TopicManger", "Cycle detected when adding topic: " + topic.getTopicId());
                    return; // Cycle detected, do not add the topic
                }
            }
        }

        topics.get(topicId).add(topic);
    }

    public enum TopicMatchMode
    {
        EQUAL,
        TOPIC_WORD_IS_INFIX,
        TOPIC_WORD_IS_PREFIX,
    }


    // Function to check for cycles
    private boolean hasCycle(String topicId, HashSet<String> visited, HashSet<String> recursionStack, HashMap<String, ArrayList<Topic>> topics) {
        // Mark the current node as visited and add to recursion stack
        visited.add(topicId);
        recursionStack.add(topicId);

        // Get the included topics for the current topic
        ArrayList<Topic> includedTopics = topics.get(topicId);
        if (includedTopics != null) {
            for (Topic includedTopic : includedTopics) {
                String includedTopicId = includedTopic.getTopicId();
                // If the included topic is not visited, recurse on it
                if (!visited.contains(includedTopicId)) {
                    if (hasCycle(includedTopicId, visited, recursionStack, topics)) {
                        return true;
                    }
                } else if (recursionStack.contains(includedTopicId)) {
                    // If the included topic is in the recursion stack, we found a cycle
                    return true;
                }
            }
        }

        // Remove the topic from recursion stack
        recursionStack.remove(topicId);
        return false;
    }


    public boolean isStringInTopic(String text, String topicId, TopicMatchMode mode, boolean checkAgainstLowerCase)
    {
        ArrayList<Topic> topicsInAllLanguages = topics.get(topicId);
        if (topicsInAllLanguages == null)
        {
            Log.d("TopicManager", "search in missing topic " + topicId);
            return false;
        }

        for (Topic topicInOneLang : topicsInAllLanguages)
        {
            if (topicInOneLang.words == null || topicInOneLang.words.isEmpty())
            {
                Log.d("TopicManager", " topic " + topicInOneLang.getTopicId() + " has no words");
                continue;
            }

            for (String word : topicInOneLang.words)
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
                if (topicInOneLang.includedTopics != null)
                {
                    for (String childrenTopicIds : topicInOneLang.includedTopics)
                    {
                        if (this.isStringInTopic(text, childrenTopicIds, mode, checkAgainstLowerCase))
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
