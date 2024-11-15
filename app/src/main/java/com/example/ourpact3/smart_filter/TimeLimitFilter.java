package com.example.ourpact3.smart_filter;

import android.view.accessibility.AccessibilityEvent;

import com.example.ourpact3.pipeline.PipelineResultBase;
import com.example.ourpact3.service.ScreenInfoExtractor;

public class TimeLimitFilter extends SpecialSmartFilterBase
{
    public TimeLimitFilter(PipelineResultBase result, String name)
    {
        super(result, name);

    }
    @Override
    public void onAppStateChange(boolean active)
    {
     if(active)
     {
         // start new session
     }
     else
     {
         // pause session
     }
    }

    @Override
    public PipelineResultBase onAccessibilityEvent(AccessibilityEvent event)
    {
        return null;
    }

    @Override
    public PipelineResultBase onScreenEvent(ScreenInfoExtractor.Screen screen)
    {
        return null;
    }

    @Override
    public PipelineResultBase onPipelineResult(PipelineResultBase result)
    {
        return null;
    }

    @Override
    public void reset()
    {

    }
}
