package com.example.ourpact3.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Topic
{
    private final String lang;
    private final String id;
    private String description;
    private ArrayList<String> words;
    private ArrayList<String> includedTopics;

    // Constructor with id and language
    public Topic(String id, String lang)
    {
        this.id = id;
        this.lang = lang;
        this.words = new ArrayList<>();
        this.includedTopics = new ArrayList<>();
        this.description = "";
    }

    // Public getters for id and lang
    public String getTopicId()
    {
        return id;
    }

    public String getTopicUID()
    {
        return id + "#" + getLanguage();
    }

    public String getLanguage()
    {
        return lang;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public ArrayList<String> getWords()
    {
        return words;
    }

    public ArrayList<String> getIncludedTopics()
    {
        return includedTopics;
    }

    // Setter for words
    public void setWords(ArrayList<String> words)
    {
        this.words = words;
    }

    // Method to add a single word
    public void addWord(String word)
    {
        this.words.add(word);
    }

    // Setter for includedTopics
    public void setIncludedTopics(ArrayList<String> includedTopics)
    {
        this.includedTopics = includedTopics;
    }

    // Method to add a single included topic
    public void addIncludedTopic(String topic)
    {
        this.includedTopics.add(topic);
    }

    // Method to convert Topic object to JSON
    public JSONObject toJson() throws JSONException
    {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("lang", lang);
        jsonObject.put("id", id);
        jsonObject.put("description", description);

        // Convert ArrayList to JSONArray
        JSONArray wordsArray = new JSONArray(words);
        jsonObject.put("words", wordsArray);

        JSONArray includedTopicsArray = new JSONArray(includedTopics);
        jsonObject.put("includedTopics", includedTopicsArray);

        return jsonObject;
    }

    // Static method to create Topic object from JSON
    public static Topic fromJson(JSONObject jsonObject) throws JSONException
    {
        String id = jsonObject.getString("id");
        String lang = jsonObject.getString("lang");
        Topic topic = new Topic(id, lang);
        topic.description = jsonObject.getString("description");

        // Convert JSONArray to ArrayList
        JSONArray wordsArray = jsonObject.getJSONArray("words");
        for (int i = 0; i < wordsArray.length(); i++)
        {
            topic.words.add(wordsArray.getString(i));
        }

        JSONArray includedTopicsArray = jsonObject.getJSONArray("includedTopics");
        for (int i = 0; i < includedTopicsArray.length(); i++)
        {
            topic.includedTopics.add(includedTopicsArray.getString(i));
        }

        return topic;
    }
}
