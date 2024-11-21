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
    private Instant sessionStart = null;
    private Instant sessionEnd = null;
    private long accumulatedSeconds = 0;
    private int resetPeriodInHours;
    private long limitInSeconds;
    private int numberOfAppUses;
    private boolean blocked;


    public TimeLimitFilter(PipelineResultBase result, String name, int resetPeriod, int limitInSeconds)
    {
        super(result, name);
        this.resetPeriodInHours = resetPeriod;
        this.limitInSeconds = limitInSeconds;
        this.blocked = false;
    }

    @Override
    public void onAppStateChange(boolean active)
    {
        if (blocked)
        {
            return;
        }
        if (active)
        {
            startSessionIfRequired();
        } else
        {
            stopSession();
        }
    }

    private void startSessionIfRequired()
    {
        // Start new sessio
        if (sessionEnd == null && sessionStart == null)
        {
            numberOfAppUses++;
            sessionStart = Instant.now();
            sessionEnd = null;
        }
    }

    private void stopSession()
    {
        // save the session accumalted as we only can have one session
        if (sessionStart != null && sessionEnd != null)
        {
            // do increase if limit already reached
            long timeSpend = Math.abs(Duration.between(sessionEnd, sessionStart).getSeconds()) + accumulatedSeconds;
            if (timeSpend < this.limitInSeconds)
            {
                sessionEnd = Instant.now();
                accumulatedSeconds += Math.abs(Duration.between(sessionEnd, sessionStart).getSeconds());
                sessionStart = null;
                sessionEnd = null;
            }
        }
    }

    private void resetMeasurement()
    {
        blocked = false;
        numberOfAppUses = 0;
        accumulatedSeconds = 0;
        sessionStart = null;
        sessionEnd = null;
        measurementEnd = Instant.now().plus(resetPeriodInHours, ChronoUnit.HOURS);
    }

    @Override
    public PipelineResultBase onAccessibilityEvent(AccessibilityEvent event)
    {
        startSessionIfRequired();
        sessionEnd = Instant.now();     //expand session
        if (measurementEnd == null || sessionEnd.isAfter(measurementEnd))
        {
            // start new session and measurement
            resetMeasurement();
            return null;
        }

        // Check if limit is exceeded
        long timeSpend = Math.abs(Duration.between(sessionEnd, sessionStart).getSeconds()) + accumulatedSeconds;
        if (blocked || timeSpend > this.limitInSeconds)
        {
            PipelineResultTimeBlock result = (PipelineResultTimeBlock) this.result.clone();
            result.usageLimit = limitInSeconds;
            result.usageTime = blocked ? limitInSeconds : timeSpend;
            result.resetPeriod = resetPeriodInHours;
            blocked = true;
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
