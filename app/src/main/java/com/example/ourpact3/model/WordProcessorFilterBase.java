package com.example.ourpact3.model;

import java.util.ArrayList;

public abstract class WordProcessorFilterBase {
    WordProcessorFilterBase(PipelineResult result, String name)
    {
        this.result = result;
        this.result.triggerFilter = name;
        this.name = name;
    }
    protected PipelineResult result;
    public final String name;
    private int priority;
    public int getPriority(){return priority;}
    public abstract PipelineResult feedWord(String text, boolean editable);
    public abstract void reset();
}
