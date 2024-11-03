package com.example.ourpact3;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.example.ourpact3.model.PipelineButtonAction;
import com.example.ourpact3.service.AppPermission;
import com.example.ourpact3.smart_filter.ExponentialPunishFilter;
import com.example.ourpact3.smart_filter.SpecialSmartFilterBase;
import com.example.ourpact3.smart_filter.WordSmartFilterIdentifier;
import com.example.ourpact3.topics.InvalidTopicIDException;
import com.example.ourpact3.pipeline.PipelineResultKeywordFilter;
import com.example.ourpact3.topics.TopicAlreadyExistsException;
import com.example.ourpact3.topics.TopicLoaderCycleDetectedException;
import com.example.ourpact3.topics.TopicManager;
import com.example.ourpact3.topics.TopicMissingException;
import com.example.ourpact3.smart_filter.WordListFilterScored.TopicScoring;
import com.example.ourpact3.pipeline.PipelineResultBase;
import com.example.ourpact3.model.PipelineWindowAction;
import com.example.ourpact3.smart_filter.WordListFilterExact;
import com.example.ourpact3.smart_filter.WordProcessorSmartFilterBase;
import com.example.ourpact3.smart_filter.WordListFilterScored;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class ExampleAppKeywordFilters
{
    ExampleAppKeywordFilters(ContentFilterService service, TopicManager topicManager, Context ctx)
    {
        this.service = service;
        this.topicManager = topicManager;

        this.isDebugVersion = (0 != (ctx.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE));


    }

    private final boolean isDebugVersion;
    private final ContentFilterService service;
    private final TopicManager topicManager;

    public TreeMap<String, AppPermission> getAppPermissions()
    {
        TreeMap<String, AppPermission> appPermissions = new TreeMap<>();
        // Add the apps to the HashSet
        appPermissions.put(this.service.getApplicationContext().getPackageName(), AppPermission.USER_RO);
        appPermissions.put("com.android.settings", AppPermission.USER_RO);
        appPermissions.put("com.google.android.inputmethod.latin", AppPermission.USER_IGNORE_LIST);
        appPermissions.put("com.google.android.apps.maps", AppPermission.USER_IGNORE_LIST);
        appPermissions.put("net.osmand.plus", AppPermission.USER_IGNORE_LIST);
        appPermissions.put("com.simplemobiletools.gallery.pro", AppPermission.USER_IGNORE_LIST);
        appPermissions.put("com.airbnb.android", AppPermission.USER_IGNORE_LIST);
        appPermissions.put("com.google.android.contacts", AppPermission.USER_IGNORE_LIST);
        appPermissions.put("com.google.android.deskclock", AppPermission.USER_IGNORE_LIST);
        appPermissions.put("de.nebenan.app", AppPermission.USER_IGNORE_LIST);
        appPermissions.put("de.flixbus.app", AppPermission.USER_IGNORE_LIST);
        appPermissions.put("org.fdroid.fddoid", AppPermission.USER_IGNORE_LIST);
        appPermissions.put("org.nuclearfog.apollo", AppPermission.USER_IGNORE_LIST);
        appPermissions.put("net.tandem", AppPermission.USER_IGNORE_LIST);
        appPermissions.put("com.meetup", AppPermission.USER_IGNORE_LIST);
        appPermissions.put("ch.protonvpn.android", AppPermission.USER_IGNORE_LIST);
        appPermissions.put("com.beemdevelopment.aegis", AppPermission.USER_IGNORE_LIST);
        appPermissions.put("io.github.muntashirakon.AppManager", AppPermission.USER_IGNORE_LIST);
        appPermissions.put("com.governikus.ausweisapp2", AppPermission.USER_IGNORE_LIST);
        appPermissions.put("com.mediatek.camera", AppPermission.USER_IGNORE_LIST);
        appPermissions.put("org.thoughtcrime.securesms", AppPermission.USER_IGNORE_LIST);
        appPermissions.put("ru.vsms", AppPermission.USER_IGNORE_LIST);  //voice dictation editor
//        ignoredApps.add("org.telegram.messenger");
        appPermissions.put("com.whatsapp", AppPermission.USER_IGNORE_LIST);
        appPermissions.put("capital.scalable.droid", AppPermission.USER_IGNORE_LIST);
        appPermissions.put("splid.teamturtle.com.splid", AppPermission.USER_IGNORE_LIST);
        appPermissions.put("com.fsck.k9", AppPermission.USER_IGNORE_LIST);
        appPermissions.put("com.trello", AppPermission.USER_IGNORE_LIST);
        appPermissions.put("com.maxistar.textpad", AppPermission.USER_IGNORE_LIST);
        appPermissions.put("cz.mobilesoft.appblock", AppPermission.USER_IGNORE_LIST);
        appPermissions.put("com.standardnotes", AppPermission.USER_IGNORE_LIST);
        appPermissions.put("com.google.android.calculator", AppPermission.USER_IGNORE_LIST);
        appPermissions.put("com.getyourguide.android", AppPermission.USER_IGNORE_LIST);
        appPermissions.put("de.c24.bankapp", AppPermission.USER_IGNORE_LIST);
        appPermissions.put("de.hafas.android.db", AppPermission.USER_IGNORE_LIST); //db navigator
        appPermissions.put("ws.xsoh.etar", AppPermission.USER_IGNORE_LIST);
        appPermissions.put("com.example.ourpact3", AppPermission.USER_IGNORE_LIST);
        appPermissions.put("com.ichi2.anki", AppPermission.USER_IGNORE_LIST);
        appPermissions.put("net.sourceforge.opencamera", AppPermission.USER_IGNORE_LIST);
        appPermissions.put("com.epson.epsonsmart", AppPermission.USER_IGNORE_LIST);
        appPermissions.put("de.mm20.launcher2", AppPermission.USER_IGNORE_LIST);
        appPermissions.put("de.reimardoeffinger.quickdic", AppPermission.USER_IGNORE_LIST);
        appPermissions.put("de.mm20.launcher2.release", AppPermission.USER_IGNORE_LIST);

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

    private AppFilter getAndroidSettings() throws CloneNotSupportedException
    {
        String myAppName = "SafeZone";
        String appName = "com.android.settings";
        ArrayList<WordProcessorSmartFilterBase> filters = new ArrayList<WordProcessorSmartFilterBase>();
        // prevent user for disabling the accessabilty service (only works in english)
        {
            PipelineResultKeywordFilter preventDisabelingAccessabilty = new PipelineResultKeywordFilter("");
            preventDisabelingAccessabilty.setWindowAction(PipelineWindowAction.STOP_FURTHER_PROCESSING);
            preventDisabelingAccessabilty.setButtonAction(PipelineButtonAction.BACK_BUTTON);

            // Killing makes it to slow
            preventDisabelingAccessabilty.setKillState(PipelineResultBase.KillState.DO_NOT_KILL);
            preventDisabelingAccessabilty.setHasExplainableButton(true);
            WordProcessorSmartFilterBase accessibilityOverview = new WordListFilterExact(WordSmartFilterIdentifier.USER_1, new ArrayList<>(List.of(
                    new ArrayList<>(List.of("Use " + myAppName)),
                    new ArrayList<>(List.of("Stop " + myAppName + "?")),
                    new ArrayList<>(List.of("Accessibility")) // Needed?

//                    ,new ArrayList<>(List.of(new String[]{"Accessibility"}))
            )), false, preventDisabelingAccessabilty, false);
            filters.add(accessibilityOverview);
        }
        // device admin stuff doesn't show up in access service we have to block the way
        {
            PipelineResultKeywordFilter preventTurnOfDeviceAdmin = new PipelineResultKeywordFilter("");
            preventTurnOfDeviceAdmin.setWindowAction(PipelineWindowAction.STOP_FURTHER_PROCESSING);
            preventTurnOfDeviceAdmin.setButtonAction(PipelineButtonAction.BACK_BUTTON);
            // Killing makes it to slow
            preventTurnOfDeviceAdmin.setKillState(PipelineResultBase.KillState.DO_NOT_KILL);
            preventTurnOfDeviceAdmin.setHasExplainableButton(true);
            WordProcessorSmartFilterBase searchForDeviceAdmin = new WordListFilterExact(WordSmartFilterIdentifier.USER_2, new ArrayList<>(List.of(
                    new ArrayList<>(List.of("Device admin apps")),
                    new ArrayList<>(List.of(new String[]{"OPEN", myAppName}))
            )), false, preventTurnOfDeviceAdmin, false);
            filters.add(searchForDeviceAdmin);

        }

        return new AppFilter(service, topicManager, filters, appName, true);
    }

    private AppFilter getPocketCastsFilter() throws TopicMissingException, CloneNotSupportedException
    {

        String appName = "au.com.shiftyjelly.pocketcasts";
        ArrayList<WordProcessorSmartFilterBase> filters = new ArrayList<WordProcessorSmartFilterBase>();
        {
            PipelineResultKeywordFilter resultIgnoreSearch = new PipelineResultKeywordFilter("");
            resultIgnoreSearch.setWindowAction(PipelineWindowAction.STOP_FURTHER_PROCESSING);
            resultIgnoreSearch.setHasExplainableButton(true);
            // Add test Filter
            // Create the inner ArrayList
            ArrayList<String> innerList = new ArrayList<>(List.of("Recent searches", "CLEAR ALL"));

// Create the outer ArrayList and add the inner list to it
            ArrayList<ArrayList<String>> outerList = new ArrayList<>();
            outerList.add(innerList);
            WordProcessorSmartFilterBase ignoreSearch = new WordListFilterExact(WordSmartFilterIdentifier.USER_4, outerList, false, resultIgnoreSearch, false);
            filters.add(ignoreSearch);
        }
        {
            PipelineResultKeywordFilter pornResult = new PipelineResultKeywordFilter("");
            pornResult.setWindowAction(PipelineWindowAction.WARNING);
            pornResult.setButtonAction(PipelineButtonAction.BACK_BUTTON);
            pornResult.setHasExplainableButton(true);
            pornResult.setKillState(PipelineResultBase.KillState.KILL_BEFORE_WINDOW);
            ArrayList<TopicScoring> allScorings = new ArrayList<>();
            allScorings.add(new TopicScoring("porn_explicit", 33, 50));
            allScorings.add(new TopicScoring("female_body_parts", 30, 45));
            allScorings.add(new TopicScoring("adult_nudity", 20, 20));  //NOTE: This is not so dangerous as this is a podcast app
            allScorings.add(new TopicScoring("adult_sex_toys", 32, 45));
//            allScorings.add(new TopicScoring("female_names", 15, 30));
//            allScorings.add(new TopicScoring("female_clothing", 15, 30));
            allScorings.add(new TopicScoring("patrick_all_merged", 49, 66));
            boolean ignoreCase = true;  // important for porn filter

//            WordListFilterScored blockAdultStuff = new WordListFilterScored("Patricks block list", new ArrayList<>(List.of(myTerms,scoringFemaleClothing,scoringFemaleNames,scoringPorn,scoringFemaleBodyParts,scoringAdultNudity,scoringSexToys)), false, topicManager, pornResult);
            WordListFilterScored blockAdultStuff = new WordListFilterScored(WordSmartFilterIdentifier.PORN, allScorings, ignoreCase, topicManager, pornResult);
            filters.add(blockAdultStuff);
        }
        return new AppFilter(service, topicManager, filters, appName, false);

    }


    private AppFilter getFirefoxFilter() throws TopicMissingException, CloneNotSupportedException
    {
        String appName = "org.mozilla.firefox";
        ArrayList<WordProcessorSmartFilterBase> filters = new ArrayList<WordProcessorSmartFilterBase>();
        {
            // ignore start page
            PipelineResultKeywordFilter ignoreHistoryPage = new PipelineResultKeywordFilter("");
            ignoreHistoryPage.setWindowAction(PipelineWindowAction.STOP_FURTHER_PROCESSING);
            ignoreHistoryPage.setHasExplainableButton(true);
            // Add test Filter
            WordProcessorSmartFilterBase ignoreSearch = new WordListFilterExact(WordSmartFilterIdentifier.USER_1, new ArrayList<>(List.of(new ArrayList<>(List.of("History", "Recently closed tabs")))), false, ignoreHistoryPage, false);
            filters.add(ignoreSearch);
        }

        {
            // note have to run before block search engines as suggestion to bad engies are blocked
            // ignore suggestion screen
            PipelineResultKeywordFilter resultIgnoreSearch = new PipelineResultKeywordFilter("");
            resultIgnoreSearch.setWindowAction(PipelineWindowAction.STOP_FURTHER_PROCESSING);
            resultIgnoreSearch.setHasExplainableButton(true);
            // Add test Filter
            WordProcessorSmartFilterBase ignoreSearch = new WordListFilterExact(WordSmartFilterIdentifier.USER_2, new ArrayList<>(List.of(new ArrayList<>(List.of("Firefox Suggest")))), false, resultIgnoreSearch, false);
            filters.add(ignoreSearch);
        }
        {
            // ignore history page
            PipelineResultKeywordFilter ignoreStartpage = new PipelineResultKeywordFilter("");
            ignoreStartpage.setWindowAction(PipelineWindowAction.STOP_FURTHER_PROCESSING);
            ignoreStartpage.setHasExplainableButton(true);
            // Add test Filter
            WordProcessorSmartFilterBase ignoreSearch = new WordListFilterExact(WordSmartFilterIdentifier.USER_3, new ArrayList<>(List.of(new ArrayList<>(List.of("Firefox", "Jump back in")))), false, ignoreStartpage, false);
            ignoreSearch.setCheckOnlyVisibleNodes(false);
            filters.add(ignoreSearch);
        }
        // Block stuff
        {
            //block unsafe search
            PipelineResultKeywordFilter blockUnsafesearch = new PipelineResultKeywordFilter("");
            blockUnsafesearch.setWindowAction(PipelineWindowAction.WARNING);
            blockUnsafesearch.setButtonAction(PipelineButtonAction.BACK_BUTTON);
            blockUnsafesearch.setHasExplainableButton(true);
            ArrayList<TopicScoring> allScorings = new ArrayList<>();
            allScorings.add(new TopicScoring("enforce_safe_search", 100, 0));
            boolean ignoreCase = false;  // important for porn filter

            WordListFilterScored blockAdultStuff = new WordListFilterScored(WordSmartFilterIdentifier.ENFORCE_SAFE_SEARCH, allScorings, ignoreCase, topicManager, blockUnsafesearch);
            filters.add(blockAdultStuff);
        }


        {
            PipelineResultKeywordFilter pornResult = new PipelineResultKeywordFilter("");
            pornResult.setWindowAction(PipelineWindowAction.WARNING);
            pornResult.setButtonAction(PipelineButtonAction.BACK_BUTTON);
            pornResult.setHasExplainableButton(true);
            pornResult.setKillState(PipelineResultBase.KillState.KILL_BEFORE_WINDOW);
            ArrayList<TopicScoring> allScorings = new ArrayList<>();
            allScorings.add(new TopicScoring("porn_explicit", 33, 0));
            allScorings.add(new TopicScoring("female_body_parts", 30, 0));
            allScorings.add(new TopicScoring("adult_nudity", 32, 0));
            allScorings.add(new TopicScoring("adult_sex_toys", 32, 0));
//            allScorings.add(new TopicScoring("female_names", 15, 0));
//            allScorings.add(new TopicScoring("female_clothing", 15, 0));
            allScorings.add(new TopicScoring("patrick_all_merged", 49, 0));
            boolean ignoreCase = true;  // important for porn filter

//            WordListFilterScored blockAdultStuff = new WordListFilterScored("Patricks block list", new ArrayList<>(List.of(myTerms,scoringFemaleClothing,scoringFemaleNames,scoringPorn,scoringFemaleBodyParts,scoringAdultNudity,scoringSexToys)), false, topicManager, pornResult);
            WordListFilterScored blockAdultStuff = new WordListFilterScored(WordSmartFilterIdentifier.USER_1, allScorings, ignoreCase, topicManager, pornResult);
            blockAdultStuff.setName("Block NSFW");
            filters.add(blockAdultStuff);
        }
        AppFilter appFilter = new AppFilter(service, topicManager, filters, appName, true);
        if(!isDebugVersion)
        {
            appFilter.setSpecialSmartFilter(SpecialSmartFilterBase.Name.EXP_PUNISH, new ExponentialPunishFilter("test", 2, 5, 5, PipelineWindowAction.WARNING));
        }
        return appFilter;

    }

    private AppFilter getDefaultFilter() throws TopicMissingException, CloneNotSupportedException
    {
        String appName = "";
        ArrayList<WordProcessorSmartFilterBase> filters = new ArrayList<WordProcessorSmartFilterBase>();


        // Block stuff
        {
            //block unsafe search
            PipelineResultKeywordFilter blockUnsafesearch = new PipelineResultKeywordFilter("");
            blockUnsafesearch.setWindowAction(PipelineWindowAction.WARNING);
            blockUnsafesearch.setButtonAction(PipelineButtonAction.BACK_BUTTON);
            blockUnsafesearch.setHasExplainableButton(true);
            ArrayList<TopicScoring> allScorings = new ArrayList<>();
            allScorings.add(new TopicScoring("enforce_safe_search", 100, 100));
            boolean ignoreCase = false;  // important for porn filter

            WordListFilterScored blockAdultStuff = new WordListFilterScored(WordSmartFilterIdentifier.ENFORCE_SAFE_SEARCH, allScorings, ignoreCase, topicManager, blockUnsafesearch);
            filters.add(blockAdultStuff);
        }


        {
            PipelineResultKeywordFilter pornResult = new PipelineResultKeywordFilter("");
            pornResult.setWindowAction(PipelineWindowAction.WARNING);
            pornResult.setButtonAction(PipelineButtonAction.BACK_BUTTON);
            pornResult.setHasExplainableButton(true);
            pornResult.setKillState(PipelineResultBase.KillState.KILL_BEFORE_WINDOW);
            ArrayList<TopicScoring> allScorings = new ArrayList<>();
            allScorings.add(new TopicScoring("porn_explicit", 40, 0));
            allScorings.add(new TopicScoring("female_body_parts", 30, 0));
            allScorings.add(new TopicScoring("adult_nudity", 32, 0));
            allScorings.add(new TopicScoring("adult_sex_toys", 32, 0));
//            allScorings.add(new TopicScoring("female_names", 15, 30));
//            allScorings.add(new TopicScoring("female_clothing", 15, 0));
            allScorings.add(new TopicScoring("patrick_all_merged", 49, 0));
            boolean ignoreCase = true;  // important for porn filter

//            WordListFilterScored blockAdultStuff = new WordListFilterScored("Patricks block list", new ArrayList<>(List.of(myTerms,scoringFemaleClothing,scoringFemaleNames,scoringPorn,scoringFemaleBodyParts,scoringAdultNudity,scoringSexToys)), false, topicManager, pornResult);
            WordListFilterScored blockAdultStuff = new WordListFilterScored(WordSmartFilterIdentifier.USER_4, allScorings, ignoreCase, topicManager, pornResult);
            blockAdultStuff.setName("Block NSFW");
            filters.add(blockAdultStuff);
        }
        //        appFilter.setSpecialSmartFilter(SpecialSmartFilterBase.Name.EXP_PUNISH, new ExponentialPunishFilter("test", 2, 5, 5));
        return new AppFilter(service, topicManager, filters, appName, true);

    }

    private AppFilter getTelegramFilter2() throws CloneNotSupportedException
    {
        String appName = "org.telegram.messenger";
        ArrayList<WordProcessorSmartFilterBase> filters = new ArrayList<WordProcessorSmartFilterBase>();
        {
            PipelineResultKeywordFilter resultIgnoreSearch = new PipelineResultKeywordFilter("");
            resultIgnoreSearch.setWindowAction(PipelineWindowAction.STOP_FURTHER_PROCESSING);
            resultIgnoreSearch.setButtonAction(PipelineButtonAction.BACK_BUTTON);
            resultIgnoreSearch.setKillState(PipelineResultBase.KillState.KILL_BEFORE_WINDOW);
            resultIgnoreSearch.setHasExplainableButton(true);
            // Add test Filter
            WordProcessorSmartFilterBase ignoreSearch = new WordListFilterExact(WordSmartFilterIdentifier.USER_1,
                    new ArrayList<>(List.of(
                            new ArrayList<>(List.of("People Nearby", "Make Myself Visible")))), false, resultIgnoreSearch, false);
            filters.add(ignoreSearch);
        }
        return new AppFilter(service, topicManager, filters, appName, false);
    }

    private AppFilter getYoutubeFilter() throws TopicMissingException, CloneNotSupportedException
    {
        String appName = "org.schabi.newpipe";
        ArrayList<WordProcessorSmartFilterBase> filters = new ArrayList<WordProcessorSmartFilterBase>();


        {
            // ignore history page
            PipelineResultKeywordFilter ignoreSettings = new PipelineResultKeywordFilter("");
            ignoreSettings.setWindowAction(PipelineWindowAction.STOP_FURTHER_PROCESSING);
            ignoreSettings.setHasExplainableButton(true);
            // Add test Filter
            WordProcessorSmartFilterBase ignoreSearch = new WordListFilterExact(WordSmartFilterIdentifier.USER_1,
                    new ArrayList<>(List.of(new ArrayList<>(List.of("Settings", "Content")))), false, ignoreSettings, false);
            filters.add(ignoreSearch);
        }
        {
            // ignore history page
            PipelineResultKeywordFilter ignoreSettings = new PipelineResultKeywordFilter("");
            ignoreSettings.setWindowAction(PipelineWindowAction.STOP_FURTHER_PROCESSING);
            ignoreSettings.setHasExplainableButton(true);
            // Add test Filter
            WordProcessorSmartFilterBase ignoreSearch = new WordListFilterExact(WordSmartFilterIdentifier.USER_2, new ArrayList<>(List.of(new ArrayList<>(List.of("Search")))), false, ignoreSettings, true);
            filters.add(ignoreSearch);
        }


        {
            PipelineResultKeywordFilter pornResult = new PipelineResultKeywordFilter("");
            pornResult.setWindowAction(PipelineWindowAction.WARNING);
            pornResult.setButtonAction(PipelineButtonAction.BACK_BUTTON);
            pornResult.setHasExplainableButton(true);
            pornResult.setKillState(PipelineResultBase.KillState.KILL_BEFORE_WINDOW);
            ArrayList<TopicScoring> allScorings = new ArrayList<>();
            allScorings.add(new TopicScoring("porn_explicit", 33, 0));
            allScorings.add(new TopicScoring("female_body_parts", 30, 0));
            allScorings.add(new TopicScoring("adult_nudity", 32, 0));
            allScorings.add(new TopicScoring("adult_sex_toys", 32, 0));
//            allScorings.add(new TopicScoring("female_names", 15, 30));
//            allScorings.add(new TopicScoring("female_clothing", 15, 0));
            allScorings.add(new TopicScoring("patrick_all_merged", 49, 0));
            boolean ignoreCase = true;  // important for porn filter

            WordListFilterScored blockAdultStuff = new WordListFilterScored(WordSmartFilterIdentifier.USER_3, allScorings, ignoreCase, topicManager, pornResult);
            filters.add(blockAdultStuff);
        }
        return new AppFilter(service, topicManager, filters, appName, false);


    }

    public ArrayList<AppFilter> getAllExampleFilters() throws TopicMissingException, CloneNotSupportedException
    {
        ArrayList<AppFilter> list = new ArrayList<>();
        list.add(getFirefoxFilter());
        list.add(getPocketCastsFilter());
        list.add(getTelegramFilter2());
        list.add(getYoutubeFilter());
        list.add(getAndroidSettings());
        list.add(getDefaultFilter());

        return list;
    }
}
