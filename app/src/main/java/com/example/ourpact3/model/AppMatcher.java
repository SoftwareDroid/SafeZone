package com.example.ourpact3.model;

import java.util.ArrayList;

public class AppMatcher {
    public String packageID;
    public String activity; //optional
    public String description;
    public boolean enabled;
    public ArrayList<EventHandler> handlers;
}
