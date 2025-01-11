package com.example.ourpact3.smart_filter;

import androidx.annotation.NonNull;

import com.example.ourpact3.pipeline.CounterAction;
import com.example.ourpact3.pipeline.PipelineResultBase;
import com.example.ourpact3.pipeline.PipelineResultKeywordFilter;
import com.example.ourpact3.service.ScreenInfoExtractor;

/**
 * Can be of different types const lists or reusable
 */
public abstract class ContentSmartFilterBase implements Cloneable
{
    public static final int TYPE_SCORED = 5;
    public static final int TYPE_EXACT = 10;
    public Long database_id;
    protected NodeCheckStrategyType nodeCheckStrategyType;
    protected boolean ignoreCase;
    private boolean readable;
    private boolean writable;
    private boolean userCreated;
    private AppGroup appGroup;
    private String shortDescription;
    private boolean enabled;
    private PipelineResultKeywordFilter constResult; // Made private
    private boolean checkOnlyVisibleNodes; // Made private

    ContentSmartFilterBase(PipelineResultKeywordFilter constResult) throws CloneNotSupportedException
    {
        this.constResult = (PipelineResultKeywordFilter) constResult.clone();
        this.enabled = true;
        this.shortDescription = "";
        this.readable = true;
        this.writable = true;
        this.nodeCheckStrategyType = NodeCheckStrategyType.BOTH;    //Default check everything
        this.checkOnlyVisibleNodes = true;
    }

    public NodeCheckStrategyType getNodeCheckStrategyType()
    {
        return nodeCheckStrategyType;
    }

    public void setNodeCheckStrategyType(NodeCheckStrategyType strategyType)
    {
        nodeCheckStrategyType = strategyType;
    }

    public boolean isIgnoringCase()
    {
        return ignoreCase;
    }

    public AppGroup getAppGroup()
    {
        return appGroup;
    }

    public void setAppGroup(AppGroup value)
    {
        appGroup = value;
    }

    public boolean isReadable()
    {
        return readable;
    }

    public boolean isWritable()
    {
        return writable;
    }

    public boolean isUserCreated()
    {
        return userCreated;
    }

    public void setUserCreated(boolean value)
    {
        userCreated = value;
    }

    public void setEnabled(boolean value)
    {
        this.enabled = value;
    }

    public CounterAction getCounterAction()
    {
        return constResult.getCounterAction();
    }

    public void setCounterAction(CounterAction action)
    {
        constResult.setCounterAction(action);
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

    public String getShortDescription()
    {
        return shortDescription;
    }

    public void setName(String name)
    {
        constResult.setTriggerFilter(name);
    }

    @NonNull
    @Override
    public ContentSmartFilterBase clone()
    {
        try
        {
            ContentSmartFilterBase clone = (ContentSmartFilterBase) super.clone();
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
