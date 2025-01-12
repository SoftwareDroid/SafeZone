package com.example.ourpact3.db;
import androidx.room.ColumnInfo;
import androidx.room.Database;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Entity(
        tableName = "apps",
        foreignKeys = @ForeignKey(
                entity = UsageFiltersEntity.class,
                parentColumns = "id",
                childColumns = "usage_filter_id",
                onDelete = ForeignKey.CASCADE
        )
)
public class AppsEntity {
    @PrimaryKey
    @ColumnInfo(name = "package_name")
    private String packageName;

    @ColumnInfo(name = "writable")
    private int writable;

    @ColumnInfo(name = "readable")
    private int readable;

    @ColumnInfo(name = "comment")
    private String comment;

    @ColumnInfo(name = "enabled")
    private int enabled;

    @ColumnInfo(name = "check_all_events")
    private int checkAllEvents;

    @ColumnInfo(name = "usage_filter_id")
    private int usageFilterId;

    // getters and setters
    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public int getWritable() {
        return writable;
    }

    public void setWritable(int writable) {
        this.writable = writable;
    }

    public int getReadable() {
        return readable;
    }

    public void setReadable(int readable) {
        this.readable = readable;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getEnabled() {
        return enabled;
    }

    public void setEnabled(int enabled) {
        this.enabled = enabled;
    }

    public int getCheckAllEvents() {
        return checkAllEvents;
    }

    public void setCheckAllEvents(int checkAllEvents) {
        this.checkAllEvents = checkAllEvents;
    }

    public int getUsageFilterId() {
        return usageFilterId;
    }

    public void setUsageFilterId(int usageFilterId) {
        this.usageFilterId = usageFilterId;
    }
}

