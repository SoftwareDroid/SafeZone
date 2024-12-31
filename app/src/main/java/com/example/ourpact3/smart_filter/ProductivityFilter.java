package com.example.ourpact3.smart_filter;

import android.view.accessibility.AccessibilityEvent;

import com.example.ourpact3.pipeline.CounterAction;
import com.example.ourpact3.pipeline.PipelineResultBase;
import com.example.ourpact3.pipeline.PipelineResultProductivityFilter;
import com.example.ourpact3.service.ScreenInfoExtractor;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

/**
 * max number of starts and/or time in s every k hours
 */
public class ProductivityFilter extends SpecialSmartFilterBase
{
    public Long database_id = null;
    private final ArrayList<ProductivityTimeRule> timerules; // White or Blacklist for certain times of the week
    private Instant measurementEnd = null;
    private Instant sessionStart = null;
    private Instant sessionEnd = null;
    private long accumulatedSeconds = 0;
    private final long resetPeriodInSeconds;
    private final long limitInSeconds;
    private int numberOfAppUses;
    private boolean blocked;
    public final Integer maxNumberOfUsages;

    public final ArrayList<ProductivityTimeRule> getAllTimeRules()
    {
        return timerules;
    }

    public int getMaxNumberOfUsages()
    {
        return maxNumberOfUsages;
    }

    public long getResetPeriodInSeconds()
    {
        return resetPeriodInSeconds;
    }

    public long getLimitInSeconds()
    {
        return limitInSeconds;
    }

    public void setDatabaseID(long id)
    {
        this.database_id = id;
    }

    public ProductivityFilter(CounterAction counterAction, String name, long resetPeriodInSeconds, long limitInSeconds, Integer maxNumberOfUsages, ArrayList<ProductivityTimeRule> timeRules)
    {
        super(new PipelineResultProductivityFilter(counterAction), name);//PipelineResultBase
        this.resetPeriodInSeconds = resetPeriodInSeconds;
        this.limitInSeconds = limitInSeconds;
        this.blocked = false;
        this.maxNumberOfUsages = maxNumberOfUsages;
        this.timerules = timeRules;
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
            startSessionIfRequired(true);
        } else
        {
            stopSession();
        }
    }

    private void startSessionIfRequired(boolean isCountingAsNewStart)
    {
        // Start new session
        if (sessionEnd == null && sessionStart == null)
        {
            if (isCountingAsNewStart)
            {
                numberOfAppUses++;
            }
            sessionStart = Instant.now();
            sessionEnd = null;
        }
    }


    public void onScreenStateChange(boolean isScreenOn)
    {
        // Sessions should automatically be restarted with next ui events
        if (!isScreenOn)
        {
            //pause session if running
            stopSession();
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
        measurementEnd = Instant.now().plus(resetPeriodInSeconds, ChronoUnit.SECONDS);
    }

    private PipelineResultBase checkTimeRules(Instant currentTimestamp)
    {
        boolean isInWhiteList = false;
        boolean isWhiteListImportant = false;
        for (ProductivityTimeRule rule : this.timerules)
        {
            boolean isApplying = rule.isRuleApplying(currentTimestamp);
            if (!rule.isBlackListMode())
            {
                isWhiteListImportant = true;
            }
            // Block if one blacklist rule applies
            if (isApplying)
            {
                if (rule.isBlackListMode())
                {
                    PipelineResultProductivityFilter result = (PipelineResultProductivityFilter) this.result.clone();
                    result.isTimeRuleBlock = true;
                    return result;
                } else
                {
                    isInWhiteList = true;
                }
            }

        }
        // if we have whitelists is has to be in one otherwise it is blocked
        if (isWhiteListImportant && !isInWhiteList)
        {
            PipelineResultProductivityFilter result = (PipelineResultProductivityFilter) this.result.clone();
            result.isTimeRuleBlock = true;
            return result;
        }
        return null;
    }

    @Override
    public PipelineResultBase onAccessibilityEvent(AccessibilityEvent event)
    {
        startSessionIfRequired(true);
        sessionEnd = Instant.now();     //expand session
        // check time rules
        PipelineResultBase timeRuleResult = checkTimeRules(sessionEnd);
        if (timeRuleResult != null)
        {
            return timeRuleResult;
        }

        if (measurementEnd == null || sessionEnd.isAfter(measurementEnd))
        {
            // start new session and measurement
            resetMeasurement();
            return null;
        }
        // check number of usages first
        if (blocked || maxNumberOfUsages != null && numberOfAppUses > maxNumberOfUsages)
        {
            PipelineResultProductivityFilter result = (PipelineResultProductivityFilter) this.result.clone();
            result.numberOfUsages = numberOfAppUses;
            result.maxNumberOfUsages = maxNumberOfUsages;
            result.usageTime = 0;
            result.resetPeriod = 0;
            blocked = true;
            return result;
        }

        // Check if limit is exceeded
        long timeSpend = Math.abs(Duration.between(sessionEnd, sessionStart).getSeconds()) + accumulatedSeconds;
        if (blocked || timeSpend > this.limitInSeconds)
        {
            PipelineResultProductivityFilter result = (PipelineResultProductivityFilter) this.result.clone();
            result.usageLimitInSeconds = limitInSeconds;
            result.usageTime = blocked ? limitInSeconds : timeSpend;
            result.resetPeriod = resetPeriodInSeconds;
            result.maxNumberOfUsages = null;
            result.numberOfUsages = 0;
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
