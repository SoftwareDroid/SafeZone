package com.example.ourpact3.model;

import android.view.accessibility.AccessibilityEvent;

public class ExponentialPunishFilter extends AppGenericEventFilterBase
{
    private long blockTil;
    private long lastEventTime;
    private int violationCounter;
    private int minBlockInSec;
    private int minTimeBetweenIncreasingViolationCounterInMS = 1000;

    public ExponentialPunishFilter(String name, int minBlockInSec)
    {
        super(new PipelineResultExpFilter(), name);
        violationCounter = 0;
        this.minBlockInSec = minBlockInSec;
    }

    @Override
    public PipelineResultBase OnAccessibilityEvent(AccessibilityEvent event)
    {
        switch (event.getEventType())
        {
            case AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED:
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
            case AccessibilityEvent.TYPE_WINDOWS_CHANGED:
            {
                long currentTime = System.currentTimeMillis();
                if (currentTime < blockTil)
                {
                    if (lastEventTime + minTimeBetweenIncreasingViolationCounterInMS < currentTime)
                    {
                        ((PipelineResultExpFilter) result).blockedTil = blockTil;
                        return result;
                    } else
                    {
                        return null;
                    }
                } else
                {
                    reset();
                }
                break;
            }
            default:


        }


        return null;
    }

    @Override
    public PipelineResultBase OnPipelineResult(PipelineResultBase result)
    {
        // we only get events here blocking is done later
        long currentTime = System.currentTimeMillis();
        if (result.windowAction == PipelineWindowAction.PERFORM_BACK_ACTION_AND_WARNING)
        {
            if (currentTime - this.lastEventTime > this.minTimeBetweenIncreasingViolationCounterInMS)
            {
                this.lastEventTime = currentTime;
                violationCounter++;
                this.blockTil = currentTime + 1000L * minBlockInSec + (long) Math.pow(3, violationCounter);
                return result;
            }
        }

        return null;
    }

    @Override
    public void reset()
    {
        this.blockTil = 0;
        violationCounter = 0;
    }
}
