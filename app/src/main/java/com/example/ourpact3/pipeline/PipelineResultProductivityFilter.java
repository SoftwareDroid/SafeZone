package com.example.ourpact3.pipeline;

import android.content.Context;

import com.example.ourpact3.model.PipelineWindowAction;

public class PipelineResultProductivityFilter
        extends PipelineResultBase
{
    public PipelineResultProductivityFilter(CounterAction action)
    {
        this.setCounterAction(action);
    }
    public Integer maxNumberOfUsages;
    public int numberOfUsages;
    public long usageTime;
    public long usageLimitInSeconds;
    public int resetPeriod;
    public boolean isTimeRuleBlock;

    @Override
    public String getDialogTitle(Context ctx)
    {
        return "Usage Blocked " + getAppName(ctx);
    }

    @Override
    public String getDialogText(Context ctx)
    {
        if(isTimeRuleBlock)
        {
            return "This app is blocked due to a time rule";
        }

        if(maxNumberOfUsages != null && numberOfUsages > maxNumberOfUsages)
        {
         return "This app was used too much. Number of starts " + numberOfUsages + " exceeded the limit of " + maxNumberOfUsages;
        }
        else
        {
            return "This app was used too much. Usage limit reached of " + usageLimitInSeconds + "s in " + resetPeriod + "hours.";
        }
    }
}
