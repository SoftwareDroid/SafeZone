package com.example.ourpact3.db;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import org.jetbrains.annotations.NotNull;

@Entity(tableName = "exception_list")
@TypeConverters({BooleanConverter.class})
public class ExceptionListEntity
{

    @PrimaryKey
    @NotNull
    private String packageName;

    private String appName;

    private boolean readable;

    private boolean writable;

    // Getters and setters
    @NonNull
    public String getPackageName()
    {
        return packageName;
    }

    public void setPackageName(@NonNull String packageName)
    {
        this.packageName = packageName;
    }

    public String getAppName()
    {
        return appName;
    }

    public void setAppName(@NonNull String appName)
    {
        this.appName = appName;
    }


    public boolean getReadable()
    {
        return readable;
    }

    public void setReadable(boolean readable)
    {
        this.readable = readable;
    }

    public boolean isWritable()
    {
        return writable;
    }

    public void setWritable(boolean writable)
    {
        this.writable = writable;
    }
}

