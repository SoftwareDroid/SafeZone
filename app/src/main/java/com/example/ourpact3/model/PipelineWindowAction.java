package com.example.ourpact3.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Buttons moved to own enum
 */
public enum PipelineWindowAction implements Parcelable {
//    PERFORM_HOME_BUTTON_AND_WARNING,
    WARNING,
    CONTINUE_PIPELINE, // Only this action doesn't abort the pipeline
    STOP_FURTHER_PROCESSING,
//    PERFORM_BACK_ACTION_AND_WARNING, // goes back and shows a warning
    END_OF_PIPE_LINE;

    // Parcelable implementation
    public static final Creator<PipelineWindowAction> CREATOR = new Creator<PipelineWindowAction>() {
        @Override
        public PipelineWindowAction createFromParcel(Parcel in) {
            return PipelineWindowAction.valueOf(in.readString());
        }

        @Override
        public PipelineWindowAction[] newArray(int size) {
            return new PipelineWindowAction[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name());
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
