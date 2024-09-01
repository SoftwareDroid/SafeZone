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
    public boolean checkOnlyVisibleNodes = true;
    public abstract PipelineResultBase feedWord(ScreenTextExtractor.Screen.Node node);
    public abstract void reset();
}
