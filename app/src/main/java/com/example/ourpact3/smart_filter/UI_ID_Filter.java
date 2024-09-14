package com.example.ourpact3.smart_filter;

import android.view.accessibility.AccessibilityEvent;

import com.example.ourpact3.model.PipelineResultBase;
import com.example.ourpact3.model.PipelineResultExpFilter;
import com.example.ourpact3.service.ScreenInfoExtractor;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Set;


public class UI_ID_Filter extends SpecialSmartFilterBase
{
    public UI_ID_Filter(PipelineResultBase result, String name, @NotNull Set<String> setOfIDs)
    {
        super(new PipelineResultExpFilter(""), name);
        this.result = result;
        this.result.setTriggerFilter(name);
        this.name = name;
        this.sefOfIDs = Collections.unmodifiableSet(setOfIDs);
    }
    public final String name;
    public Set<String> sefOfIDs;
    public void setFilterIds(@NotNull Set<String> sefOfIDs)
    {
        this.sefOfIDs = sefOfIDs;
    }
    /**
     *
     * @param event
     * @return true if the pipeline should be stopped
     */
    public PipelineResultBase onAccessibilityEvent(AccessibilityEvent event)
    {
        return null;
    }

    @Override
    public PipelineResultBase onScreenEvent(@NotNull ScreenInfoExtractor.Screen screen)
    {
        if(screen.isScreenPartOfClass(sefOfIDs))
        {
            return this.result;
        }
        return null;
    }

    public PipelineResultBase onPipelineResult(PipelineResultBase result)
    {
        return null;
    }

    public void reset()
    {
    }
}

