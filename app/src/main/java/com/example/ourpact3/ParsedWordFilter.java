package com.example.ourpact3;

import android.content.res.XmlResourceParser;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ParsedWordFilter {

    private String version;
    private List<WordGroup> wordGroups;
    private Set<String> ignoredApps;

    public ParsedWordFilter(XmlResourceParser inputStream) throws XmlPullParserException, IOException {

        wordGroups = new ArrayList<>();

        int eventType = inputStream.getEventType(); //TODO: app ingnore list
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG) {
                if (inputStream.getName().equals("adult_filer")) {
                    version = inputStream.getAttributeValue(null, "version");
                } else if (inputStream.getName().equals("group")) {
                    WordGroup wordGroup = new WordGroup();
                    wordGroup.setName(inputStream.getAttributeValue(null, "name"));

                    try {
                        wordGroup.setRead(Integer.parseInt(inputStream.getAttributeValue("0", "read")));
                        wordGroup.setWrite(Integer.parseInt(inputStream.getAttributeValue("0", "write")));
                    }
                    catch (NumberFormatException exp)
                    {

                    }
                    wordGroups.add(wordGroup);
                } else if (inputStream.getName().equals("word")) {
                    String word = inputStream.nextText();
                    WordGroup lastGroup = wordGroups.get(wordGroups.size() - 1);
                    lastGroup.addWord(word + " ");
                }
            } else if (inputStream.getName().equals("app")) {
                String appName = inputStream.nextText();
                ignoredApps.add(appName);
            }
        }
        eventType = inputStream.next();
    }


public Set<String> getIgnoredApps() {return ignoredApps;}
public String getVersion() {
    return version;
}

public List<WordGroup> getWordGroups() {
    return wordGroups;
}

public static class WordGroup {
    private String name;
    private int read;
    private int write;
    private List<String> words;

    public WordGroup() {
        words = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getReadScore() {
        return read;
    }

    public void setRead(int read) {
        this.read = read;
    }

    public int getWriteScore() {
        return write;
    }

    public void setWrite(int write) {
        this.write = write;
    }

    public List<String> getWords() {
        return words;
    }

    public void addWord(String word) {
        words.add(word);
    }
}
}