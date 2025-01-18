package com.example.ourpact3.db;

import androidx.room.TypeConverter;

import com.example.ourpact3.model.PipelineButtonAction;

public class PipelineButtonActionConverter
{
    @TypeConverter
    public static PipelineButtonAction fromInteger(int value)
    {
        return PipelineButtonAction.fromValue(value);
    }

    @TypeConverter
    public static int toInteger(PipelineButtonAction action)
    {
        return action.getValue();
    }
}
