package com.example.ourpact3;

import android.view.accessibility.AccessibilityNodeInfo;

import com.example.ourpact3.model.PipelineResult;
import com.example.ourpact3.model.PipelineWindowAction;
import com.example.ourpact3.model.WordListFilterScored;
import com.example.ourpact3.model.WordProcessorFilterBase;

import java.util.ArrayList;
import java.util.TreeMap;
import java.util.Map;

public class KeywordScoreWindowCalculator
{
    private final TreeMap<String, Integer> wordToReadableCount = new TreeMap<>();
    private final TreeMap<String, Integer> wordToWriteableCount = new TreeMap<>();

    public String getDebugFilterState(AccessibilityNodeInfo node, AppKeywordFilter appRule, boolean isMagnificationEnabled)
    {
        wordToReadableCount.clear();
        wordToWriteableCount.clear();
        // collect all words
        processNode(node, isMagnificationEnabled);
        StringBuilder combinedDebugState = new StringBuilder("Debug State\n");
        int sumScore = 0;
        for (WordProcessorFilterBase filter : appRule.getAllFilters())
        {
            if (filter instanceof WordListFilterScored)
            {
                StringBuilder filterResultLines = new StringBuilder();
                WordListFilterScored scoredFilter = (WordListFilterScored) filter;

                for (Map.Entry<String, Integer> entry : wordToReadableCount.entrySet())
                {
                    scoredFilter.reset();
                    PipelineResult result = null;
                    for (int i = 0; i < entry.getValue(); i++)
                    {
                        result = scoredFilter.feedWord(entry.getKey(), false);
                    }
//                    if(result != null)
                    {

                        filterResultLines.append(getResultLine(entry.getValue(), entry.getKey(), scoredFilter.getCurrentScore() / entry.getValue(), true,""));
                        sumScore += scoredFilter.getCurrentScore();
                        result = null;
                    }
                }
                for (Map.Entry<String, Integer> entry : wordToWriteableCount.entrySet())
                {
                    scoredFilter.reset();
                    PipelineResult result = null;

                    for (int i = 0; i < entry.getValue(); i++)
                    {
                      result =   filter.feedWord(entry.getKey(), true);
                    }

//                    if(result != null)
                    {
                        filterResultLines.append(getResultLine(entry.getValue(), entry.getKey(), scoredFilter.getCurrentScore() / entry.getValue(), false,""));

                        sumScore += scoredFilter.getCurrentScore();
                        result = null;
                    }
                }
                if (sumScore != 0)
                {
                    combinedDebugState.append("Filter: ").append(filter.name).append("\n");
                    combinedDebugState.append("Sum: ").append(sumScore).append("\n");
                    combinedDebugState.append(filterResultLines);
                    sumScore = 0;
                }
            }

        }
        return combinedDebugState.toString(); // added return statement
    }

    private String getResultLine(int count, String word, int plusScore, boolean read, String topicTrigger)
    {
        if (count > 0 && word != null && plusScore != 0)
        {
            if (topicTrigger == null)
            {
                topicTrigger = "";
            }
            return String.valueOf(count) + " x " + word + " (" + (read ? "read" : "write") + ") \t +" + String.valueOf(plusScore) + " topic trigger: " + topicTrigger + "\n";
        }
        return "";
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