package com.example.ourpact3.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Relation;
import androidx.room.TypeConverters;

import com.example.ourpact3.model.PipelineButtonAction;

import java.util.List;

@Entity(tableName = "usage_filters")
@TypeConverters({BooleanConverter.class, PipelineButtonActionConverter.class})
public class UsageFiltersEntity {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "explainable")
    private int explainable;

    @ColumnInfo(name = "window_action")
    private int windowAction;

    @ColumnInfo(name = "button_action")
    private PipelineButtonAction buttonAction;

    @ColumnInfo(name = "kill")
    private boolean kill;

    @ColumnInfo(name = "enabled")
    private boolean enabled;

    @ColumnInfo(name = "reset_period")
    private int resetPeriod;

    @ColumnInfo(name = "time_limit")
    private int timeLimit;

    @ColumnInfo(name = "max_starts")
    private int maxStarts;

    @Relation(parentColumn = "id", entityColumn = "usage_filter_id")
    public List<TimeRestrictionRulesEntity> timeRestrictions;

    // getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getExplainable() {
        return explainable;
    }

    public void setExplainable(int explainable) {
        this.explainable = explainable;
    }

    public int getWindowAction() {
        return windowAction;
    }

    public void setWindowAction(int windowAction) {
        this.windowAction = windowAction;
    }

    public PipelineButtonAction getButtonAction() {
        return buttonAction;
    }

    public void setButtonAction(PipelineButtonAction buttonAction) {
        this.buttonAction = buttonAction;
    }

    public boolean getKill() {
        return kill;
    }

    public void
    setKill(boolean kill) {
        this.kill = kill;
    }

    public boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getResetPeriod() {
        return resetPeriod;
    }

    public void setResetPeriod(int resetPeriod) {
        this.resetPeriod = resetPeriod;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }

    public int getMaxStarts() {
        return maxStarts;
    }

    public void setMaxStarts(int maxStarts) {
        this.maxStarts = maxStarts;
    }
}
