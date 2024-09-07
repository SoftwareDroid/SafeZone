package com.example.ourpact3.filter;

import androidx.annotation.NonNull;

import com.example.ourpact3.model.PipelineResultBase;
import com.example.ourpact3.model.PipelineResultKeywordFilter;
import com.example.ourpact3.service.ScreenTextExtractor;

public abstract class WordProcessorFilterBase implements Cloneable{
    WordProcessorFilterBase(PipelineResultKeywordFilter constResult, String name) throws CloneNotSupportedException
    {
        this.constResult = (PipelineResultKeywordFilter) constResult.clone();
        constResult.setTriggerFilter(name);
        this.name = name;
    }
    @NonNull
    @Override
    public WordProcessorFilterBase clone() {
        try {
            WordProcessorFilterBase clone = (WordProcessorFilterBase) super.clone();
            // Deep copy the constResult
            clone.constResult = (PipelineResultKeywordFilter) this.constResult.clone();
            // Note: name is final and immutable, so no need to clone it
            clone.checkOnlyVisibleNodes = this.checkOnlyVisibleNodes; // Copy the boolean field
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(); // Can't happen since we are Cloneable
        }
    }

    protected PipelineResultKeywordFilter constResult;
    public final String name;
    public boolean checkOnlyVisibleNodes = true;
    public abstract PipelineResultBase feedWord(ScreenTextExtractor.Screen.Node node);
    public abstract void reset();
}
