package com.example.ourpact3;
import com.example.ourpact3.AppKeywordFilter;
import com.example.ourpact3.model.Topic;
import com.example.ourpact3.model.TopicManager;
import com.example.ourpact3.model.WordListFilterScored.TopicScoring;
import com.example.ourpact3.model.PipelineResult;
import com.example.ourpact3.model.PipelineWindowAction;
import com.example.ourpact3.model.WordListFilterExact;
import com.example.ourpact3.model.WordProcessorFilterBase;
import com.example.ourpact3.model.WordListFilterScored;
import java.util.ArrayList;
import java.util.List;

public class ExampleAppKeywordFilters
{
    ExampleAppKeywordFilters(ContentFilerService service,TopicManager topicManager)
    {
        this.service = service;
        this.topicManager = topicManager;
    }
    private final ContentFilerService service;
    private final TopicManager topicManager;

    public void addExampleTopics()
    {
        Topic adultTopic = new Topic("porn", "topics/en");
        adultTopic.setWords(new ArrayList<String>(List.of("porn", "femdom", "naked")));
        adultTopic.setIncludedTopics(new ArrayList<String>(List.of("female")));

        Topic adultChildTopic = new Topic("female", "topics/en");
        adultChildTopic.setWords(new ArrayList<String>(List.of("girl", "butt")));

        topicManager.addTopic(adultTopic);
        topicManager.addTopic(adultChildTopic);
    }

    private AppKeywordFilter getPocketCastsFilter()
    {

        String appName = "au.com.shiftyjelly.pocketcasts";
        ArrayList<WordProcessorFilterBase> filters = new ArrayList<WordProcessorFilterBase>();
        {
            PipelineResult resultIgnoreSearch = new PipelineResult();
            resultIgnoreSearch.windowAction = PipelineWindowAction.STOP_FURTHER_PROCESSING;
            resultIgnoreSearch.logging = true;
            // Add test Filter
            WordProcessorFilterBase ignoreSearch = new WordListFilterExact("null", new ArrayList<>(List.of("Recent searches", "CLEAR ALL")), false, resultIgnoreSearch);
            filters.add(ignoreSearch);
        }
        {
            PipelineResult pornResult = new PipelineResult();
            pornResult.windowAction = PipelineWindowAction.PERFORM_BACK_ACTION;
            pornResult.logging = true;
            TopicScoring sampleScoring = new TopicScoring("porn", 100, 100);
            WordListFilterScored blockAdultStuff = new WordListFilterScored("block adult stuff", new ArrayList<>(List.of(sampleScoring)), false, topicManager, pornResult);
            filters.add(blockAdultStuff);
        }
        return new AppKeywordFilter(service, topicManager, filters, appName);

    }
    private AppKeywordFilter getTelegramFilter()
    {
        String appName = "org.telegram.messenger";
        ArrayList<WordProcessorFilterBase> filters = new ArrayList<WordProcessorFilterBase>();
        {
            PipelineResult resultIgnoreSearch = new PipelineResult();
            resultIgnoreSearch.windowAction = PipelineWindowAction.PERFORM_BACK_ACTION;
            resultIgnoreSearch.logging = true;
            // Add test Filter
            WordProcessorFilterBase ignoreSearch = new WordListFilterExact("Block people nearby", new ArrayList<>(List.of("People Nearby", "Make Myself Visible")), false, resultIgnoreSearch);
            filters.add(ignoreSearch);
        }
        return new AppKeywordFilter(service, topicManager, filters, appName);

    }

    public ArrayList<AppKeywordFilter> getAllExampleFilters()
    {
        ArrayList<AppKeywordFilter> list = new ArrayList<>();
        list.add(getTelegramFilter());
        list.add(getPocketCastsFilter());
        return list;
    }
}
