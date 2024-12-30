package com.example.ourpact3.smart_filter;

import java.time.LocalTime;
import java.time.DayOfWeek;
import java.time.Instant;
import java.util.EnumSet;
import java.time.ZonedDateTime;

public class ProductivityTimeRule
{
    private EnumSet<DayOfWeek> weekdays;
    private LocalTime startTime;
    private LocalTime endTime;
    private boolean isBlackListTimeMode; //if false when all

    public ProductivityTimeRule(LocalTime startTime, LocalTime endTime, EnumSet<DayOfWeek> weekdays, boolean isBlackListMode)
    {
        this.startTime = startTime;
        this.endTime = endTime;
        this.weekdays = weekdays;
        this.isBlackListTimeMode = isBlackListMode;
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
