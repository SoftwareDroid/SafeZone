package com.example.ourpact3.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

// Entity
@Entity(
        tableName = "time_restriction_rules",
        foreignKeys = @ForeignKey(
                entity = UsageFiltersEntity.class,
                parentColumns = "id",
                childColumns = "usage_filter_id",
                onDelete = ForeignKey.CASCADE
        )
)
@TypeConverters({BooleanConverter.class})
public class TimeRestrictionRulesEntity {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "usage_filter_id")
    private int usageFilterId;

    @ColumnInfo(name = "monday")
    private boolean monday;

    @ColumnInfo(name = "tuesday")
    private boolean tuesday;

    @ColumnInfo(name = "wednesday")
    private boolean wednesday;

    @ColumnInfo(name = "thursday")
    private boolean thursday;

    @ColumnInfo(name = "friday")
    private boolean friday;

    @ColumnInfo(name = "saturday")
    private boolean saturday;

    @ColumnInfo(name = "sunday")
    private boolean sunday;

    @ColumnInfo(name = "start_hour")
    private int startHour;

    @ColumnInfo(name = "start_min")
    private int startMin;

    @ColumnInfo(name = "end_hour")
    private int endHour;

    @ColumnInfo(name = "end_min")
    private int endMin;

    @ColumnInfo(name = "black_list")
    private boolean blackList;

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUsageFilterId() {
        return usageFilterId;
    }

    public void setUsageFilterId(int usageFilterId) {
        this.usageFilterId = usageFilterId;
    }

    public boolean isMonday() {
        return monday;
    }

    public void setMonday(boolean monday) {
        this.monday = monday;
    }

    public boolean isTuesday() {
        return tuesday;
    }

    public void setTuesday(boolean tuesday) {
        this.tuesday = tuesday;
    }

    public boolean isWednesday() {
        return wednesday;
    }

    public void setWednesday(boolean wednesday) {
        this.wednesday = wednesday;
    }

    public boolean isThursday() {
        return thursday;
    }

    public void setThursday(boolean thursday) {
        this.thursday = thursday;
    }

    public boolean isFriday() {
        return friday;
    }

    public void setFriday(boolean friday) {
        this.friday = friday;
    }

    public boolean isSaturday() {
        return saturday;
    }

    public void setSaturday(boolean saturday) {
        this.saturday = saturday;
    }

    public boolean isSunday() {
        return sunday;
    }

    public void setSunday(boolean sunday) {
        this.sunday = sunday;
    }

    public int getStartHour() {
        return startHour;
    }

    public void setStartHour(int startHour) {
        this.startHour = startHour;
    }

    public int getStartMin() {
        return startMin;
    }

    public void setStartMin(int startMin) {
        this.startMin = startMin;
    }

    public int getEndHour() {
        return endHour;
    }

    public void setEndHour(int endHour) {
        this.endHour = endHour;
    }

    public int getEndMin() {
        return endMin;
    }

    public void setEndMin(int endMin) {
        this.endMin = endMin;
    }

    public boolean isBlackList() {
        return blackList;
    }

    public void setBlackList(boolean blackList) {
        this.blackList = blackList;
    }
}

// DAO


