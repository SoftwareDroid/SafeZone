package com.example.ourpact3.service;

import android.content.Context;
import android.content.pm.ApplicationInfo;

import com.example.ourpact3.ContentFilterService;
import com.example.ourpact3.db.DatabaseManager;
import com.example.ourpact3.model.PipelineButtonAction;
import com.example.ourpact3.pipeline.CounterAction;
import com.example.ourpact3.smart_filter.AppFilter;
import com.example.ourpact3.smart_filter.ExponentialPunishFilter;
import com.example.ourpact3.smart_filter.ProductivityTimeRule;
import com.example.ourpact3.smart_filter.SpecialSmartFilterBase;
import com.example.ourpact3.smart_filter.ProductivityFilter;
import com.example.ourpact3.smart_filter.WordSmartFilterIdentifier;
import com.example.ourpact3.topics.InvalidTopicIDException;
import com.example.ourpact3.pipeline.PipelineResultKeywordFilter;
import com.example.ourpact3.topics.TopicAlreadyExistsException;
import com.example.ourpact3.topics.TopicLoaderCycleDetectedException;
import com.example.ourpact3.topics.TopicManager;
import com.example.ourpact3.topics.TopicMissingException;
import com.example.ourpact3.smart_filter.WordListFilterScored.TopicScoring;
import com.example.ourpact3.model.PipelineWindowAction;
import com.example.ourpact3.smart_filter.WordListFilterExact;
import com.example.ourpact3.smart_filter.WordProcessorSmartFilterBase;
import com.example.ourpact3.smart_filter.WordListFilterScored;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.TreeMap;

public class ExampleAppKeywordFilters
{
    public ExampleAppKeywordFilters(ContentFilterService service, TopicManager topicManager, Context ctx)
    {
        this.service = service;
        this.topicManager = topicManager;

        this.isDebugVersion = (0 != (ctx.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE));


    }

    private final boolean isDebugVersion;
    private final ContentFilterService service;
    private final TopicManager topicManager;

    public void createInitialLanguages(Context context)
    {
        DatabaseManager dbManager = new DatabaseManager(context);
        dbManager.open();
        if (dbManager.needInitialFilling())
        {
            final long en_id = dbManager.addLanguage(new DatabaseManager.Language(1, "english", "en"));
            final long de_id = dbManager.addLanguage(new DatabaseManager.Language(2, "german", "de"));
            // create some list
            // Id 0 means creates entry
            final long pornMergedListId = dbManager.addOrUpdateWordList(new DatabaseManager.WordList(0, "porn_block", en_id, true, true, "blocks porn content"));
            final long pornExplicitId = dbManager.addOrUpdateWordList(new DatabaseManager.WordList(0, "porn_explicit", en_id, true, true, "sub porn list"));
            dbManager.addOrUpdateWordList(new DatabaseManager.WordList(0, "porn_block", de_id, true, true, "blocks porn content"));
            dbManager.addOrUpdateWordList(new DatabaseManager.WordList(0, "enforce_safe_search", en_id, true, true, "blocks porn content"));
            // define relations
            dbManager.defineAsSublist(pornMergedListId, pornExplicitId);
            // define some words
            dbManager.addWordToList(pornExplicitId, "porn", false, "");
            dbManager.addWordToList(pornExplicitId, "cumshot", false, "");
            dbManager.addWordToList(pornExplicitId, "escort", false, "");

        }
    }

    public void setInitialAppRules(DatabaseManager dbManager)
    {
        List<DatabaseManager.AppRuleTuple> initialRules = new ArrayList<>();
        initialRules.add(new DatabaseManager.AppRuleTuple("com.android.settings", "Android System Settings", true,false,true));
        initialRules.add(new DatabaseManager.AppRuleTuple("org.telegram.messenger", "", true,true,true));
        initialRules.add(new DatabaseManager.AppRuleTuple("org.schabi.newpipe", "", true,true,true));
        // fill db
        dbManager.insertBatchAppRules(initialRules);
    }



    public TreeMap<String, AppPermission> getAppPermissionsFromDB(Context context)
    {
        TreeMap<String, AppPermission> appPermissions = new TreeMap<>();
        DatabaseManager dbManager = new DatabaseManager(context);
        dbManager.open();
        if (dbManager.needInitialFilling())
        {
            // Fill initial app rules
            setInitialAppRules(dbManager);
            //
            List<DatabaseManager.ExceptionTuple> initialExceptions = new ArrayList<>();
            initialExceptions.add(new DatabaseManager.ExceptionTuple(this.service.getApplicationContext().getPackageName(), true, false));
            initialExceptions.add(new DatabaseManager.ExceptionTuple("com.android.settings", true, false));
            initialExceptions.add(new DatabaseManager.ExceptionTuple("com.android.systemui", true, false));
            initialExceptions.add(new DatabaseManager.ExceptionTuple("com.google.android.inputmethod.latin", true, false));
            initialExceptions.add(new DatabaseManager.ExceptionTuple("com.google.android.apps.maps", true, true));
            initialExceptions.add(new DatabaseManager.ExceptionTuple("net.osmand.plus", true, true));
            initialExceptions.add(new DatabaseManager.ExceptionTuple("com.simplemobiletools.gallery.pro", true, true));
            initialExceptions.add(new DatabaseManager.ExceptionTuple("com.airbnb.android", true, true));
            initialExceptions.add(new DatabaseManager.ExceptionTuple("com.google.android.contacts", true, false));
            initialExceptions.add(new DatabaseManager.ExceptionTuple("com.google.android.deskclock", true, false));
            initialExceptions.add(new DatabaseManager.ExceptionTuple("de.nebenan.app", true, true));
            initialExceptions.add(new DatabaseManager.ExceptionTuple("de.flixbus.app", true, true));
            initialExceptions.add(new DatabaseManager.ExceptionTuple("org.fdroid.fdroid", true, true));
            initialExceptions.add(new DatabaseManager.ExceptionTuple("net.tandem", true, true));
            initialExceptions.add(new DatabaseManager.ExceptionTuple("com.meetup", true, true));
            initialExceptions.add(new DatabaseManager.ExceptionTuple("ch.protonvpn.android", true, true));
            initialExceptions.add(new DatabaseManager.ExceptionTuple("com.beemdevelopment.aegis", true, true));
            initialExceptions.add(new DatabaseManager.ExceptionTuple("io.github.muntashirakon.AppManager", true, true));
            initialExceptions.add(new DatabaseManager.ExceptionTuple("com.governikus.ausweisapp2", true, true));
            initialExceptions.add(new DatabaseManager.ExceptionTuple("com.mediatek.camera", true, true));
            initialExceptions.add(new DatabaseManager.ExceptionTuple("org.thoughtcrime.securesms", true, true));
            initialExceptions.add(new DatabaseManager.ExceptionTuple("ru.vsms", true, true));
            initialExceptions.add(new DatabaseManager.ExceptionTuple("com.whatsapp", true, true));
            initialExceptions.add(new DatabaseManager.ExceptionTuple("capital.scalable.droid", true, true));
            initialExceptions.add(new DatabaseManager.ExceptionTuple("splid.teamturtle.com.splid", true, true));
            initialExceptions.add(new DatabaseManager.ExceptionTuple("com.fsck.k9", true, true));
            initialExceptions.add(new DatabaseManager.ExceptionTuple("com.trello", true, true));
            initialExceptions.add(new DatabaseManager.ExceptionTuple("com.maxistar.textpad", true, true));
            initialExceptions.add(new DatabaseManager.ExceptionTuple("cz.mobilesoft.appblock", true, true));
            initialExceptions.add(new DatabaseManager.ExceptionTuple("com.standardnotes", true, true));
            initialExceptions.add(new DatabaseManager.ExceptionTuple("com.google.android.calculator", true, true));
            initialExceptions.add(new DatabaseManager.ExceptionTuple("com.getyourguide.android", true, true));
            initialExceptions.add(new DatabaseManager.ExceptionTuple("de.c24.bankapp", true, true));
            initialExceptions.add(new DatabaseManager.ExceptionTuple("de.hafas.android.db", true, true));
            initialExceptions.add(new DatabaseManager.ExceptionTuple("ws.xsoh.etar", true, true));
            initialExceptions.add(new DatabaseManager.ExceptionTuple("com.ichi2.anki", true, true));
            initialExceptions.add(new DatabaseManager.ExceptionTuple("net.sourceforge.opencamera", true, true));
            initialExceptions.add(new DatabaseManager.ExceptionTuple("com.epson.epsonsmart", true, true));
            initialExceptions.add(new DatabaseManager.ExceptionTuple("de.mm20.launcher2", true, true));
            initialExceptions.add(new DatabaseManager.ExceptionTuple("de.reimardoeffinger.quickdic", true, true));
            initialExceptions.add(new DatabaseManager.ExceptionTuple("de.mm20.launcher2.release", true, true));

            dbManager.insertBatchExceptions(initialExceptions);

        }
        for (DatabaseManager.ExceptionTuple exception : dbManager.getAllExceptions())
        {

            appPermissions.put(exception.packageID, exception.writable ? AppPermission.USER_IGNORE_LIST : AppPermission.USER_RW);
        }
        dbManager.close();
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
            CounterAction a = new CounterAction();
            a.setWindowAction(PipelineWindowAction.NO_WARNING_AND_STOP);
            a.setButtonAction(PipelineButtonAction.BACK_BUTTON);
            // Killing makes it to slow
            a.setKillState(CounterAction.KillState.DO_NOT_KILL);
            a.setHasExplainableButton(true);
            preventDisabelingAccessabilty.setCounterAction(a);

            WordProcessorSmartFilterBase accessibilityOverview = new WordListFilterExact(WordSmartFilterIdentifier.USER_1, new ArrayList<>(List.of(
                    new ArrayList<>(List.of("Use " + myAppName)),
                    new ArrayList<>(List.of("Stop " + myAppName + "?")),
                    new ArrayList<>(List.of("Downloaded apps")) //accessabilty site block

//                    ,new ArrayList<>(List.of(new String[]{"Accessibility"}))
            )), false, preventDisabelingAccessabilty, false);


            filters.add(accessibilityOverview);
        }
        // device admin stuff doesn't show up in access service we have to block the way
        {
            PipelineResultKeywordFilter preventTurnOfDeviceAdmin = new PipelineResultKeywordFilter("");
            CounterAction a = new CounterAction();
            a.setWindowAction(PipelineWindowAction.NO_WARNING_AND_STOP);
            a.setButtonAction(PipelineButtonAction.HOME_BUTTON);
            // Killing makes it to slow
            a.setKillState(CounterAction.KillState.DO_NOT_KILL);
            a.setHasExplainableButton(true);
            preventTurnOfDeviceAdmin.setCounterAction(a);
            WordProcessorSmartFilterBase searchForDeviceAdmin = new WordListFilterExact(WordSmartFilterIdentifier.USER_2, new ArrayList<>(List.of(
                    new ArrayList<>(List.of("Device admin apps")),
                    new ArrayList<>(List.of("Add a language")), // prevent switching language
                    new ArrayList<>(List.of("Debugging")), // prevent switching language
                    new ArrayList<>(List.of("Package installer")), // prevent turn on package installer notifcations again
                    new ArrayList<>(List.of("Device admin settings")),  // this is a invisible text
                    new ArrayList<>(List.of(new String[]{"OPEN", myAppName}))
            )), false, preventTurnOfDeviceAdmin, false);
            searchForDeviceAdmin.setCheckOnlyVisibleNodes(false);
            filters.add(searchForDeviceAdmin);

        }

        return new AppFilter(service, topicManager, filters, appName, true);
    }

    private AppFilter getPackageInstallerFilter() throws CloneNotSupportedException
    {
        String myAppName = "SafeZone";
        String appName = "com.google.android.packageinstaller";
        ArrayList<WordProcessorSmartFilterBase> filters = new ArrayList<WordProcessorSmartFilterBase>();
        /**
         * This code is only probaly needed in the debug version To prevent disabling via reinstalling
         * When you install an app on an Android device, especially in a development environment using Android Studio,
         * the app is treated as a new installation each time you uninstall
         * and then reinstall it.
         * This behavior is particularly relevant for apps that require special permissions, such as accessibility permissions.
         * Here are a few reasons why you need to request accessibility permission each time you reinstall your debug app
         */
        {
            PipelineResultKeywordFilter preventReinstallingAndLosePermissons = new PipelineResultKeywordFilter("");
            CounterAction a = new CounterAction();
            a.setWindowAction(PipelineWindowAction.NO_WARNING_AND_STOP);
            a.setButtonAction(PipelineButtonAction.BACK_BUTTON);
            // Killing makes it to slow
            a.setKillState(CounterAction.KillState.DO_NOT_KILL);
            a.setHasExplainableButton(false);
            preventReinstallingAndLosePermissons.setCounterAction(a);

            WordProcessorSmartFilterBase reinstallAppPopup = new WordListFilterExact(WordSmartFilterIdentifier.USER_1, new ArrayList<>(List.of(
                    new ArrayList<>(List.of(myAppName, "Do you want to install this app?")),
                    new ArrayList<>(List.of(myAppName, "Do you want to update this app?"))
            )), false, preventReinstallingAndLosePermissons, false);

            filters.add(reinstallAppPopup);
        }
        return new AppFilter(service, topicManager, filters, appName, true);

    }

    private AppFilter getPocketCastsFilter() throws TopicMissingException, CloneNotSupportedException
    {

        String appName = "au.com.shiftyjelly.pocketcasts";
        ArrayList<WordProcessorSmartFilterBase> filters = new ArrayList<WordProcessorSmartFilterBase>();
        {
            PipelineResultKeywordFilter resultIgnoreSearch = new PipelineResultKeywordFilter("");
            CounterAction a = new CounterAction();
            a.setWindowAction(PipelineWindowAction.NO_WARNING_AND_STOP);
            a.setHasExplainableButton(true);
            resultIgnoreSearch.setCounterAction(a);
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
            CounterAction a = new CounterAction();
            a.setWindowAction(PipelineWindowAction.WARNING);
            a.setButtonAction(PipelineButtonAction.BACK_BUTTON);
            a.setHasExplainableButton(true);
            a.setKillState(CounterAction.KillState.KILL_BEFORE_WINDOW);
            pornResult.setCounterAction(a);
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
            CounterAction a = new CounterAction();
            a.setWindowAction(PipelineWindowAction.NO_WARNING_AND_STOP);
            a.setHasExplainableButton(true);
            ignoreHistoryPage.setCounterAction(a);
            // Add test Filter
            WordProcessorSmartFilterBase ignoreSearch = new WordListFilterExact(WordSmartFilterIdentifier.USER_1, new ArrayList<>(List.of(new ArrayList<>(List.of("History", "Recently closed tabs")))), false, ignoreHistoryPage, false);
            filters.add(ignoreSearch);
        }

        {
            // note have to run before block search engines as suggestion to bad engies are blocked
            // ignore suggestion screen
            PipelineResultKeywordFilter resultIgnoreSearch = new PipelineResultKeywordFilter("");
            CounterAction a = new CounterAction();
            a.setWindowAction(PipelineWindowAction.NO_WARNING_AND_STOP);
            a.setHasExplainableButton(true);
            resultIgnoreSearch.setCounterAction(a);
            // Add test Filter
            WordProcessorSmartFilterBase ignoreSearch = new WordListFilterExact(WordSmartFilterIdentifier.USER_2, new ArrayList<>(List.of(new ArrayList<>(List.of("Firefox Suggest")))), false, resultIgnoreSearch, false);
            filters.add(ignoreSearch);
        }
        {
            // ignore history page
            PipelineResultKeywordFilter ignoreStartpage = new PipelineResultKeywordFilter("");
            CounterAction a = new CounterAction();
            a.setWindowAction(PipelineWindowAction.NO_WARNING_AND_STOP);
            a.setHasExplainableButton(true);
            ignoreStartpage.setCounterAction(a);
            // Add test Filter
            WordProcessorSmartFilterBase ignoreSearch = new WordListFilterExact(WordSmartFilterIdentifier.USER_3, new ArrayList<>(List.of(new ArrayList<>(List.of("Firefox", "Jump back in")))), false, ignoreStartpage, false);
            ignoreSearch.setCheckOnlyVisibleNodes(false);
            filters.add(ignoreSearch);
        }
        // Block stuff
        {
            //block unsafe search
            PipelineResultKeywordFilter blockUnsafesearch = new PipelineResultKeywordFilter("");
            CounterAction a = new CounterAction();
            a.setWindowAction(PipelineWindowAction.WARNING);
            a.setButtonAction(PipelineButtonAction.BACK_BUTTON);
            a.setHasExplainableButton(true);
            blockUnsafesearch.setCounterAction(a);
            ArrayList<TopicScoring> allScorings = new ArrayList<>();
            allScorings.add(new TopicScoring("enforce_safe_search", 100, 0));
            boolean ignoreCase = false;  // important for porn filter

            WordListFilterScored blockAdultStuff = new WordListFilterScored(WordSmartFilterIdentifier.ENFORCE_SAFE_SEARCH, allScorings, ignoreCase, topicManager, blockUnsafesearch);
            filters.add(blockAdultStuff);
        }


        {
            PipelineResultKeywordFilter pornResult = new PipelineResultKeywordFilter("");
            CounterAction a = new CounterAction();
            a.setWindowAction(PipelineWindowAction.WARNING);
            a.setButtonAction(PipelineButtonAction.BACK_BUTTON);
            a.setHasExplainableButton(true);
            a.setKillState(CounterAction.KillState.KILL_BEFORE_WINDOW);
            pornResult.setCounterAction(a);
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
        if (!isDebugVersion)
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
            CounterAction a = new CounterAction();
            a.setWindowAction(PipelineWindowAction.WARNING);
            a.setButtonAction(PipelineButtonAction.BACK_BUTTON);
            a.setHasExplainableButton(true);
            blockUnsafesearch.setCounterAction(a);
            ArrayList<TopicScoring> allScorings = new ArrayList<>();
            allScorings.add(new TopicScoring("enforce_safe_search", 100, 100));
            boolean ignoreCase = false;  // important for porn filter

            WordListFilterScored blockAdultStuff = new WordListFilterScored(WordSmartFilterIdentifier.ENFORCE_SAFE_SEARCH, allScorings, ignoreCase, topicManager, blockUnsafesearch);
            filters.add(blockAdultStuff);
        }


        {
            PipelineResultKeywordFilter pornResult = new PipelineResultKeywordFilter("");
            CounterAction a = new CounterAction();
            a.setWindowAction(PipelineWindowAction.WARNING);
            a.setButtonAction(PipelineButtonAction.BACK_BUTTON);
            a.setHasExplainableButton(true);
            a.setKillState(CounterAction.KillState.KILL_BEFORE_WINDOW);
            pornResult.setCounterAction(a);
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

    private AppFilter getAppolo() throws CloneNotSupportedException
    {
        String appName = "org.nuclearfog.apollo";
        ArrayList<WordProcessorSmartFilterBase> filters = new ArrayList<WordProcessorSmartFilterBase>();

        AppFilter filter = new AppFilter(service, topicManager, filters, appName, false);

        ArrayList<ProductivityTimeRule> timeRules = new ArrayList<>();
        timeRules.add(new ProductivityTimeRule(LocalTime.of(11,0),LocalTime.of(12,0),EnumSet.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY),true));
        filter.setSpecialSmartFilter(SpecialSmartFilterBase.Name.TIME_LIMIT, new ProductivityFilter(new CounterAction(PipelineWindowAction.WARNING,PipelineButtonAction.BACK_BUTTON,true), "Time limit", 6, 200, 3,timeRules));
        return filter;
    }

    private AppFilter getTelegramFilter2() throws CloneNotSupportedException
    {
        String appName = "org.telegram.messenger";
        ArrayList<WordProcessorSmartFilterBase> filters = new ArrayList<WordProcessorSmartFilterBase>();
        {
            PipelineResultKeywordFilter resultIgnoreSearch = new PipelineResultKeywordFilter("");
            CounterAction a = new CounterAction();
            a.setWindowAction(PipelineWindowAction.NO_WARNING_AND_STOP);
            a.setButtonAction(PipelineButtonAction.BACK_BUTTON);
            a.setKillState(CounterAction.KillState.KILL_BEFORE_WINDOW);
            a.setHasExplainableButton(true);
            resultIgnoreSearch.setCounterAction(a);
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
            CounterAction a = new CounterAction();
            a.setWindowAction(PipelineWindowAction.NO_WARNING_AND_STOP);
            a.setHasExplainableButton(true);
            ignoreSettings.setCounterAction(a);
            // Add test Filter
            WordProcessorSmartFilterBase ignoreSearch = new WordListFilterExact(WordSmartFilterIdentifier.USER_1,
                    new ArrayList<>(List.of(new ArrayList<>(List.of("Settings", "Content")))), false, ignoreSettings, false);
            filters.add(ignoreSearch);
        }
        {
            // ignore history page
            PipelineResultKeywordFilter ignoreSettings = new PipelineResultKeywordFilter("");
            CounterAction a = new CounterAction();
            a.setWindowAction(PipelineWindowAction.NO_WARNING_AND_STOP);
            a.setHasExplainableButton(true);
            ignoreSettings.setCounterAction(a);
            // Add test Filter
            WordProcessorSmartFilterBase ignoreSearch = new WordListFilterExact(WordSmartFilterIdentifier.USER_2, new ArrayList<>(List.of(new ArrayList<>(List.of("Search")))), false, ignoreSettings, true);
            filters.add(ignoreSearch);
        }


        {
            PipelineResultKeywordFilter pornResult = new PipelineResultKeywordFilter("");
            CounterAction a = new CounterAction();
            a.setWindowAction(PipelineWindowAction.WARNING);
            a.setButtonAction(PipelineButtonAction.BACK_BUTTON);
            a.setHasExplainableButton(true);
            a.setKillState(CounterAction.KillState.KILL_BEFORE_WINDOW);
            pornResult.setCounterAction(a);
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
        //Productive Filter
        AppFilter filter = new AppFilter(service, topicManager, filters, appName, false);
        {
            // No Timerules
            filter.setSpecialSmartFilter(SpecialSmartFilterBase.Name.TIME_LIMIT, new ProductivityFilter(new CounterAction(PipelineWindowAction.WARNING,PipelineButtonAction.BACK_BUTTON,true), "Time limit", 1, 200, 3,new ArrayList<>()));
        }
        return filter;


    }

    private AppFilter getArdMediathekFilter() throws TopicMissingException, CloneNotSupportedException
    {
        String appName = "de.ard.audiothek";
        ArrayList<WordProcessorSmartFilterBase> filters = new ArrayList<WordProcessorSmartFilterBase>();
        {
            // ignore search suggestions
            PipelineResultKeywordFilter ignoreSearchSuggestions = new PipelineResultKeywordFilter("");
            CounterAction a = new CounterAction();
            a.setWindowAction(PipelineWindowAction.NO_WARNING_AND_STOP);
            a.setHasExplainableButton(true);
            ignoreSearchSuggestions.setCounterAction(a);
            WordProcessorSmartFilterBase ignoreSearch = new WordListFilterExact(WordSmartFilterIdentifier.USER_1,
                    new ArrayList<>(List.of(new ArrayList<>(List.of("Suche nach Episoden und Podcasts")))), false, ignoreSearchSuggestions, true);
            filters.add(ignoreSearch);
        }


        {
            PipelineResultKeywordFilter pornResult = new PipelineResultKeywordFilter("");
            CounterAction a = new CounterAction();
            a.setWindowAction(PipelineWindowAction.WARNING);
            a.setButtonAction(PipelineButtonAction.BACK_BUTTON);
            a.setHasExplainableButton(true);
            a.setKillState(CounterAction.KillState.KILL_BEFORE_WINDOW);
            pornResult.setCounterAction(a);
            ArrayList<TopicScoring> allScorings = new ArrayList<>();
            allScorings.add(new TopicScoring("porn_explicit", 33, 0));
            allScorings.add(new TopicScoring("female_body_parts", 30, 0));
            allScorings.add(new TopicScoring("adult_nudity", 32, 0));
            allScorings.add(new TopicScoring("adult_sex_toys", 32, 0));
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
        list.add(getArdMediathekFilter());
        list.add(getFirefoxFilter());
        list.add(getPocketCastsFilter());
        list.add(getTelegramFilter2());
        list.add(getYoutubeFilter());
        list.add(getPackageInstallerFilter());
        list.add(getAndroidSettings());
        list.add(getAppolo());
        list.add(getDefaultFilter());

        return list;
    }
}
