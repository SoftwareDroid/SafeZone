package com.example.ourpact3.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Buttons moved to own enum
 */
public enum PipelineWindowAction implements Parcelable {
    WARNING(0),
    CONTINUE_PIPELINE(1), // Only this action doesn't abort the pipeline
    STOP_FURTHER_PROCESSING(2),
    END_OF_PIPE_LINE(3);

    private final int value;

    PipelineWindowAction(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static PipelineWindowAction fromValue(int value) {
        for (PipelineWindowAction action : PipelineWindowAction.values()) {
            if (action.getValue() == value) {
                return action;
            }
        }
        throw new IllegalArgumentException("Invalid value: " + value);
    }

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
