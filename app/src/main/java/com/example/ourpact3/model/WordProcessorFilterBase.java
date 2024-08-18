package com.example.ourpact3.model;

import java.util.ArrayList;

public abstract class WordProcessorFilterBase {
    WordProcessorFilterBase(ArrayList<FilerAppAction> actions)
    {
        this.actions = actions;
    }
    private ArrayList<FilerAppAction> actions;
    private int priority;
    public int getPriority(){return priority;}
    public abstract boolean feedWord(String text, boolean editable);
    public abstract void reset();
    public ArrayList<FilerAppAction> getActions(){return actions;}
}
