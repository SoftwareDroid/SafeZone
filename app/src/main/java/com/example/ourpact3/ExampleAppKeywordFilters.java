package com.example.ourpact3;

import com.example.ourpact3.model.InvalidTopicIDException;
import com.example.ourpact3.model.Topic;
import com.example.ourpact3.model.TopicAlreadyExistsException;
import com.example.ourpact3.model.TopicLoaderCycleDetectedException;
import com.example.ourpact3.model.TopicManager;
import com.example.ourpact3.model.TopicMissingException;
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
    ExampleAppKeywordFilters(ContentFilterService service, TopicManager topicManager)
    {
        this.service = service;
        this.topicManager = topicManager;
    }

    private final ContentFilterService service;
    private final TopicManager topicManager;

    public void addExampleTopics() throws TopicLoaderCycleDetectedException, TopicAlreadyExistsException, InvalidTopicIDException
    {
        Topic adultTopic = new Topic("porn", "topics/en");
        adultTopic.setWords(new ArrayList<String>(List.of("porn", "femdom", "naked")));
        adultTopic.setIncludedTopics(new ArrayList<String>(List.of("female")));

        Topic adultChildTopic = new Topic("female", "topics/en");
        adultChildTopic.setWords(new ArrayList<String>(List.of("girl", "butt")));

        topicManager.addTopic(adultTopic);
        topicManager.addTopic(adultChildTopic);
    }

    private AppKeywordFilter getPocketCastsFilter() throws TopicMissingException
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
            TopicScoring sampleScoring = new TopicScoring("porn_explicit", 33, 50);
            WordListFilterScored blockAdultStuff = new WordListFilterScored("block adult stuff", new ArrayList<>(List.of(sampleScoring)), false, topicManager, pornResult);
            filters.add(blockAdultStuff);
        }
        return new AppKeywordFilter(service, topicManager, filters, appName);

    }

    private AppKeywordFilter getFirefoxFilter() throws TopicMissingException
    {
        String appName = "org.mozilla.firefox";
        ArrayList<WordProcessorFilterBase> filters = new ArrayList<WordProcessorFilterBase>();
        {
            // ignore suggestion screen
            PipelineResult resultIgnoreSearch = new PipelineResult();
            resultIgnoreSearch.windowAction = PipelineWindowAction.STOP_FURTHER_PROCESSING;
            resultIgnoreSearch.logging = true;
            // Add test Filter
            WordProcessorFilterBase ignoreSearch = new WordListFilterExact("null", new ArrayList<>(List.of("Firefox Suggest")), false, resultIgnoreSearch);
            filters.add(ignoreSearch);
        }
        {
            // ignore history page
            PipelineResult ignoreHistoryPage = new PipelineResult();
            ignoreHistoryPage.windowAction = PipelineWindowAction.STOP_FURTHER_PROCESSING;
            ignoreHistoryPage.logging = true;
            // Add test Filter
            WordProcessorFilterBase ignoreSearch = new WordListFilterExact("null", new ArrayList<>(List.of("History", "Recently closed tabs")), false, ignoreHistoryPage);
            filters.add(ignoreSearch);
        }
        {
            PipelineResult pornResult = new PipelineResult();
            pornResult.windowAction = PipelineWindowAction.PERFORM_BACK_ACTION_AND_WARNING;
            pornResult.logging = true;
            ArrayList<TopicScoring> allScorings = new ArrayList<>();
            allScorings.add(new TopicScoring("porn_explicit", 33, 50));
            allScorings.add(new TopicScoring("female_body_parts", 30, 45));
            allScorings.add(new TopicScoring("adult_nudity", 32, 45));
            allScorings.add(new TopicScoring("adult_sex_toys", 32, 45));
            allScorings.add(new TopicScoring("female_names", 15, 30));
            allScorings.add(new TopicScoring("female_clothing", 15, 30));
            allScorings.add(new TopicScoring("patrick_all_merged", 49, 66));
            boolean ignoreCase = true;  // important for porn filter

//            WordListFilterScored blockAdultStuff = new WordListFilterScored("Patricks block list", new ArrayList<>(List.of(myTerms,scoringFemaleClothing,scoringFemaleNames,scoringPorn,scoringFemaleBodyParts,scoringAdultNudity,scoringSexToys)), false, topicManager, pornResult);
            WordListFilterScored blockAdultStuff = new WordListFilterScored("Patricks block list", allScorings, ignoreCase, topicManager, pornResult);
            filters.add(blockAdultStuff);
        }
        return new AppKeywordFilter(service, topicManager, filters, appName);

    }

    private AppKeywordFilter getTelegramFilter2()
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

    public ArrayList<AppKeywordFilter> getAllExampleFilters() throws TopicMissingException
    {
        ArrayList<AppKeywordFilter> list = new ArrayList<>();
        list.add(getFirefoxFilter());
        list.add(getPocketCastsFilter());
        list.add(getTelegramFilter2());
        return list;
    }
}
