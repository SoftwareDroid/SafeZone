package com.example.ourpact3.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

/**
 * Contains scored and not scored entries
 */
@Entity(
        tableName = "WordEntity",
        foreignKeys = {@ForeignKey(
                entity = LanguageEntity.class,
                parentColumns = "id",
                childColumns = "language_id",
                onDelete = ForeignKey.CASCADE
        ), @ForeignKey(
                entity = WordListEntity.class,
                parentColumns = "id",
                childColumns = "word_list_id",
                onDelete = ForeignKey.CASCADE
        )},

        indices = @Index(value = {"language_id"})
)
@TypeConverters({BooleanConverter.class})
public class WordEntity
{
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "text")
    private String text;

    @ColumnInfo(name = "language_id")
    private long languageId;

    @ColumnInfo(name = "read_score")
    private Integer readScore;

    @ColumnInfo(name = "write_score")
    private Integer writeScore;

    @ColumnInfo(name = "group_number")
    private int expressionGroupNumber;

    @ColumnInfo(name = "is_regex")
    private boolean isRegex;

    public static final int TOPIC_SCORED = 1;
    public static final int TOPIC_EXACT = 2;
    @ColumnInfo(name = "topic_type")
    private int topicType;

    @ColumnInfo(name = "word_list_id")
    private long wordListID;

    public long getWordListID()
    {
        return wordListID;
    }

    public void setWordListID(long wordListID)
    {
        this.wordListID = wordListID;
    }

    // getters and setters
    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getText()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;
    }

    public int getReadScore()
    {
        return readScore;
    }

    public void setReadScore(Integer readScore)
    {
        this.readScore = readScore;
    }

    public Integer getWriteScore()
    {
        return writeScore;
    }

    public void setWriteScore(int writeScore)
    {
        this.writeScore = writeScore;
    }

    public int getExpressionGroupNumber()
    {
        return expressionGroupNumber;
    }

    public void setExpressionGroupNumber(int expressionGroupNumber)
    {
        this.expressionGroupNumber = expressionGroupNumber;
    }

    public long getLanguageId()
    {
        return languageId;
    }

    public void setLanguageId(long languageId)
    {
        this.languageId = languageId;
    }

    public boolean isRegex()
    {
        return isRegex;
    }

    public void setRegex(boolean regex)
    {
        isRegex = regex;
    }

    public int getTopicType()
    {
        return topicType;
    }

    public void setTopicType(int topicType)
    {
        this.topicType = topicType;
    }
}

