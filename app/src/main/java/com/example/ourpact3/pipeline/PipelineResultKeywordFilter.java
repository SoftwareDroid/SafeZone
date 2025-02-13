package com.example.ourpact3.pipeline;

import android.content.Context;
import com.example.ourpact3.R; // Adjust the package name as necessary
import com.example.ourpact3.db.ContentFilterEntity;
import com.example.ourpact3.db.WordEntity;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class PipelineResultKeywordFilter extends PipelineResultBase
{
    public PipelineResultKeywordFilter(ContentFilterEntity filter)
    {
        this.filter = filter;
    }
    public final ContentFilterEntity filter; // information but the filter
    public ArrayList<WordEntity> triggerWords = new ArrayList<>();

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

        if (this.getCounterAction().getKillState() == CounterAction.KillState.KILLED) {
            return ctx.getString(R.string.app_killed);
        }
        return ctx.getString(R.string.app_blocked);
    }
}
