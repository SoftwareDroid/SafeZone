package com.example.ourpact3.db;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import org.jetbrains.annotations.NotNull;

@Entity(
        tableName = "app_content_filter",
        primaryKeys = {"app_package_name", "content_filter_id"},
        foreignKeys = {
                @ForeignKey(
                        entity = AppEntity.class,
                        parentColumns = "package_name",
                        childColumns = "app_package_name",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = ContentFilterEntity.class,
                        parentColumns = "id",
                        childColumns = "content_filter_id",
                        onDelete = ForeignKey.CASCADE
                ),
        },
        indices = @Index(value = {"content_filter_id"})
)
public class AppContentFilterEntity {
    @ColumnInfo(name = "app_package_name")
    @NotNull
    private String appPackageName;

    @ColumnInfo(name = "content_filter_id")
    private int contentFilterId;

    // getters and setters
    @NonNull
    public String getAppPackageName() {
        return appPackageName;
    }

    public void setAppPackageName(String appPackageName) {
        this.appPackageName = appPackageName;
    }

    public int getContentFilterId() {
        return contentFilterId;
    }

    public void setContentFilterId(int contentFilterId) {
        this.contentFilterId = contentFilterId;
    }
}
