package com.example.ourpact3;

import com.example.ourpact3.model.FilerAppAction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WordListFilterExact extends WordProcessorFilterBase {
    WordListFilterExact(ArrayList<String> listOfWords, boolean ignoreCase, ArrayList<FilerAppAction> actions) {
        super(actions);
        for (String word : listOfWords) {
            if(ignoreCase)
            {
                this.map.put(word.toLowerCase(), 0);
            }
        }
        this.ignoreCase = ignoreCase;
    }
    private boolean ignoreCase;
    private HashMap<String, Integer> map = new HashMap<>();

    public boolean feedWord(String text, boolean editable) {
        if(ignoreCase)
        {
            text = text.toLowerCase();
        }

        Integer hits = this.map.get(text);
        if (hits != null) {
            // Update
            map.put(text, hits + 1);
            if (this.isFinished())
            {
                return true;
            }

        }


        return false;
}

public boolean isFinished()
{
    for (Map.Entry<String, Integer> entry : map.entrySet())
    {
        if( entry.getValue() != 1)
        {
            return true;
        }
    }
    return false;
}

public void reset() {
    for (String key : map.keySet()) {
        map.put(key, 0);
    }
}
}
