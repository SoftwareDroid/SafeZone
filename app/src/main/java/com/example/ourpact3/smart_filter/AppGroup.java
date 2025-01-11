package com.example.ourpact3.smart_filter;

public enum AppGroup
{
    SINGLE_APP_USAGE_ONLY(5),
    ALL(10),
    WEB_BROWSER(20),
    NO_WEB_BROWSER(30);

    private final int value;

    AppGroup(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    // Method to convert int to enum
    public static AppGroup fromValue(int value) {
        for (AppGroup type : AppGroup.values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid value: " + value);
    }
}
