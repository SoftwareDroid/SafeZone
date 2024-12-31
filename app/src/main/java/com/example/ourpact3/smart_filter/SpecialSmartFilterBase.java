package com.example.ourpact3.smart_filter;

import android.view.accessibility.AccessibilityEvent;

import com.example.ourpact3.pipeline.CounterAction;
import com.example.ourpact3.pipeline.PipelineResultBase;
import com.example.ourpact3.service.ScreenInfoExtractor;

public abstract class SpecialSmartFilterBase
{
    /**
     * Note the order defines the executation and is important
     */
    public enum Name
    {
        LEARNED_GOOD,
        LEARNED_BAD,
        EXP_PUNISH,
        USAGE_RESTRICTION
    }

    SpecialSmartFilterBase(PipelineResultBase result, String name)
    {
        this.result = result;
        this.result.setTriggerFilter(name);
        this.name = name;
    }

    protected PipelineResultBase result;
    private boolean enabled = true;

    public boolean isEnabled()
    {
        return enabled;
    }

    public void setEnabled(boolean v)
    {
        this.enabled = v;
    }

    public final String name;

    public CounterAction getCounterAction()
    {
        return result.getCounterAction();
    }

    public void onScreenStateChange(boolean on)
    {
    }

    public void onAppStateChange(boolean active)
    {
    }

    /**
     * @param event
     * @return true if the pipeline should be stopped
     */
    public abstract PipelineResultBase onAccessibilityEvent(AccessibilityEvent event);

    public abstract PipelineResultBase onScreenEvent(ScreenInfoExtractor.Screen screen);

    /**
     * Gets called then an other filter completes
     *
     * @param result
     * @return true if the pipeline should be stopped
     */
    public abstract PipelineResultBase onPipelineResult(PipelineResultBase result);

    public abstract void reset();
}
