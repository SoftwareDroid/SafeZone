package com.example.ourpact3.service;

import com.example.ourpact3.smart_filter.AppFilter;
import com.example.ourpact3.util.SubstringFinder;

import java.util.NoSuchElementException;
import java.util.TreeSet;

public class KeywordScoreWindowCalculator
{
    private StringBuilder filterResultLines = new StringBuilder();
    private StringBuilder filterResultLines2 = new StringBuilder();

    public String getDebugFilterState(ScreenInfoExtractor.Screen screen, AppFilter appRule)
    {
        if(appRule == null)
        {
            return "No Info AppRule is Missing";
        }
        filterResultLines = new StringBuilder();
        filterResultLines2 = new StringBuilder();

        return "Not State";
    }
    private void addResultLine(int count, String word, int plusScore, boolean read, TreeSet<String> topicTriggers)
    {
        try
        {
            if (word != null && !topicTriggers.isEmpty())
            {
                String shortendString = SubstringFinder.findSubstringWithContext(word, topicTriggers.first(), 5);
                ;
                if (count > 0 && plusScore != 0)
                {
                    String number = count == 1 ? "" : " x " + count + " ";
                    String text = number + "'" + topicTriggers.first() + "'" + " in '" + shortendString + "' (" + (read ? "read" : "write") + ") \t => +" + String.valueOf(plusScore) + "\n";
                    this.filterResultLines.append(text);
                } else
                {
                    String text = word + " (" + (read ? "read" : "write") + ")\n";
                    this.filterResultLines2.append(text);
                }
            }
        } catch (NoSuchElementException ignored)
        {
            // catch case to catch exceptions
        }
    }

}