package com.example.ourpact3;

import com.example.ourpact3.model.ExponentialPunishFilter;
import com.example.ourpact3.model.InvalidTopicIDException;
import com.example.ourpact3.model.PipelineResultKeywordFilter;
import com.example.ourpact3.model.TopicAlreadyExistsException;
import com.example.ourpact3.model.TopicLoaderCycleDetectedException;
import com.example.ourpact3.model.TopicManager;
import com.example.ourpact3.model.TopicMissingException;
import com.example.ourpact3.model.WordListFilterScored.TopicScoring;
import com.example.ourpact3.model.PipelineResultBase;
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
//        Topic adultTopic = new Topic("porn", "topics/en");
//        adultTopic.setWords(new ArrayList<String>(List.of("porn", "femdom", "naked")));
//        adultTopic.setIncludedTopics(new ArrayList<String>(List.of("female")));
//
//        Topic adultChildTopic = new Topic("female", "topics/en");
//        adultChildTopic.setWords(new ArrayList<String>(List.of("girl", "butt")));

//        topicManager.addTopic(adultTopic);
//        topicManager.addTopic(adultChildTopic);
    }

    private AppFilter getSettings()
    {
        String appName = "com.android.settings";
        ArrayList<WordProcessorFilterBase> filters = new ArrayList<WordProcessorFilterBase>();
        // prevent user for disabling the accessabilty service (only works in english)
        {
            PipelineResultKeywordFilter preventDisabelingAccessabilty = new PipelineResultKeywordFilter();
            preventDisabelingAccessabilty.windowAction = PipelineWindowAction.PERFORM_BACK_ACTION;
            preventDisabelingAccessabilty.hasExplainableButton = true;
            // Add test Filter
            WordProcessorFilterBase accessibilityOverview = new WordListFilterExact("prevent turning of", new ArrayList<>(List.of("Use OurPact3")), false, preventDisabelingAccessabilty);
            WordProcessorFilterBase accessibilityDialog = new WordListFilterExact("prevent turning of", new ArrayList<>(List.of("Stop OurPact3?")), false, preventDisabelingAccessabilty);
            WordProcessorFilterBase preventUninstall = new WordListFilterExact("prevent uninstall", new ArrayList<>(List.of("OurPact3","UNINSTALL")), false, preventDisabelingAccessabilty);
            filters.add(accessibilityOverview);
            filters.add(preventUninstall);
            filters.add(accessibilityDialog);
        }
        return new AppFilter(service, topicManager, filters, appName);
    }

    private AppFilter getPocketCastsFilter() throws TopicMissingException
    {

        String appName = "au.com.shiftyjelly.pocketcasts";
        ArrayList<WordProcessorFilterBase> filters = new ArrayList<WordProcessorFilterBase>();
        {
            PipelineResultKeywordFilter resultIgnoreSearch = new PipelineResultKeywordFilter();
            resultIgnoreSearch.windowAction = PipelineWindowAction.STOP_FURTHER_PROCESSING;
            resultIgnoreSearch.hasExplainableButton = true;
            // Add test Filter
            WordProcessorFilterBase ignoreSearch = new WordListFilterExact("null", new ArrayList<>(List.of("Recent searches", "CLEAR ALL")), false, resultIgnoreSearch);
            filters.add(ignoreSearch);
        }
        {
            PipelineResultKeywordFilter pornResult = new PipelineResultKeywordFilter();
            pornResult.windowAction = PipelineWindowAction.PERFORM_BACK_ACTION_AND_WARNING;
            pornResult.hasExplainableButton = true;
            ArrayList<TopicScoring> allScorings = new ArrayList<>();
            allScorings.add(new TopicScoring("porn_explicit", 33, 50));
            allScorings.add(new TopicScoring("female_body_parts", 30, 45));
            allScorings.add(new TopicScoring("adult_nudity", 20, 20));  //NOTE: This is not so dangerous as this is a podcast app
            allScorings.add(new TopicScoring("adult_sex_toys", 32, 45));
//            allScorings.add(new TopicScoring("female_names", 15, 30));
            allScorings.add(new TopicScoring("female_clothing", 15, 30));
            allScorings.add(new TopicScoring("patrick_all_merged", 49, 66));
            boolean ignoreCase = true;  // important for porn filter

//            WordListFilterScored blockAdultStuff = new WordListFilterScored("Patricks block list", new ArrayList<>(List.of(myTerms,scoringFemaleClothing,scoringFemaleNames,scoringPorn,scoringFemaleBodyParts,scoringAdultNudity,scoringSexToys)), false, topicManager, pornResult);
            WordListFilterScored blockAdultStuff = new WordListFilterScored("Patricks block list", allScorings, ignoreCase, topicManager, pornResult);
            filters.add(blockAdultStuff);
        }
        return new AppFilter(service, topicManager, filters, appName);

    }

    private AppFilter getFirefoxFilter() throws TopicMissingException
    {
        String appName = "org.mozilla.firefox";
        ArrayList<WordProcessorFilterBase> filters = new ArrayList<WordProcessorFilterBase>();
        {
            // ignore start page
            PipelineResultKeywordFilter ignoreHistoryPage = new PipelineResultKeywordFilter();
            ignoreHistoryPage.windowAction = PipelineWindowAction.STOP_FURTHER_PROCESSING;
            ignoreHistoryPage.hasExplainableButton = true;
            // Add test Filter
            WordProcessorFilterBase ignoreSearch = new WordListFilterExact("null", new ArrayList<>(List.of("History", "Recently closed tabs")), false, ignoreHistoryPage);
            filters.add(ignoreSearch);
        }

        {
            // note have to run before block search engines as suggestion to bad engies are blocked
            // ignore suggestion screen
            PipelineResultKeywordFilter resultIgnoreSearch = new PipelineResultKeywordFilter();
            resultIgnoreSearch.windowAction = PipelineWindowAction.STOP_FURTHER_PROCESSING;
            resultIgnoreSearch.hasExplainableButton = true;
            // Add test Filter
            WordProcessorFilterBase ignoreSearch = new WordListFilterExact("null", new ArrayList<>(List.of("Firefox Suggest")), false, resultIgnoreSearch);
            filters.add(ignoreSearch);
        }
        {
            // ignore history page
            PipelineResultKeywordFilter ignoreStartpage = new PipelineResultKeywordFilter();
            ignoreStartpage.windowAction = PipelineWindowAction.STOP_FURTHER_PROCESSING;
            ignoreStartpage.hasExplainableButton = true;
            // Add test Filter
            WordProcessorFilterBase ignoreSearch = new WordListFilterExact("null", new ArrayList<>(List.of("Firefox", "Jump back in")), false, ignoreStartpage);
            filters.add(ignoreSearch);
        }
        // Block stuff
        {
            //block unsafe search
            PipelineResultKeywordFilter blockUnsafesearch = new PipelineResultKeywordFilter();
            blockUnsafesearch.windowAction = PipelineWindowAction.PERFORM_BACK_ACTION_AND_WARNING;
            blockUnsafesearch.hasExplainableButton = true;
            ArrayList<TopicScoring> allScorings = new ArrayList<>();
            allScorings.add(new TopicScoring("enforce_safe_search", 100, 0));
            boolean ignoreCase = false;  // important for porn filter

            WordListFilterScored blockAdultStuff = new WordListFilterScored("Enforce safe search", allScorings, ignoreCase, topicManager, blockUnsafesearch);
            filters.add(blockAdultStuff);
        }


        {
            PipelineResultKeywordFilter pornResult = new PipelineResultKeywordFilter();
            pornResult.windowAction = PipelineWindowAction.PERFORM_BACK_ACTION_AND_WARNING;
            pornResult.hasExplainableButton = true;
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
        AppFilter appFilter = new AppFilter(service, topicManager, filters, appName);
        appFilter.addGenericEventFilters(new ExponentialPunishFilter("test",10,3));
        return appFilter;

    }

    private AppFilter getTelegramFilter2()
    {
        String appName = "org.telegram.messenger";
        ArrayList<WordProcessorFilterBase> filters = new ArrayList<WordProcessorFilterBase>();
        {
            PipelineResultKeywordFilter resultIgnoreSearch = new PipelineResultKeywordFilter();
            resultIgnoreSearch.windowAction = PipelineWindowAction.PERFORM_BACK_ACTION;
            resultIgnoreSearch.hasExplainableButton = true;
            // Add test Filter
            WordProcessorFilterBase ignoreSearch = new WordListFilterExact("Block people nearby", new ArrayList<>(List.of("People Nearby", "Make Myself Visible")), false, resultIgnoreSearch);
            filters.add(ignoreSearch);
        }
        return new AppFilter(service, topicManager, filters, appName);
    }

    public ArrayList<AppFilter> getAllExampleFilters() throws TopicMissingException
    {
        ArrayList<AppFilter> list = new ArrayList<>();
        list.add(getFirefoxFilter());
        list.add(getPocketCastsFilter());
        list.add(getTelegramFilter2());
        list.add(getSettings());
        return list;
    }
}
