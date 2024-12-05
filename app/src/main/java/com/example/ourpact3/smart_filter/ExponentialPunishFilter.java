package com.example.ourpact3.smart_filter;

import android.view.accessibility.AccessibilityEvent;

import com.example.ourpact3.model.PipelineButtonAction;
import com.example.ourpact3.pipeline.CounterAction;
import com.example.ourpact3.pipeline.PipelineResultBase;
import com.example.ourpact3.pipeline.PipelineResultExpFilter;
import com.example.ourpact3.model.PipelineWindowAction;
import com.example.ourpact3.service.ScreenInfoExtractor;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
/*
Only works with PERFORM_BACK_ACTION_AND_WARNING

 */
public class ExponentialPunishFilter extends SpecialSmartFilterBase
{
    private long blockTil;
    private long lastEventTime;
    private int minTimeBetweenIncreasingViolationCounterInMS = 1000;
    private final int violationExpiringInMin; // Time in Minutes violations are remembed
    private ArrayList<Violation> violationList = new ArrayList<>();
    private boolean isBlocking = false;
    private final int MIN_NEEDED_VIOLATATIONS;
    private final int MIN_TIME_OF_BLOCKING_IN_SECONDS;
    public ExponentialPunishFilter(String name, int MIN_NEEDED_VIOLATATIONS, int violationExpiringInMin, int MIN_TIME_OF_BLOCKING_IN_SECONDS, PipelineWindowAction action)
    {
        //Default action should be with warning but settings are then triggered to ofen and Warning is not closeable
        super(new PipelineResultExpFilter("",action), name);
        this.result.getCounterAction().setButtonAction(PipelineButtonAction.BACK_BUTTON);
        this.violationExpiringInMin = violationExpiringInMin;
        this.MIN_NEEDED_VIOLATATIONS = MIN_NEEDED_VIOLATATIONS;
        this.MIN_TIME_OF_BLOCKING_IN_SECONDS = MIN_TIME_OF_BLOCKING_IN_SECONDS;
    }

    private boolean hasOneKillingViolation()
    {
        for (Violation v : violationList)
        {
            if (v.haveToKill)
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public PipelineResultBase onAccessibilityEvent(AccessibilityEvent event)
    {
        switch (event.getEventType())
        {
            case AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED:
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
            case AccessibilityEvent.TYPE_WINDOWS_CHANGED:
                long currentTime = System.currentTimeMillis();
                if (isBlocking && currentTime < blockTil)
                {
                    PipelineResultExpFilter copy = (PipelineResultExpFilter) result.clone();
                    // if one violation could have killed then we show same behavior here
                    copy.getCounterAction().setKillState(hasOneKillingViolation() ? CounterAction.KillState.KILL_BEFORE_WINDOW : CounterAction.KillState.DO_NOT_KILL);
                    copy.blockedTil = blockTil;
                    copy.violationCounter = this.violationList.size();
                    return copy;
                } else if (isBlocking && currentTime >= blockTil)
                {
                    isBlocking = false;
                }
                break;
            default:
                break;
        }
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
        long currentTime = System.currentTimeMillis();
        if (result.getCounterAction().getWindowAction() == PipelineWindowAction.WARNING || result.getCounterAction().getButtonAction() == PipelineButtonAction.BACK_BUTTON || result.getCounterAction().getButtonAction() == PipelineButtonAction.HOME_BUTTON)
        {
            if (currentTime - this.lastEventTime > this.minTimeBetweenIncreasingViolationCounterInMS)
            {
                this.lastEventTime = currentTime;

                // Add new violation to list

                this.result.setScreen(result.getScreen()); // copy app name
                boolean haveToKill = result.getCounterAction().getKillState() == CounterAction.KillState.KILL_BEFORE_WINDOW;
                violationList.add(new Violation(currentTime, haveToKill));

                // Remove expired violations from list
                long expirationTime = currentTime - TimeUnit.MINUTES.toMillis(violationExpiringInMin);
                for (int i = 0; i < violationList.size(); i++)
                {
                    if (violationList.get(i).getTimestamp() < expirationTime)
                    {
                        violationList.remove(i);
                        i--;
                    }
                }

                // Update blocking state
                if (violationList.size() > MIN_NEEDED_VIOLATATIONS)
                {
                    isBlocking = true;
                    int totalViolations = violationList.size();
                    blockTil = currentTime + TimeUnit.SECONDS.toMillis(MIN_TIME_OF_BLOCKING_IN_SECONDS) + (long) Math.pow(3, totalViolations) * 1000;
                }
            }
        }
        return null;
    }

    public void reset()
    {

    }

    private static class Violation
    {
        private final long timestamp;
        public final boolean haveToKill;

        public Violation(long timestamp, boolean haveToKill)
        {
            this.timestamp = timestamp;
            this.haveToKill = haveToKill;
        }

        public long getTimestamp()
        {
            return timestamp;
        }
    }
}