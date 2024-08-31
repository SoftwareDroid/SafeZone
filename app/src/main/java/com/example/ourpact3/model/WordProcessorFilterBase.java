package com.example.ourpact3.model;

public abstract class WordProcessorFilterBase {
    WordProcessorFilterBase(PipelineResultKeywordFilter result, String name)
    {
        this.result = result;
        this.result.triggerFilter = name;
        this.name = name;
    }
    protected PipelineResultKeywordFilter result;
    public final String name;
    private int priority;
    public int getPriority(){return priority;}
    public abstract PipelineResultBase feedWord(String text, boolean editable);
    public abstract void reset();
}
