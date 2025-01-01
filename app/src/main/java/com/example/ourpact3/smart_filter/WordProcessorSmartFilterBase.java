package com.example.ourpact3.smart_filter;

import androidx.annotation.NonNull;

import com.example.ourpact3.pipeline.PipelineResultBase;
import com.example.ourpact3.pipeline.PipelineResultKeywordFilter;
import com.example.ourpact3.service.ScreenInfoExtractor;

/**
 * Can be of different types const lists or reusable
 */
public abstract class WordProcessorSmartFilterBase implements Cloneable
{
    private String shortDescription;
    private boolean enabled;
    private PipelineResultKeywordFilter constResult; // Made private
    private boolean checkOnlyVisibleNodes = true; // Made private
    public final WordSmartFilterIdentifier identifier;

    WordProcessorSmartFilterBase(PipelineResultKeywordFilter constResult, WordSmartFilterIdentifier identifier) throws CloneNotSupportedException
    {
        this.constResult = (PipelineResultKeywordFilter) constResult.clone();
        this.identifier = identifier;
        this.enabled = true;
        this.shortDescription = "";
    }

    public void setEnabled(boolean value)
    {
        this.enabled = value;
    }

    public boolean isEnabled()
    {
        return this.enabled;
    }

    /**
     * Returns the used defined name of the filter
     *
     * @return
     */
    public String getName()
    {
        return constResult.getTriggerFilter();
    }

    public void setFilterShortDescription(String text)
    {
        shortDescription = text;
    }

    public String getFilterShortDescription()
    {
        return shortDescription;
    }

    public void setName(String name)
    {
        constResult.setTriggerFilter(name);
    }

    @NonNull
    @Override
    public WordProcessorSmartFilterBase clone()
    {
        try
        {
            WordProcessorSmartFilterBase clone = (WordProcessorSmartFilterBase) super.clone();
            // Deep copy the constResult
            clone.constResult = (PipelineResultKeywordFilter) this.constResult.clone();
            // Note: name is final and immutable, so no need to clone it
            clone.checkOnlyVisibleNodes = this.checkOnlyVisibleNodes; // Copy the boolean field
            return clone;
        } catch (CloneNotSupportedException e)
        {
            throw new AssertionError(); // Can't happen since we are Cloneable
        }
    }

    // Getter for constResult
    public PipelineResultKeywordFilter getConstResult()
    {
        return constResult;
    }

    // Setter for constResult
    public void setConstResult(PipelineResultKeywordFilter constResult) throws CloneNotSupportedException
    {
        this.constResult = (PipelineResultKeywordFilter) constResult.clone();
    }

    // Getter for checkOnlyVisibleNodes
    public boolean isCheckOnlyVisibleNodes()
    {
        return checkOnlyVisibleNodes;
    }

    // Setter for checkOnlyVisibleNodes
    public void setCheckOnlyVisibleNodes(boolean checkOnlyVisibleNodes)
    {
        this.checkOnlyVisibleNodes = checkOnlyVisibleNodes;
    }

    public abstract PipelineResultBase feedWord(ScreenInfoExtractor.Screen.TextNode textNode);

    public abstract void reset();
}
