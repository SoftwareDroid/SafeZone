package com.example.ourpact3.smart_filter;

import android.view.accessibility.AccessibilityEvent;

import com.example.ourpact3.pipeline.PipelineResultBase;
import com.example.ourpact3.pipeline.PipelineResultTimeBlock;
import com.example.ourpact3.service.ScreenInfoExtractor;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class TimeLimitFilter extends SpecialSmartFilterBase
{
    private Instant measurementEnd = null;
    private Instant sessionStart = Instant.now();
    private Instant sessionEnd = Instant.now();
    private long accumulatedSeconds = 0;
    private int duration;
    private long limitInSeconds;
    private int numberOfAppUses;


    public TimeLimitFilter(PipelineResultBase result, String name, int resetPeriod,int limitInSeconds)
    {
        super(result, name);
        this.duration = resetPeriod;
        this.limitInSeconds = limitInSeconds;
    }

    @Override
    public void onAppStateChange(boolean active)
    {
        if (active)
        {
            numberOfAppUses++;
        } else
        {
            saveSessionTime();
        }
    }

    private void saveSessionTime()
    {
        // save the session accumalted as we only can have one session
        accumulatedSeconds += Math.abs(Duration.between(sessionEnd,sessionStart).getSeconds());
        sessionStart = Instant.now();
        sessionEnd = Instant.now();
    }

    @Override
    public PipelineResultBase onAccessibilityEvent(AccessibilityEvent event)
    {
        sessionEnd = Instant.now();     //expand session
        if (measurementEnd == null)
        {
            // start new session
            numberOfAppUses = 0;
            accumulatedSeconds = 0;
            sessionStart = Instant.now();
            measurementEnd = sessionEnd.plus(duration, ChronoUnit.HOURS);
        } else
        {
            if (sessionEnd.isAfter(measurementEnd))
            {
                sessionEnd = null; // reset
            }
        }

        // Check if limit is exceeded
        long timeSpend = Math.abs(Duration.between(sessionEnd, sessionStart).getSeconds()) + accumulatedSeconds;
        if( timeSpend > this.limitInSeconds)
        {
            PipelineResultTimeBlock result = (PipelineResultTimeBlock) this.result.clone();
            result.usageLimit = limitInSeconds;
            result.usageTime = timeSpend;
            return result;
        }
        // Calculate
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
