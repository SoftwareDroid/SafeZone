package com.example.ourpact3.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

/**
 * Gives an app k different content_filters which are reusable between apps and have a priority per app
 */

@Entity(
        tableName = "ContentFilterToAppEntity",
        foreignKeys = {@ForeignKey(
                entity = ContentFiltersEntity.class,
                parentColumns = "id",
                childColumns = "content_filter_id",
                onDelete = ForeignKey.CASCADE
        ), @ForeignKey(
                entity = AppEntity.class,
                parentColumns = "package_name",
                childColumns = "package_name",
                onDelete = ForeignKey.CASCADE
        )},
        indices = {}
)
public class ContentFilterToAppEntity
{
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "content_filter_id")
    private long contentFilterID;

    @ColumnInfo(name = "package_name")
    private String packageName;

    @ColumnInfo(name = "priority")
    private int priority;


    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public long getContentFilterID()
    {
        return contentFilterID;
    }

    public void setContentFilterID(long contentFilterID)
    {
        this.contentFilterID = contentFilterID;
    }

    public String getPackageName()
    {
        return packageName;
    }

    public void setPackageName(String packageName)
    {
        this.packageName = packageName;
    }

    public int getPriority()
    {
        return priority;
    }

    public void setPriority(int priority)
    {
        this.priority = priority;
    }

}
