package com.example.ourpact3.util;

public class SubstringFinder {

    public static String findSubstringWithContext(String text, String substring, int n) {
        StringBuilder result = new StringBuilder();
        int index = text.indexOf(substring);

        // If the substring is not found, return an empty string
        if (index == -1) {
            return "";
        }

        // Calculate the start and end indices for the substring
        int start = Math.max(0, index - n);
        int end = Math.min(text.length(), index + substring.length() + n); // +n to include n after the substring

        // Append "..." if there are characters before the start
        if (start > 0) {
            result.append("...");
        }

        // Append the substring with context
        result.append(text.substring(start, end));

        // Append "..." if there are characters after the end
        if (end < text.length()) {
            result.append("...");
        }

        return result.toString();
    }

}
