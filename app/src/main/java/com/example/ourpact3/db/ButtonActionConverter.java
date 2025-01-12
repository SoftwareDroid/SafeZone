package com.example.ourpact3.db;

import androidx.room.TypeConverter;

import com.example.ourpact3.model.PipelineButtonAction;
import com.example.ourpact3.model.PipelineWindowAction;

public class ButtonActionConverter
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
