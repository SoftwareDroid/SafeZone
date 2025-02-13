package com.example.ourpact3.unused;

import com.example.ourpact3.pipeline.PipelineResultBase;
import com.example.ourpact3.smart_filter.SpecialSmartFilterBase;
import com.example.ourpact3.smart_filter.ContentSmartFilter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PipelineHistory
{
    //
    public class ResultWithExplaination
    {
        PipelineResultBase result;  //gff null aber es gibt dennoch ein kommentar
        String explainationShort = "";
        String resultIconChar = ""; //e.g Skull for killing
    }

    private ArrayList<SpecialSmartFilterBase> runSmartFilters1;
    private ArrayList<ContentSmartFilter> runSmartFilters2;
    public int number = 0;

    public List<ContentSmartFilter> getSmartFilters2()
    {
        return Collections.unmodifiableList(runSmartFilters2);
    }

    public List<SpecialSmartFilterBase> getSpecialFilters()
    {
        return Collections.unmodifiableList(runSmartFilters1);
    }

    PipelineHistory()
    {

    }

    /**
     * @param filter
     */
    public void addSpecialFilter(SpecialSmartFilterBase filter, ResultWithExplaination resultWithExplaination)
    {
        number += 1;
    }

    public void addWorProcessorFilter(ContentSmartFilter filter, ResultWithExplaination resultWithExplaination)
    {
        runSmartFilters2.add(filter);
    }
}
