package com.example.ourpact3.model;

import java.util.ArrayList;

public class ScreenFilterTrigger extends Trigger {
    public static class ScoredTopic {
        public int read;
        public int write;
        public Topic topic;
    }

    public ArrayList<ScoredTopic> scoredTopics;

    @Override
    public String getType() {
        return "ScreenFilter";
    }
}
