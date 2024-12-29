package com.example.ourpact3.util;

import android.content.Context;

import com.example.ourpact3.R;

import java.time.DayOfWeek;

// Helper functions
public class WeekDayToString {

    // Function to get short form (e.g., Mo)
    public static String getShortForm(DayOfWeek day, Context context) {
        switch (day) {
            case MONDAY:
                return context.getString(R.string.short_monday); // Assuming you have a short form resource
            case TUESDAY:
                return context.getString(R.string.short_tuesday);
            case WEDNESDAY:
                return context.getString(R.string.short_wednesday);
            case THURSDAY:
                return context.getString(R.string.short_thursday);
            case FRIDAY:
                return context.getString(R.string.short_friday);
            case SATURDAY:
                return context.getString(R.string.short_saturday);
            case SUNDAY:
                return context.getString(R.string.short_sunday);
            default:
                return "";
        }
    }

    // Function to get long form (e.g., Monday)
    public static String getLongForm(DayOfWeek day, Context context) {
        switch (day) {
            case MONDAY:
                return context.getString(R.string.monday);
            case TUESDAY:
                return context.getString(R.string.tuesday);
            case WEDNESDAY:
                return context.getString(R.string.wednesday);
            case THURSDAY:
                return context.getString(R.string.thursday);
            case FRIDAY:
                return context.getString(R.string.friday);
            case SATURDAY:
                return context.getString(R.string.saturday);
            case SUNDAY:
                return context.getString(R.string.sunday);
            default:
                return "";
        }
    }
}

