package com.example.ourpact3.pipeline;

import android.content.Context;
import com.example.ourpact3.R; // Adjust the package name as necessary

import androidx.annotation.NonNull;

public class PipelineResultKeywordFilter extends PipelineResultBase
{
    public String inputTriggerWord;

    public PipelineResultKeywordFilter(String triggerPackage)
    {
        super(triggerPackage);
    }

    @NonNull
    @Override
    public PipelineResultBase clone()
    {
        // Call the superclass clone method to get a deep copy of the base class fields
        PipelineResultKeywordFilter cloned = (PipelineResultKeywordFilter) super.clone();

        // Deep copy the additional field if necessary
        // If additionalField is mutable, you would need to clone it as well
        // For example, if it were an object, you would do something like:
        // cloned.additionalField = new String(this.additionalField); // If it's a String, this is not needed since String is immutable

        // If additionalField were a mutable object, you would clone it here
        cloned.inputTriggerWord = this.inputTriggerWord; // Just a direct assignment since String is immutable

        return cloned;
    }


    @Override
    public String getDialogTitle(Context ctx)

    {
        return "Keyword Block: " + getAppName(ctx);
    }

    @Override
    public String getDialogText(Context ctx)
    {

        if (this.getKillState() == KillState.KILLED) {
            return ctx.getString(R.string.app_killed);
        }
        return ctx.getString(R.string.app_blocked);
    }
}
