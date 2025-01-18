package com.example.ourpact3.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

@Entity(
        tableName = "word_list",
        foreignKeys = @ForeignKey(
                entity = LanguageEntity.class,
                parentColumns = "id",
                childColumns = "language_id",
                onDelete = ForeignKey.CASCADE
        )
)
@TypeConverters({BooleanConverter.class})
public class WordListEntity {
    @PrimaryKey
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "text")
    private String text;

    @ColumnInfo(name = "language_id")
    private int languageId;

    @ColumnInfo(name = "is_regex")
    private boolean isRegex;

    @ColumnInfo(name = "topic_type")
    private int topicType;

    @ColumnInfo(name = "topic_id")
    private int topicId;

    // getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getLanguageId() {
        return languageId;
    }

    public void setLanguageId(int languageId) {
        this.languageId = languageId;
    }

    public boolean isRegex() {
        return isRegex;
    }

    public void setRegex(boolean regex) {
        isRegex = regex;
    }

    public int getTopicType() {
        return topicType;
    }

    public void setTopicType(int topicType) {
        this.topicType = topicType;
    }

    public int getTopicId() {
        return topicId;
    }

    public void setTopicId(int topicId) {
        this.topicId = topicId;
    }
}

