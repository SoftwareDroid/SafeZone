package com.example.ourpact3.db;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import org.jetbrains.annotations.NotNull;

@Entity(
        tableName = "AppEntity",
        foreignKeys = @ForeignKey(
                entity = UsageFiltersEntity.class,
                parentColumns = "id",
                childColumns = "usage_filter_id",
                onDelete = ForeignKey.CASCADE
        ),
        indices = @Index(value = {"usage_filter_id"})
)
@TypeConverters({BooleanConverter.class})
public class AppEntity
{
    @PrimaryKey
    @ColumnInfo(name = "package_name")
    @NotNull
    private String packageName;

    @ColumnInfo(name = "writable")
    private boolean writable;

    @ColumnInfo(name = "readable")
    private boolean readable;

    @ColumnInfo(name = "comment")
    private String comment;

    @ColumnInfo(name = "enabled")
    private boolean enabled;

    @ColumnInfo(name = "check_all_events")
    private boolean checkAllEvents;

    @ColumnInfo(name = "usage_filter_id")
    private int usageFilterId;

    // getters and setters
    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public boolean getWritable() {
        return writable;
    }

    public void setWritable(boolean writable) {
        this.writable = writable;
    }

    public boolean getReadable() {
        return readable;
    }

    public void setReadable(boolean readable) {
        this.readable = readable;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean getCheckAllEvents() {
        return checkAllEvents;
    }

    public void setCheckAllEvents(boolean checkAllEvents) {
        this.checkAllEvents = checkAllEvents;
    }

    public int getUsageFilterId() {
        return usageFilterId;
    }

    public void setUsageFilterId(int usageFilterId) {
        this.usageFilterId = usageFilterId;
    }
}

