package com.example.ourpact3.smart_filter;

import android.content.Context;

import com.example.ourpact3.R;
import com.example.ourpact3.util.WeekDayToString;

import java.time.LocalTime;
import java.time.DayOfWeek;
import java.time.Instant;
import java.util.EnumSet;
import java.time.ZonedDateTime;
import java.util.stream.Collectors;

public class ProductivityTimeRule
{
    private EnumSet<DayOfWeek> weekdays;
    private LocalTime startTime;
    private LocalTime endTime;
    private boolean isBlackListTimeMode; //if false when all
    public EnumSet<DayOfWeek> getWeekdays() {return weekdays;}
    public ProductivityTimeRule(LocalTime startTime, LocalTime endTime, EnumSet<DayOfWeek> weekdays, boolean isBlackListMode)
    {
        this.startTime = startTime;
        this.endTime = endTime;
        this.weekdays = weekdays;
        this.isBlackListTimeMode = isBlackListMode;
    }

    public String getTimeText() {
        return String.format("%02d:%02d",startTime.getHour(),startTime.getMinute()) + " - " + String.format("%02d:%02d",endTime.getHour(),endTime.getMinute()) ; // Format as "start - end"
    }

    public String getWeekdayText(Context context)
    {
        String ret;
        if(weekdays.size() == 7)
        {
            ret = context.getString(R.string.always);
        }
        else
        {
            // Create a string representation of the weekdays
            ret = weekdays.stream()
                    .map(day -> WeekDayToString.getShortForm(day, context)) // Use the short form function
                    .collect(Collectors.joining(", ")); // Join the short forms with a comma
        }
        return ret;
    }

    public boolean isBlackListTimeMode(){return isBlackListTimeMode;}
    public LocalTime getStartTime(){return startTime;}
    public LocalTime getEndTime(){return endTime;}

    public boolean isBlackListMode()
    {
        return this.isBlackListTimeMode;
    }

    public boolean hasWeekday(DayOfWeek day)
    {
        return weekdays.contains(day);
    }

    public boolean isRuleApplying(Instant timestamp)
    {
        ZonedDateTime zdt = timestamp.atZone(java.time.ZoneId.systemDefault());


        return weekdays.contains(zdt.getDayOfWeek()) && (zdt.toLocalTime().isAfter(startTime) && (zdt.toLocalTime().isBefore(endTime)));
    }
}
