package com.example.ourpact3.pipeline;

import android.content.Context;

import androidx.annotation.NonNull;

import com.example.ourpact3.R;

public class PipelineResultLearnedMode extends PipelineResultBase
{
    public PipelineResultLearnedMode(String triggerPackage)
    {
//        super(triggerPackage);
    }
    @NonNull
    @Override
    public PipelineResultBase clone()
    {
        // Call the superclass clone method to get a deep copy of the base class fields
        PipelineResultLearnedMode cloned = (PipelineResultLearnedMode) super.clone();

        // Deep copy the additional field if necessary
        // If additionalField is mutable, you would need to clone it as well
        // For example, if it were an object, you would do something like:
        // cloned.additionalField = new String(this.additionalField); // If it's a String, this is not needed since String is immutable

        // If additionalField were a mutable object, you would clone it here

        return cloned;
    }
    @Override
    public String getDialogTitle(Context ctx)

    {
        return ctx.getString(R.string.message_title_blocked_by_learned) + getAppName(ctx);
    }

    @Override
    public String getDialogText(Context ctx)
    {

        if (this.getKillState() == KillState.KILLED) {
            return ctx.getString(R.string.app_killed);
        }
        return ctx.getString(R.string.message_body_blocked_by_learned);
    }
}
