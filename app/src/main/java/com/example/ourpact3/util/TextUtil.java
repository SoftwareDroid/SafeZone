package com.example.ourpact3.util;

import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;

public class TextUtil {

    /**
     * Appends text and highlights a specific word with the specified color and boldness.
     *
     * @param originalText    The original text to display.
     * @param wordToHighlight The word to highlight.
     * @param color           The color to apply to the highlighted word.
     * @param isBold         Whether to apply bold style to the highlighted word.
     * @return A SpannableStringBuilder with the highlighted word.
     */
    public static SpannableStringBuilder appendText(String originalText, String wordToHighlight, int color, boolean isBold) {
        // Create a SpannableStringBuilder
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();

        // Append the original text
        spannableStringBuilder.append(originalText);

        // Find the start index of the word to highlight
        int startIndex = originalText.indexOf(wordToHighlight);
        int endIndex = startIndex + wordToHighlight.length();

        // Check if the word is found
        if (startIndex != -1) {
            // Apply color to the word
            spannableStringBuilder.setSpan(new ForegroundColorSpan(color), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            // Apply bold style if specified
            if (isBold) {
                spannableStringBuilder.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }

        return spannableStringBuilder;
    }
}

