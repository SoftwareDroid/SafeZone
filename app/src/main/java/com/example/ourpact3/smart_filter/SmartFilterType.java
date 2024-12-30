package com.example.ourpact3.smart_filter;

// Different addable smart filters int conversion is needed for the database
public enum SmartFilterType {
    USAGE_RESTRICTION(10);

    private final int value;

    SmartFilterType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    // Method to convert int to enum
    public static SmartFilterType fromValue(int value) {
        for (SmartFilterType type : SmartFilterType.values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid value: " + value);
    }
}

