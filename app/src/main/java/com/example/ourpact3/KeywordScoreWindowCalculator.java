package com.example.ourpact3;

import android.view.accessibility.AccessibilityNodeInfo;

import com.example.ourpact3.model.WordListFilterScored;
import com.example.ourpact3.model.WordProcessorFilterBase;

import java.util.TreeMap;
import java.util.Map;
import java.util.TreeSet;

public class KeywordScoreWindowCalculator
{
    private final TreeMap<String, Integer> wordToReadableCount = new TreeMap<>();
    private final TreeMap<String, Integer> wordToWriteableCount = new TreeMap<>();
    private StringBuilder filterResultLines = new StringBuilder();
    private StringBuilder filterResultLines2 = new StringBuilder();

    public String getDebugFilterState(AccessibilityNodeInfo node, AppFilter appRule, boolean isMagnificationEnabled)
    {
        wordToReadableCount.clear();
        wordToWriteableCount.clear();
        filterResultLines = new StringBuilder();
        filterResultLines2 = new StringBuilder();

        // collect all words
        processNode(node, isMagnificationEnabled);
        StringBuilder combinedDebugState = new StringBuilder("Debug State\n");
        int sumScore = 0;
        for (WordProcessorFilterBase filter : appRule.getAllFilters())
        {
            if (filter instanceof WordListFilterScored)
            {
                WordListFilterScored scoredFilter = (WordListFilterScored) filter;

                for (Map.Entry<String, Integer> entry : wordToReadableCount.entrySet())
                {
                    scoredFilter.reset();
                    for (int i = 0; i < entry.getValue(); i++)
                    {
                        scoredFilter.feedWord(entry.getKey(), false);
                    }
                    addResultLine(entry.getValue(), entry.getKey(), scoredFilter.getCurrentScore() / entry.getValue(), true, scoredFilter.getTriggerWordsInTopic());
                    sumScore += scoredFilter.getCurrentScore();
                }
                for (Map.Entry<String, Integer> entry : wordToWriteableCount.entrySet())
                {
                    scoredFilter.reset();

                    for (int i = 0; i < entry.getValue(); i++)
                    {
                        filter.feedWord(entry.getKey(), true);
                    }

                    addResultLine(entry.getValue(), entry.getKey(), scoredFilter.getCurrentScore() / entry.getValue(), false, scoredFilter.getTriggerWordsInTopic());
                    sumScore += scoredFilter.getCurrentScore();
                }
                if (sumScore != 0)
                {
                    combinedDebugState.append("Filter: ").append(filter.name).append("\n");
                    combinedDebugState.append("Sum: ").append(sumScore).append("\n");
                    combinedDebugState.append("Found matches: \n");
                    combinedDebugState.append(filterResultLines);
                    combinedDebugState.append("\n\n=======Not matched======\n");
                    combinedDebugState.append(filterResultLines2);
                    sumScore = 0;
                }
            }

        }
        return combinedDebugState.toString(); // added return statement
    }

    private void addResultLine(int count, String word, int plusScore, boolean read, TreeSet<String> topicTriggers)
    {
        if (word != null)
        {
            if(count > 0 && plusScore != 0)
            {
                String number = count == 1 ? "" : " x " + String.valueOf(count) + " ";
                String text = number + "'" + topicTriggers.first() + "'" + " in " + word + " (" + (read ? "read" : "write") + ") \t => +" + String.valueOf(plusScore) +"\n";
                this.filterResultLines.append(text);
            }
            else
            {
                String text =  word + " (" + (read ? "read" : "write") + ")\n" ;
                this.filterResultLines2.append(text);
            }
        }
    }

    private void processNode(AccessibilityNodeInfo node, boolean isMagnificationEnabled)
    {
        if ((isMagnificationEnabled || node.isVisibleToUser()) && node.getText() != null && node.getText().length() > 1)
        {
            String text = node.getText().toString();
            if (node.isEditable())
            {
                wordToWriteableCount.merge(text, 1, Integer::sum);
            } else
            {
                wordToReadableCount.merge(text, 1, Integer::sum);
            }
        }
        // process all children for current processor
        int childCount = node.getChildCount();
        for (int n = 0; n < childCount; n++)
        {
            AccessibilityNodeInfo childNode = node.getChild(n);
            if (childNode != null)
            {
                processNode(childNode, isMagnificationEnabled);
            }
        }
    }
}