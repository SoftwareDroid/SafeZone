package com.example.ourpact3.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class WordListEntity
{
    @PrimaryKey(autoGenerate = true)
    private long id;
    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "version")
    private Integer version;

    @ColumnInfo(name = "app")
    private String app;

    public void setApp(String packageID)
    {
        this.app = packageID;
    }
    public String getApp(){return this.app;}

    public Integer getVersion()
    {
        return version;
    }

    public void setVersion(Integer version)
    {
        this.version = version;
    }

    // Getters and setters
    public long getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }
}
