package com.example.ourpact3.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.ourpact3.model.PipelineButtonAction;
import com.example.ourpact3.model.PipelineWindowAction;
import com.example.ourpact3.smart_filter.NodeCheckStrategyType;

@Entity(tableName = "ContentFilterEntity",
        foreignKeys = @ForeignKey(
                entity = WordListEntity.class,
                parentColumns = "id",
                childColumns = "word_list_id",
                onDelete = ForeignKey.CASCADE
        ))
@TypeConverters({WindowActionConverter.class,NodeCheckStrategyTypeConverter.class , PipelineButtonActionConverter.class,BooleanConverter.class})
public class ContentFilterEntity
{
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    // Counter action
    @ColumnInfo(name = "explainable")
    private boolean explainable;

    @ColumnInfo(name = "window_action")
    private PipelineWindowAction windowAction;

    @ColumnInfo(name = "button_action")
    private PipelineButtonAction buttonAction;

    @ColumnInfo(name = "kill")
    private boolean kill;

    // End of counter action

    @ColumnInfo(name = "enabled")
    private boolean enabled;

    @ColumnInfo(name = "user_created")
    private boolean userCreated;

    @ColumnInfo(name = "app_group")
    private String appGroup;

    @ColumnInfo(name = "readable")
    private boolean readable;

    @ColumnInfo(name = "writable")
    private boolean writable;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "short_description")
    private String shortDescription;

    @ColumnInfo(name = "checks_only_visible")
    private boolean checksOnlyVisible;

    @ColumnInfo(name = "what_to_check")
    private NodeCheckStrategyType whatToCheck;

    @ColumnInfo(name = "ignore_case")
    private boolean ignoreCase;

    @ColumnInfo(name = "word_list_id")
    private long wordListID;

    // getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean getExplainable() {
        return explainable;
    }

    public void setExplainable(boolean explainable) {
        this.explainable = explainable;
    }

    public PipelineWindowAction getWindowAction() {
        return windowAction;
    }

    public void setWindowAction(PipelineWindowAction windowAction) {
        this. windowAction = windowAction;
    }

    public PipelineButtonAction getButtonAction() {
        return buttonAction;
    }

    public void setButtonAction(PipelineButtonAction buttonAction) {
        this.buttonAction = buttonAction;
    }

    public boolean getKill() {
        return kill;
    }

    public void setKill(boolean kill) {
        this.kill = kill;
    }

    public boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean getUserCreated() {
        return userCreated;
    }

    public void setUserCreated(boolean userCreated) {
        this.userCreated = userCreated;
    }

    public String getAppGroup() {
        return appGroup;
    }

    public void setAppGroup(String appGroup) {
        this.appGroup = appGroup;
    }

    public boolean getReadable() {
        return readable;
    }

    public void setReadable(boolean readable) {
        this.readable = readable;
    }

    public boolean getWritable() {
        return writable;
    }

    public void setWritable(boolean writable) {
        this.writable = writable;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public boolean getChecksOnlyVisible() {
        return checksOnlyVisible;
    }

    public void setChecksOnlyVisible(boolean checksOnlyVisible) {
        this.checksOnlyVisible = checksOnlyVisible;
    }

    public NodeCheckStrategyType getWhatToCheck() {
        return whatToCheck;
    }

    public void setWhatToCheck(NodeCheckStrategyType whatToCheck) {
        this.whatToCheck = whatToCheck;
    }

    public boolean getIgnoreCase() {
        return ignoreCase;
    }

    public void setIgnoreCase(boolean ignoreCase) {
        this.ignoreCase = ignoreCase;
    }

    public long getWordListID() {
        return wordListID;
    }

    public void setWordListID(long wordListID) {
        this.wordListID = wordListID;
    }
}

