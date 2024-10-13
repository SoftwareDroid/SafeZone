package com.example.ourpact3.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.ourpact3.service.AppPermission;

import org.jetbrains.annotations.NotNull;

@Entity(tableName = "app_info")
public class AppInfo {
    @NotNull
    @PrimaryKey
    public String packageId = "";

    @ColumnInfo(name = "state")
    public AppPermission state;

    @ColumnInfo(name = "locked_until")
    public long lockedUntil;
}


