package com.example.ourpact3.model;

import java.util.ArrayList;

public abstract class WordProcessorFilterBase {
    WordProcessorFilterBase(ArrayList<FilterAppAction> actions)
    {
        this.actions = actions;
    }
    private ArrayList<FilterAppAction> actions;
    private int priority;
    public int getPriority(){return priority;}
    public abstract boolean feedWord(String text, boolean editable);
    public abstract void reset();
    public ArrayList<FilterAppAction> getActions(){return actions;}
}
