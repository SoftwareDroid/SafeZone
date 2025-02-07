package com.example.ourpact3.db;

import androidx.room.TypeConverter;

import com.example.ourpact3.smart_filter.NodeCheckStrategyType;

public class NodeCheckStrategyTypeConverter
{
    @TypeConverter
    public static NodeCheckStrategyType fromInteger(int value)
    {
        return NodeCheckStrategyType.fromValue(value);
    }

    @TypeConverter
    public static int toInteger(NodeCheckStrategyType action)
    {
        return action.getValue();
    }
}
