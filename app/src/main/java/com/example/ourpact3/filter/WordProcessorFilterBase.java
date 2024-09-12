package com.example.ourpact3.filter;

import androidx.annotation.NonNull;

import com.example.ourpact3.model.PipelineResultBase;
import com.example.ourpact3.model.PipelineResultKeywordFilter;
import com.example.ourpact3.service.ScreenInfoExtractor;

public abstract class WordProcessorFilterBase implements Cloneable {
    private PipelineResultKeywordFilter constResult; // Made private
    public final String name; // Kept as final and public
    private boolean checkOnlyVisibleNodes = true; // Made private

    WordProcessorFilterBase(PipelineResultKeywordFilter constResult, String name) throws CloneNotSupportedException {
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

    // Getter for constResult
    public PipelineResultKeywordFilter getConstResult() {
        return constResult;
    }

    // Setter for constResult
    public void setConstResult(PipelineResultKeywordFilter constResult) throws CloneNotSupportedException {
        this.constResult = (PipelineResultKeywordFilter) constResult.clone();
    }

    // Getter for checkOnlyVisibleNodes
    public boolean isCheckOnlyVisibleNodes() {
        return checkOnlyVisibleNodes;
    }

    // Setter for checkOnlyVisibleNodes
    public void setCheckOnlyVisibleNodes(boolean checkOnlyVisibleNodes) {
        this.checkOnlyVisibleNodes = checkOnlyVisibleNodes;
    }

    public abstract PipelineResultBase feedWord(ScreenInfoExtractor.Screen.TextNode textNode);
    public abstract void reset();
}
