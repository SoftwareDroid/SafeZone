package com.example.ourpact3;

import com.example.ourpact3.service.ScreenTextExtractor;
import com.example.ourpact3.filter.WordListFilterScored;
import com.example.ourpact3.filter.WordProcessorFilterBase;
import com.example.ourpact3.util.SubstringFinder;

import java.util.TreeSet;

public class KeywordScoreWindowCalculator
{
    private StringBuilder filterResultLines = new StringBuilder();
    private StringBuilder filterResultLines2 = new StringBuilder();

    public String getDebugFilterState(ScreenTextExtractor.Screen screen, AppFilter appRule)
    {
        if(appRule == null)
        {
            return "No Info AppRule is Missing";
        }
        filterResultLines = new StringBuilder();
        filterResultLines2 = new StringBuilder();

        // collect all words
        StringBuilder combinedDebugState = new StringBuilder("Found in Screen\n");
        int sumScore = 0;
        for (WordProcessorFilterBase filter : appRule.getAllFilters())
        {
            if (filter instanceof WordListFilterScored)
            {
                WordListFilterScored scoredFilter = (WordListFilterScored) filter;
                for(ScreenTextExtractor.Screen.TextNode n : screen.getTextNodes())
                {
                    scoredFilter.reset();
                    scoredFilter.feedWord(n);
                    addResultLine(1, n.text, scoredFilter.getCurrentScore(), !n.editable, scoredFilter.getTriggerWordsInTopic());
                    sumScore += scoredFilter.getCurrentScore();
                }


                if (sumScore != 0)
                {
                    combinedDebugState.append("Filter: ").append(filter.name).append("\n");
                    combinedDebugState.append("Sum: ").append(sumScore).append("\n");
                    combinedDebugState.append("Found matches: \n");
                    combinedDebugState.append(filterResultLines);
                    combinedDebugState.append("\n\nNot matched:\n");
                    combinedDebugState.append(filterResultLines2);
                    sumScore = 0;
                }
            }

        }
        return combinedDebugState.toString();
    }
    private void addResultLine(int count, String word, int plusScore, boolean read, TreeSet<String> topicTriggers)
    {
        if (word != null && !topicTriggers.isEmpty())
        {
            String shortendString =  SubstringFinder.findSubstringWithContext(word,topicTriggers.first(),5);;
            if(count > 0 && plusScore != 0)
            {
                String number = count == 1 ? "" : " x " + count + " ";
                String text = number + "'" + topicTriggers.first() + "'" + " in '" + shortendString + "' (" + (read ? "read" : "write") + ") \t => +" + String.valueOf(plusScore) +"\n";
                this.filterResultLines.append(text);
            }
            else
            {
                String text =  word + " (" + (read ? "read" : "write") + ")\n" ;
                this.filterResultLines2.append(text);
            }
        }
    }

}