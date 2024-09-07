package com.example.ourpact3.filter;

import android.view.accessibility.AccessibilityEvent;

import com.example.ourpact3.model.PipelineResultBase;
import com.example.ourpact3.model.PipelineResultExpFilter;
import com.example.ourpact3.model.PipelineWindowAction;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class ExponentialPunishFilter extends AppGenericEventFilterBase
{
    private long blockTil;
    private long lastEventTime;
    private int minTimeBetweenIncreasingViolationCounterInMS = 1000;
    private int violationExpiringInMin;
    private ArrayList<Violation> violationList = new ArrayList<>();
    private boolean isBlocking = false;

    public ExponentialPunishFilter(String name, int minBlockInSec, int violationExpiringInMin)
    {
        super(new PipelineResultExpFilter(""), name);
        this.violationExpiringInMin = violationExpiringInMin;
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
    public PipelineResultBase OnAccessibilityEvent(AccessibilityEvent event)
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
                    copy.setKillState(hasOneKillingViolation() ? PipelineResultBase.KillState.KILL_BEFORE_WINDOW : PipelineResultBase.KillState.DO_NOT_KILL);
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
    public PipelineResultBase OnPipelineResult(PipelineResultBase result)
    {
        long currentTime = System.currentTimeMillis();
        if (result.getWindowAction() == PipelineWindowAction.PERFORM_BACK_ACTION_AND_WARNING)
        {
            if (currentTime - this.lastEventTime > this.minTimeBetweenIncreasingViolationCounterInMS)
            {
                this.lastEventTime = currentTime;

                // Add new violation to list
                this.result.setTriggerPackage(result.getTriggerPackage()); // last event defines the trigger package
                boolean haveToKill = result.getKillState() == PipelineResultBase.KillState.KILL_BEFORE_WINDOW;
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
                if (violationList.size() > 2)
                {
                    isBlocking = true;
                    int totalViolations = violationList.size();
                    blockTil = currentTime + TimeUnit.SECONDS.toMillis(violationExpiringInMin) + (long) Math.pow(3, totalViolations) * 1000;
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