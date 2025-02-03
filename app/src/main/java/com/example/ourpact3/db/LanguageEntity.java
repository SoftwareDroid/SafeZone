package com.example.ourpact3.db;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

@Entity(tableName = "LanguageEntity")
@TypeConverters({BooleanConverter.class})
public class LanguageEntity
{
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String longLanguageCode;
    private String shortLanguageCode;
    private boolean enabled;

    // Getters and setters
    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public boolean isEnabled()
    {
        return enabled;
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    public String getLongLanguageCode()
    {
        return longLanguageCode;
    }

    public void setLongLanguageCode(String longLanguageCode)
    {
        this.longLanguageCode = longLanguageCode;
    }

    public String getShortLanguageCode()
    {
        return shortLanguageCode;
    }

    public void setShortLanguageCode(String shortLanguageCode)
    {
        this.shortLanguageCode = shortLanguageCode;
    }
}
