package com.example.ourpact3;

import com.example.ourpact3.service.AppPermission;
import com.example.ourpact3.smart_filter.ExponentialPunishFilter;
import com.example.ourpact3.smart_filter.SpecialSmartFilterBase;
import com.example.ourpact3.topics.InvalidTopicIDException;
import com.example.ourpact3.model.PipelineResultKeywordFilter;
import com.example.ourpact3.topics.TopicAlreadyExistsException;
import com.example.ourpact3.topics.TopicLoaderCycleDetectedException;
import com.example.ourpact3.topics.TopicManager;
import com.example.ourpact3.topics.TopicMissingException;
import com.example.ourpact3.smart_filter.WordListFilterScored.TopicScoring;
import com.example.ourpact3.model.PipelineResultBase;
import com.example.ourpact3.model.PipelineWindowAction;
import com.example.ourpact3.smart_filter.WordListFilterExact;
import com.example.ourpact3.smart_filter.WordProcessorFilterBase;
import com.example.ourpact3.smart_filter.WordListFilterScored;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class ExampleAppKeywordFilters
{
    ExampleAppKeywordFilters(ContentFilterService service, TopicManager topicManager)
    {
        this.service = service;
        this.topicManager = topicManager;
    }

    private final ContentFilterService service;
    private final TopicManager topicManager;

    public TreeMap<String,AppPermission> getAppPermissions()
    {
        TreeMap<String, AppPermission> appPermissions = new TreeMap<>();
        // Add the apps to the HashSet
        appPermissions.put(this.service.getApplicationContext().getPackageName(),AppPermission.USER_RO);
        appPermissions.put("com.android.settings",AppPermission.USER_RO);
        appPermissions.put("net.tandem",AppPermission.USER_IGNORE_LIST);
        appPermissions.put("org.thoughtcrime.securesms",AppPermission.USER_IGNORE_LIST);
//        ignoredApps.add("org.telegram.messenger");
        appPermissions.put("com.whatsapp",AppPermission.USER_IGNORE_LIST);
        appPermissions.put("cz.mobilesoft.appblock",AppPermission.USER_IGNORE_LIST);
        appPermissions.put("com.standardnotes",AppPermission.USER_IGNORE_LIST);
        appPermissions.put("de.c24.bankapp",AppPermission.USER_IGNORE_LIST);
        appPermissions.put("ws.xsoh.etar",AppPermission.USER_IGNORE_LIST);
        appPermissions.put("com.example.ourpact3",AppPermission.USER_IGNORE_LIST);
        appPermissions.put("com.ichi2.anki",AppPermission.USER_IGNORE_LIST);
        appPermissions.put("com.epson.epsonsmart",AppPermission.USER_IGNORE_LIST);
        appPermissions.put("de.mm20.launcher2",AppPermission.USER_IGNORE_LIST);
        appPermissions.put("de.reimardoeffinger.quickdic",AppPermission.USER_IGNORE_LIST);
        appPermissions.put("de.mm20.launcher2.release",AppPermission.USER_IGNORE_LIST);

        return appPermissions;
    }

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

    private AppFilter getSettings() throws CloneNotSupportedException
    {
        String appName = "com.android.settings";
        ArrayList<WordProcessorFilterBase> filters = new ArrayList<WordProcessorFilterBase>();
        // prevent user for disabling the accessabilty service (only works in english)
        {
            PipelineResultKeywordFilter preventDisabelingAccessabilty = new PipelineResultKeywordFilter("");
            preventDisabelingAccessabilty.setWindowAction(PipelineWindowAction.PERFORM_BACK_ACTION_AND_WARNING);
            preventDisabelingAccessabilty.setKillState(PipelineResultBase.KillState.KILL_BEFORE_WINDOW);
            preventDisabelingAccessabilty.setHasExplainableButton(true);
            // Add test Filter
            WordProcessorFilterBase accessibilityOverview = new WordListFilterExact("prevent turning of", new ArrayList<>(List.of("Use OurPact3")), false, preventDisabelingAccessabilty,false);
            WordProcessorFilterBase accessibilityDialog = new WordListFilterExact("prevent turning of", new ArrayList<>(List.of("Stop OurPact3?")), false, preventDisabelingAccessabilty,false);
            WordProcessorFilterBase preventUninstall = new WordListFilterExact("prevent uninstall", new ArrayList<>(List.of("OurPact3","UNINSTALL")), false, preventDisabelingAccessabilty,false);
            filters.add(accessibilityOverview);
            filters.add(preventUninstall);
            filters.add(accessibilityDialog);
        }

        AppFilter appFilter = new AppFilter(service, topicManager, filters, appName);
        appFilter.setSpecialSmartFilter(SpecialSmartFilterBase.Name.EXP_PUNISH ,new ExponentialPunishFilter("test",1,30,120));
        return appFilter;
    }

    private AppFilter getPocketCastsFilter() throws TopicMissingException, CloneNotSupportedException
    {

        String appName = "au.com.shiftyjelly.pocketcasts";
        ArrayList<WordProcessorFilterBase> filters = new ArrayList<WordProcessorFilterBase>();
        {
            PipelineResultKeywordFilter resultIgnoreSearch = new PipelineResultKeywordFilter("");
            resultIgnoreSearch.setWindowAction(PipelineWindowAction.STOP_FURTHER_PROCESSING);
            resultIgnoreSearch.setHasExplainableButton(true);
            // Add test Filter
            WordProcessorFilterBase ignoreSearch = new WordListFilterExact("null", new ArrayList<>(List.of("Recent searches", "CLEAR ALL")), false, resultIgnoreSearch,false);
            filters.add(ignoreSearch);
        }
        {
            PipelineResultKeywordFilter pornResult = new PipelineResultKeywordFilter("");
            pornResult.setWindowAction(PipelineWindowAction.PERFORM_BACK_ACTION_AND_WARNING);
            pornResult.setHasExplainableButton(true);
            pornResult.setKillState(PipelineResultBase.KillState.KILL_BEFORE_WINDOW);
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

    private AppFilter getFirefoxFilter() throws TopicMissingException, CloneNotSupportedException
    {
        String appName = "org.mozilla.firefox";
        ArrayList<WordProcessorFilterBase> filters = new ArrayList<WordProcessorFilterBase>();
        {
            // ignore start page
            PipelineResultKeywordFilter ignoreHistoryPage = new PipelineResultKeywordFilter("");
            ignoreHistoryPage.setWindowAction(PipelineWindowAction.STOP_FURTHER_PROCESSING);
            ignoreHistoryPage.setHasExplainableButton(true);
            // Add test Filter
            WordProcessorFilterBase ignoreSearch = new WordListFilterExact("null", new ArrayList<>(List.of("History", "Recently closed tabs")), false, ignoreHistoryPage,false);
            filters.add(ignoreSearch);
        }

        {
            // note have to run before block search engines as suggestion to bad engies are blocked
            // ignore suggestion screen
            PipelineResultKeywordFilter resultIgnoreSearch = new PipelineResultKeywordFilter("");
            resultIgnoreSearch.setWindowAction(PipelineWindowAction.STOP_FURTHER_PROCESSING);
            resultIgnoreSearch.setHasExplainableButton(true);
            // Add test Filter
            WordProcessorFilterBase ignoreSearch = new WordListFilterExact("null", new ArrayList<>(List.of("Firefox Suggest")), false, resultIgnoreSearch,false);
            filters.add(ignoreSearch);
        }
        {
            // ignore history page
            PipelineResultKeywordFilter ignoreStartpage = new PipelineResultKeywordFilter("");
            ignoreStartpage.setWindowAction(PipelineWindowAction.STOP_FURTHER_PROCESSING);
            ignoreStartpage.setHasExplainableButton(true);
            // Add test Filter
            WordProcessorFilterBase ignoreSearch = new WordListFilterExact("null", new ArrayList<>(List.of("Firefox", "Jump back in")), false, ignoreStartpage,false);
            ignoreSearch.setCheckOnlyVisibleNodes(false);
            filters.add(ignoreSearch);
        }
        // Block stuff
        {
            //block unsafe search
            PipelineResultKeywordFilter blockUnsafesearch = new PipelineResultKeywordFilter("");
            blockUnsafesearch.setWindowAction(PipelineWindowAction.PERFORM_BACK_ACTION_AND_WARNING);
            blockUnsafesearch.setHasExplainableButton(true);
            ArrayList<TopicScoring> allScorings = new ArrayList<>();
            allScorings.add(new TopicScoring("enforce_safe_search", 100, 0));
            boolean ignoreCase = false;  // important for porn filter

            WordListFilterScored blockAdultStuff = new WordListFilterScored("Enforce safe search", allScorings, ignoreCase, topicManager, blockUnsafesearch);
            filters.add(blockAdultStuff);
        }


        {
            PipelineResultKeywordFilter pornResult = new PipelineResultKeywordFilter("");
            pornResult.setWindowAction(PipelineWindowAction.PERFORM_BACK_ACTION_AND_WARNING);
            pornResult.setHasExplainableButton(true);
            pornResult.setKillState(PipelineResultBase.KillState.KILL_BEFORE_WINDOW);
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
        appFilter.setSpecialSmartFilter(SpecialSmartFilterBase.Name.EXP_PUNISH,new ExponentialPunishFilter("test",10,30,5));
        return appFilter;

    }

    private AppFilter getTelegramFilter2() throws CloneNotSupportedException
    {
        String appName = "org.telegram.messenger";
        ArrayList<WordProcessorFilterBase> filters = new ArrayList<WordProcessorFilterBase>();
        {
            PipelineResultKeywordFilter resultIgnoreSearch = new PipelineResultKeywordFilter("");
            resultIgnoreSearch.setWindowAction(PipelineWindowAction.PERFORM_BACK_ACTION);
            resultIgnoreSearch.setHasExplainableButton(true);
            // Add test Filter
            WordProcessorFilterBase ignoreSearch = new WordListFilterExact("Block people nearby", new ArrayList<>(List.of("People Nearby", "Make Myself Visible")), false, resultIgnoreSearch,false);
            filters.add(ignoreSearch);
        }
        return new AppFilter(service, topicManager, filters, appName);
    }

    private AppFilter getYoutubeFilter() throws TopicMissingException, CloneNotSupportedException
    {
        String appName = "org.schabi.newpipe";
        ArrayList<WordProcessorFilterBase> filters = new ArrayList<WordProcessorFilterBase>();


        {
            // ignore history page
            PipelineResultKeywordFilter ignoreSettings = new PipelineResultKeywordFilter("");
            ignoreSettings.setWindowAction(PipelineWindowAction.STOP_FURTHER_PROCESSING);
            ignoreSettings.setHasExplainableButton(true);
            // Add test Filter
            WordProcessorFilterBase ignoreSearch = new WordListFilterExact("null", new ArrayList<>(List.of("Settings", "Content")), false, ignoreSettings,false);
            filters.add(ignoreSearch);
        }
        {
        // ignore history page
        PipelineResultKeywordFilter ignoreSettings = new PipelineResultKeywordFilter("");
        ignoreSettings.setWindowAction(PipelineWindowAction.STOP_FURTHER_PROCESSING);
        ignoreSettings.setHasExplainableButton(true);
        // Add test Filter
        WordProcessorFilterBase ignoreSearch = new WordListFilterExact("null", new ArrayList<>(List.of("Search")), false, ignoreSettings,true);
        filters.add(ignoreSearch);
    }


        {
            PipelineResultKeywordFilter pornResult = new PipelineResultKeywordFilter("");
            pornResult.setWindowAction(PipelineWindowAction.PERFORM_BACK_ACTION_AND_WARNING);
            pornResult.setHasExplainableButton(true);
            pornResult.setKillState(PipelineResultBase.KillState.KILL_BEFORE_WINDOW);
            ArrayList<TopicScoring> allScorings = new ArrayList<>();
            allScorings.add(new TopicScoring("porn_explicit", 33, 50));
            allScorings.add(new TopicScoring("female_body_parts", 30, 45));
            allScorings.add(new TopicScoring("adult_nudity", 32, 45));
            allScorings.add(new TopicScoring("adult_sex_toys", 32, 45));
            allScorings.add(new TopicScoring("female_names", 15, 30));
            allScorings.add(new TopicScoring("female_clothing", 15, 30));
            allScorings.add(new TopicScoring("patrick_all_merged", 49, 66));
            boolean ignoreCase = true;  // important for porn filter

            WordListFilterScored blockAdultStuff = new WordListFilterScored("Patricks block list", allScorings, ignoreCase, topicManager, pornResult);
            filters.add(blockAdultStuff);
        }
        AppFilter appFilter = new AppFilter(service, topicManager, filters, appName);
        appFilter.setSpecialSmartFilter(SpecialSmartFilterBase.Name.EXP_PUNISH,new ExponentialPunishFilter("test",2,10,15));
        return appFilter;


    }

    public ArrayList<AppFilter> getAllExampleFilters() throws TopicMissingException, CloneNotSupportedException
    {
        ArrayList<AppFilter> list = new ArrayList<>();
        list.add(getFirefoxFilter());
        list.add(getPocketCastsFilter());
        list.add(getTelegramFilter2());
        list.add(getYoutubeFilter());
        list.add(getSettings());

        return list;
    }
}
