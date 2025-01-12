package com.example.ourpact3.db;

import androidx.room.TypeConverter;

import com.example.ourpact3.model.PipelineWindowAction;

public class WindowActionConverter {
    @TypeConverter
    public static PipelineWindowAction fromInteger(int value) {
        return PipelineWindowAction.fromValue(value);
    }

    @TypeConverter
    public static int toInteger(PipelineWindowAction action) {
        return action.getValue();
    }
}
