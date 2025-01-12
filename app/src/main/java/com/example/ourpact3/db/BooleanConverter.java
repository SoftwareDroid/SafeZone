package com.example.ourpact3.db;

import androidx.room.TypeConverter;

public class BooleanConverter {
    @TypeConverter
    public static boolean fromInteger(int value) {
        return value == 1;
    }

    @TypeConverter
    public static int toInteger(boolean value) {
        return value ? 1 : 0;
    }
}
