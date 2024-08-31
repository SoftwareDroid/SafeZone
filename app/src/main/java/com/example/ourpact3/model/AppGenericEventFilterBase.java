package com.example.ourpact3.model;

import android.view.accessibility.AccessibilityEvent;

public abstract class AppGenericEventFilterBase
{
    AppGenericEventFilterBase(PipelineResultBase result, String name)
    {
        this.result = result;
        this.result.triggerFilter = name;
        this.name = name;
    }
    protected PipelineResultBase result;
    public final String name;

    /**
     *
     * @param event
     * @return true if the pipeline should be stopped
     */
    public abstract PipelineResultBase OnAccessibilityEvent(AccessibilityEvent event);

    /**
     * Gets called then an other filter completes
     * @param result
     * @return true if the pipeline should be stopped
     */
    public abstract PipelineResultBase OnPipelineResult(PipelineResultBase result);
    public abstract void reset();
}
