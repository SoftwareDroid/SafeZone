package com.example.ourpact3.db;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class LanguageEntity {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String longLanguageCode;
    private String shortLanguageCode;

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLongLanguageCode() {
        return longLanguageCode;
    }

    public void setLongLanguageCode(String longLanguageCode) {
        this.longLanguageCode = longLanguageCode;
    }

    public String getShortLanguageCode() {
        return shortLanguageCode;
    }

    public void setShortLanguageCode(String shortLanguageCode) {
        this.shortLanguageCode = shortLanguageCode;
    }
}

