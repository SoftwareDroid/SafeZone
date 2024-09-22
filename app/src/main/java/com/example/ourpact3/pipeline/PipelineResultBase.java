package com.example.ourpact3.pipeline;
import android.os.Parcel;
import android.os.Parcelable;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;

import com.example.ourpact3.AppFilter;
import com.example.ourpact3.model.PipelineWindowAction;
import com.example.ourpact3.service.ScreenInfoExtractor;

public abstract class PipelineResultBase implements Cloneable
{
    @NonNull
    @Override
    public PipelineResultBase clone() {
        try {
            PipelineResultBase clone = (PipelineResultBase) super.clone();

            // Deep copy mutable fields
            if (this.windowAction != null) {
                clone.windowAction = this.windowAction;
            }
            if (this.triggerFilter != null) {
                clone.triggerFilter = this.triggerFilter; // Strings are immutable, but this is just for clarity
            }
            if (this.screen != null) {
                //TODO: perhaps clone in future
                clone.screen = this.screen; // Assuming ScreenTextExtractor.Screen has a clone method
            }
            if (this.currentAppFilter != null) {
                clone.currentAppFilter = this.currentAppFilter; // Shallow ref is OK
            }
            clone.killState = this.killState; // Enum is immutable
            clone.hasExplainableButton = this.hasExplainableButton;

            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(); // Can't happen since we are Cloneable
        }
    }


    public enum KillState
    {
        DO_NOT_KILL,
        KILL_BEFORE_WINDOW,
        KILLED // internal usage only
    }
    private PipelineHistory history; //TODO: irgendwie dranhängen usw auf aufbauen
    private PipelineWindowAction windowAction; // Changed to private
    private String triggerPackage; // Changed to private
    private String triggerFilter; // Changed to private
    private ScreenInfoExtractor.Screen screen; // Changed to private
    private AppFilter currentAppFilter; // Changed to private
    private KillState killState = KillState.DO_NOT_KILL; // Changed to private
    private boolean hasExplainableButton; // Changed to private
    public PipelineResultBase(String triggerPackage) {
        if (triggerPackage == null) {
            throw new IllegalArgumentException("triggerPackage cannot be null");
        }
        this.triggerPackage = triggerPackage;
    }
    /*
    // Parcelable implementation
    protected PipelineResultBase(Parcel in) {
        triggerPackage = in.readString();
        triggerFilter = in.readString();
        windowAction = in.readParcelable(PipelineWindowAction.class.getClassLoader());
        screen = in.readParcelable(ScreenInfoExtractor.Screen.class.getClassLoader());
        currentAppFilter = in.readParcelable(AppFilter.class.getClassLoader());
        killState = KillState.valueOf(in.readString());
        hasExplainableButton = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(triggerPackage);
        dest.writeString(triggerFilter);
        dest.writeParcelable(windowAction, flags);
        dest.writeParcelable(screen, flags);
        dest.writeParcelable(currentAppFilter, flags);
        dest.writeString(killState.name());
        dest.writeByte((byte) (hasExplainableButton ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PipelineResultBase> CREATOR = new Creator<PipelineResultBase>() {
        @Override
        public PipelineResultBase createFromParcel(Parcel in) {
            // You need to implement a way to create the specific subclass of PipelineResultBase
            // For example, if you have a subclass called PipelineResult, you would do:
            return new PipelineResult(in); // Replace with your actual subclass
        }

        @Override
        public PipelineResultBase[] newArray(int size) {
            return new PipelineResultBase[size];
        }
    };*/


    // Getters and Setters
    public PipelineWindowAction getWindowAction()
    {
        return windowAction;
    }

    public void setWindowAction(PipelineWindowAction windowAction)
    {
        this.windowAction = windowAction;
    }

    public String getTriggerPackage()
    {
        return triggerPackage;
    }

    public void setTriggerPackage(String triggerPackage)
    {
        this.triggerPackage = triggerPackage;
    }

    public String getTriggerFilter()
    {
        return triggerFilter;
    }

    public void setTriggerFilter(String triggerFilter)
    {
        this.triggerFilter = triggerFilter;
    }

    public ScreenInfoExtractor.Screen getScreen()
    {
        return screen;
    }

    public void setScreen(ScreenInfoExtractor.Screen screen)
    {
        this.screen = screen;
    }

    public AppFilter getCurrentAppFilter()
    {
        return currentAppFilter;
    }

    public void setCurrentAppFilter(AppFilter currentAppFilter)
    {
        this.currentAppFilter = currentAppFilter;
    }

    public KillState getKillState()
    {
        return killState;
    }

    public void setKillState(KillState killState)
    {
        this.killState = killState;
    }

    public boolean isHasExplainableButton()
    {
        return hasExplainableButton;
    }

    public void setHasExplainableButton(boolean hasExplainableButton)
    {
        this.hasExplainableButton = hasExplainableButton;
    }

    public abstract String getDialogTitle(Context ctx);

    public abstract String getDialogText(Context ctx);

    public String getAppName(Context ctx)
    {
        try
        {
            PackageManager packageManager = ctx.getPackageManager();
            ApplicationInfo appInfo = packageManager.getApplicationInfo(triggerPackage, 0);
            return (String) packageManager.getApplicationLabel(appInfo);
        } catch (Exception e)
        {
            return triggerPackage;
        }
    }
}
