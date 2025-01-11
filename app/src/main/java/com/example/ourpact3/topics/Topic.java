package com.example.ourpact3.topics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class Topic
{
    public static class ScoredWordEntry
    {
        public long id;
        public String word;
        public int read;
        public int write;
//        public int group;
        public int languageId;
        public boolean isRegex;
    }


    private String name;
    private String description;
    private Map<ScoredWordEntry, Pattern> compiledPatterns = new HashMap<>();
    private ArrayList<ScoredWordEntry> scoredWords;
    private boolean allWordsInLowerCase;

    public Map<ScoredWordEntry, Pattern> getCompiledPatterns()
    {
        return compiledPatterns;
    }

    public void setLowerCaseTopic(boolean allWordsLowerCase)
    {
        this.allWordsInLowerCase = allWordsLowerCase;
    }

    public boolean isLowerCaseTopic()
    {
        return allWordsInLowerCase;
    }
    public long database_id;
    public long getDatabase_id(){return database_id;}
    // Constructor with id and language
    public Topic(String name)
    {
        this.name = name;
        this.scoredWords = new ArrayList<>();
//        this.includedTopics = new ArrayList<>();
        this.compiledPatterns = new HashMap<>();
        this.description = "";
        this.allWordsInLowerCase = false;
    }


    // Public getters for id and lang
    public String getTopicName()
    {
        return name;
    }


    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public ArrayList<ScoredWordEntry> getScoredWords()
    {
        return scoredWords;
    }


    // Setter for words
    public void setScoredWords(ArrayList<ScoredWordEntry> words)
    {
        this.scoredWords = words;
    }

    // Method to add a single word
    public void addScoredWord(ScoredWordEntry word)
    {
        this.scoredWords.add(word);
    }
    public void addRegExpWord(ScoredWordEntry regExp)
    {
        if (!this.compiledPatterns.containsKey(regExp))
        {
            Pattern pattern = Pattern.compile(regExp.word);
            this.compiledPatterns.put(regExp, pattern);
        }
    }

    // Setter for includedTopics
//    public void setIncludedTopics(ArrayList<String> includedTopics)
//    {
//        this.includedTopics = includedTopics;
//    }

    // Method to add a single included topic
//    public void addIncludedTopic(String topic)
//    {
//        this.includedTopics.add(topic);
//    }

    // Method to convert Topic object to JSON
    /*public JSONObject toJson() throws JSONException
    {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("lang", lang);
        jsonObject.put("id", id);
        jsonObject.put("description", description);

        // Convert ArrayList to JSONArray
        JSONArray wordsArray = new JSONArray(words);
        jsonObject.put("words", wordsArray);

        if (!this.compiledPatterns.isEmpty())
        {
            ArrayList<TopicEntry> regExpKeys = new ArrayList<>(this.compiledPatterns.keySet());
            JSONArray wordsArray2 = new JSONArray(regExpKeys);
            jsonObject.put("regExpWords", wordsArray2);
        }
        JSONArray includedTopicsArray = new JSONArray(includedTopics);
        jsonObject.put("includedTopics", includedTopicsArray);

        return jsonObject;
    }*/

    // Static method to create Topic object from JSON
    /*public static Topic fromJson(JSONObject jsonObject) throws JSONException
    {
        String id = jsonObject.getString("id");
        String lang = jsonObject.getString("lang");
        Topic topic = new Topic(id, lang);
        topic.description = jsonObject.getString("description");
        if (jsonObject.has("ignore_loading"))
        {
            topic.ignoreLoading = jsonObject.getBoolean("ignore_loading");
        }
        // Convert JSONArray to ArrayList
        JSONArray wordsArray = jsonObject.getJSONArray("words");

        boolean allLowerCase = true;
        for (int i = 0; i < wordsArray.length(); i++)
        {
            String word = wordsArray.getString(i);
            if (allLowerCase && !word.equals(word.toLowerCase()))
            {
                allLowerCase = false;
            }
            topic.words.add(wordsArray.getString(i));
        }
        topic.setLowerCaseTopic(allLowerCase);
        //

        JSONArray wordsArray2 = jsonObject.optJSONArray("regExpWords");
        if (wordsArray2 != null)
        {
            for (int i = 0; i < wordsArray2.length(); i++)
            {
                String regExp = wordsArray2.getString(i);
                if (!topic.compiledPatterns.containsKey(regExp))
                {
                    Pattern pattern = Pattern.compile(regExp);
                    topic.compiledPatterns.put(regExp, pattern);
                }
            }
        }
        JSONArray includedTopicsArray = jsonObject.getJSONArray("includedTopics");
        for (int i = 0; i < includedTopicsArray.length(); i++)
        {
            topic.includedTopics.add(includedTopicsArray.getString(i));
        }

        return topic;
    }*/
}
