package com.example.ourpact3.smart_filter;

// Different addable smart filters int conversion is needed for the database
public enum NodeCheckStrategyType
{
    WRITE_ONLY(5),
    READ_ONLY(10),
    ALL(15);
    private final int value;

    NodeCheckStrategyType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    // Method to convert int to enum
    public static NodeCheckStrategyType fromValue(int value) {
        for (NodeCheckStrategyType type : NodeCheckStrategyType.values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid value: " + value);
    }
}

