package com.example.ourpact3.model;

/**
 * Enum representing actions for pipeline buttons.
 */
public enum PipelineButtonAction {
    NONE(0),
    BACK_BUTTON(1),
    HOME_BUTTON(2);

    private final int value;

    PipelineButtonAction(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static PipelineButtonAction fromValue(int value) {
        for (PipelineButtonAction action : PipelineButtonAction.values()) {
            if (action.getValue() == value) {
                return action;
            }
        }
        throw new IllegalArgumentException("Invalid value: " + value);
    }
}
