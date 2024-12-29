package com.example.ourpact3.ui.usage_restriction;

import android.content.Context;

import com.example.ourpact3.R;
import com.example.ourpact3.util.WeekDayToString;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

public class TimeRuleListEntry {
    private LocalTime start; // Start time
    private LocalTime end;   // End time
    private String additionalText; // String representation of weekdays
    private Context context;
    public TimeRuleListEntry(Context context,LocalTime start, LocalTime end, List<DayOfWeek> days, boolean blackList) {
        this.start = start;
        this.end = end;
        this.context = context;
        // saves space
        if(days.size() == 7)
        {
         this.additionalText = context.getString(R.string.all_days);
        }
        else
        {
            // Create a string representation of the weekdays
            this.additionalText = days.stream()
                    .map(day -> WeekDayToString.getShortForm(day, context)) // Use the short form function
                    .collect(Collectors.joining(", ")); // Join the short forms with a comma
        }
    }

    // Method to get the main text as a formatted string
    public String getTimeText() {
        return String.format("%02d:%02d",start.getHour(),start.getMinute()) + " - " + String.format("%02d:%02d",end.getHour(),end.getMinute()) ; // Format as "start - end"
    }

    public String getWeekdayText() {
        return additionalText; // Return the string representation of weekdays
    }

    // Getters for LocalTime
    public LocalTime getStart() {
        return start;
    }

    public LocalTime getEnd() {
        return end;
    }
}
