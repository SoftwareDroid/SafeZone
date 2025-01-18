package com.example.ourpact3;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import com.example.ourpact3.unused.DatabaseManager;
import com.example.ourpact3.unused.UsageSmartFilterManager;
import com.example.ourpact3.learn_mode.LearnModeComponent;
import com.example.ourpact3.model.CheatKeyManager;
import com.example.ourpact3.service.AppPermission;
import com.example.ourpact3.service.ExampleAppKeywordFilters;
import com.example.ourpact3.service.ScreenReceiver;
import com.example.ourpact3.smart_filter.AppFilter;
import com.example.ourpact3.smart_filter.UsageRestrictionsFilter;
import com.example.ourpact3.smart_filter.SpecialSmartFilterBase;
import com.example.ourpact3.util.CrashHandler;
import com.example.ourpact3.pipeline.PipelineResultBase;
import com.example.ourpact3.service.AppKiller;
import com.example.ourpact3.service.IContentFilterService;
import com.example.ourpact3.service.NormalModeComponent;
import com.example.ourpact3.topics.TopicManager;
import com.example.ourpact3.util.PreferencesKeys;

import org.jetbrains.annotations.NotNull;

import java.util.TreeMap;

// https://developer.android.com/guide/topics/ui/accessibility/service

/**
 * NOTE: do not rename class then app_dev.sh doesn't work anymore
 */
public class ContentFilterService extends AccessibilityService implements IContentFilterService
{
    private ScreenReceiver screenReceiver;
    private NormalModeComponent normalModeProcessor;
    private AppKiller appKillerService;
    private LearnModeComponent learnModeComponent;
    private Mode mode = Mode.NORMAL_MODE;
    private final TopicManager topicManager = new TopicManager();
    private CrashHandler crashHandler;
    private CheatKeyManager cheatKeyManager;
    private TreeMap<String, AppPermission> usedAppPermissions;
    private BroadcastReceiver commandReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String command = intent.getStringExtra("command");
            assert command != null;
            if (command.equals(COMMAND_RELOAD_SETTINGS))
            {
                reloadSettings();
            } else if (command.equals(COMMAND_RELOAD_USAGE_FILTER_FOR_APP))
            {
                String packageId = intent.getStringExtra("app");
                reloadUsageFilterForApp(packageId);
            }
        }
    };

    private void reloadUsageFilterForApp(String packageId)
    {
        if (packageId == null)
        {
            return;
        }
        // reload changes
        DatabaseManager.open();
        DatabaseManager.AppRuleTuple appRuleTuple = DatabaseManager.getAppRuleByPackageId(packageId);
        assert appRuleTuple != null;
        UsageRestrictionsFilter usageRestrictionsFilter = UsageSmartFilterManager.getUsageFilterById(appRuleTuple.usageFilterID);
        assert usageRestrictionsFilter != null;
        DatabaseManager.close();
        normalModeProcessor.appFilters.get(packageId).setSpecialSmartFilter(SpecialSmartFilterBase.Name.USAGE_RESTRICTION, usageRestrictionsFilter);
    }

    //    private boolean isRunning = false;
    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @Override
    public void onServiceConnected()
    {
        // setup crash handler
        crashHandler = new CrashHandler(this, this);
        Thread.setDefaultUncaughtExceptionHandler(crashHandler);
        // internal broadcast receiver to receive commands e.g reload all settings
        IntentFilter intentFilter = new IntentFilter("SEND_COMMAND");
        registerReceiver(commandReceiver, intentFilter);
        //
        {
            screenReceiver = new ScreenReceiver(this);
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_SCREEN_ON);
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            registerReceiver(screenReceiver, filter);
        }
        learnModeComponent = new LearnModeComponent(this, this, this);
        this.setNewMode(Mode.NORMAL_MODE);

        normalModeProcessor = new NormalModeComponent(this, this, this);

        appKillerService = new AppKiller(this, this);
        cheatKeyManager = new CheatKeyManager(this, 45); //TODO: constant

// get WindowManager needed for creating overlay window
// Load all system topics

        try
        {
            //TODO: load all topics
            // check all topics
//            topicManager.checkAllTopics();
            // load all example filters
            ExampleAppKeywordFilters exampleFilters = new ExampleAppKeywordFilters(this, this.topicManager, this.getApplicationContext());
            this.usedAppPermissions = exampleFilters.getAppPermissionsFromDB(this.getApplicationContext());
            exampleFilters.addExampleTopics();
            for (AppFilter filter : exampleFilters.getAllExampleFilters())
            {
                filter.setCallback(normalModeProcessor);
                normalModeProcessor.appFilters.put(filter.getPackageName(), filter);
            }
            reloadSettings();
            AccessibilityServiceInfo info = new AccessibilityServiceInfo();
            info.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
            info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
            info.flags = AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS;
            setServiceInfo(info);

        } catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public boolean isMagnificationEnabled()
    {
        ContentResolver cr = getContentResolver();
        return Settings.Secure.getInt(cr, "accessibility_display_magnification_enabled", 0) == 1;
    }

    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable scheduledRunnable;
    private boolean isHandlerScheduled = false;

    @SuppressLint("NewApi")
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event)
    {
        // the a cheat key is used then don't filte
        if (cheatKeyManager.isServiceIsDisabled(getBaseContext()))
        {
            return;
        }
        if (event.getPackageName() != null && event.getPackageName().equals("com.android.system.ui"))
        {

        }
        // Schedule the execution of processPipelineResults in the foreground after 500ms
        // Create a new runnable to execute in the foreground after 500ms
        // If a handler is already scheduled, remove it
        normalModeProcessor.processPipelineResults();
        if (isHandlerScheduled)
        {
            handler.removeCallbacks(scheduledRunnable);
        }
        scheduledRunnable = new Runnable()
        {
            @Override
            public void run()
            {
                // Execute the method in the foreground
                normalModeProcessor.processPipelineResults();
                isHandlerScheduled = false; // Reset the flag after execution
            }
        };
        isHandlerScheduled = true; // Set the flag to indicate a handler is scheduled

        // Schedule the new runnable with a delay of 500ms
        handler.postDelayed(scheduledRunnable, 200);


        // never process this for UI control reasons
        if (event == null || event.getPackageName() == null || event.getPackageName().equals(this.getPackageName()))
        {
            return;
        }
        if (mode == Mode.LEARN_OVERLAY_MODE)
        {
            this.normalModeProcessor.onAccessibilityEvent(event);
            this.learnModeComponent.onAccessibilityEvent(event);
        } else if (mode == Mode.NORMAL_MODE)
        {
            String appName = event.getPackageName().toString();
            if (!this.isPackageIgnoredForNormalMode(appName))
            {
                this.normalModeProcessor.onAccessibilityEvent(event);
            } else
            {
                // we still have to update app changes, to e.g. correctly calculate time and usage blocks
                this.normalModeProcessor.updatePotentialAppChange(appName);
            }

        } else if (mode == Mode.APP_KILL_MODE_1)
        {
            try
            {
                Log.d("KILLER", "APP killMode");
                this.appKillerService.onAccessibilityEvent(event);
            } catch (InterruptedException ignored)
            {
                Log.d("KILLER", "APP killMode interrupt");

            }
        }

    }


    @Override
    public void onInterrupt()
    {
        Log.d("a", "a");
    }

    @Override
    public void forwardPipelineResultToLearner(PipelineResultBase result)
    {
        this.learnModeComponent.onPipelineResult(result);
    }

    @Override
    public Mode getMode()
    {
        return mode;
    }

    @Override
    public void activateAppKillMode(@NotNull PipelineResultBase resultBase)
    {
        setNewMode(Mode.APP_KILL_MODE_1);
        this.appKillerService.setApp(resultBase);
    }

    @Override
    public void activateLearnMode()
    {
        setNewMode(Mode.LEARN_OVERLAY_MODE);
    }

    @Override
    public void stopLearnMode()
    {
        setNewMode(Mode.NORMAL_MODE);
    }

    @Override
    public void onScreenStateChange(boolean on)
    {
        if (this.mode == Mode.NORMAL_MODE)
        {
            this.normalModeProcessor.onScreenStateChange(on);
        }
    }

    public void setNewMode(Mode mode)
    {
        this.mode = mode;
        switch (mode)
        {
            case NORMAL_MODE:
                this.learnModeComponent.stopOverlay();
                break;
            case APP_KILL_MODE_1:
                this.learnModeComponent.stopOverlay();
                break;
            case LEARN_OVERLAY_MODE:
                this.learnModeComponent.createOverlay();
                break;
        }
    }

    @Override
    public void finishAppKilling(PipelineResultBase lastResult)
    {
        // Perhaps show warning or notification after killing
        this.mode = Mode.NORMAL_MODE;
        this.normalModeProcessor.onPipelineResultForeground(lastResult);
    }

    @Override
    public boolean isPackageIgnoredForNormalMode(String id)
    {
        AppPermission permission = this.usedAppPermissions.get(id);
        if (id != null && permission != null)
        {
            return permission == AppPermission.USER_IGNORE_LIST;
        }
        return false;
    }

    @Override
    public boolean isPackagedIgnoredForLearning(String id)
    {
        AppPermission permission = this.usedAppPermissions.get(id);
        if (permission != null)
        {
            return permission != AppPermission.USER_RW;
        }
        return false;
    }

    @Override
    public void destroyGUI()
    {
        if (this.learnModeComponent != null)
        {
            this.learnModeComponent.stopOverlay();
            this.learnModeComponent.stopOverlay();
        }
        if (this.normalModeProcessor != null)
        {
            this.normalModeProcessor.destroyGUI();
        }
    }

    @Override
    public void onAppChange(String oldApp, String newApp)
    {
        this.learnModeComponent.onAppChange(oldApp, newApp);
        if (this.mode == Mode.NORMAL_MODE)
        {
            this.normalModeProcessor.onAppChange(oldApp, newApp);
        }
    }

    @Override
    public void setSpecialSmartFilter(String app, SpecialSmartFilterBase.Name name, SpecialSmartFilterBase filter)
    {
        AppFilter appFilter = this.normalModeProcessor.appFilters.get(app);
        if (appFilter != null)
        {
            appFilter.setSpecialSmartFilter(name, filter);
        }
    }

    @Override
    public SpecialSmartFilterBase getSpecialSmartFilter(String app, SpecialSmartFilterBase.Name name)
    {
        AppFilter appFilter = this.normalModeProcessor.appFilters.get(app);
        if (appFilter != null)
        {
            return appFilter.getSpecialSmartFilter(name);
        }
        return null;
    }


    public static final String COMMAND_RELOAD_SETTINGS = "reload";
    public static final String COMMAND_RELOAD_USAGE_FILTER_FOR_APP = "reload_2";

    private void handleCommand(String command)
    {

    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        unregisterReceiver(commandReceiver);
    }


    private void reloadSettings()
    {
        SharedPreferences sharedPreferences = getSharedPreferences(PreferencesKeys.MAIN_PREFERENCES, MODE_PRIVATE);
        boolean useWarnWindows = sharedPreferences.getBoolean(PreferencesKeys.OPTION_USE_WARN_WINDOWS, PreferencesKeys.OPTION_USE_WARN_WINDOWS_DEFAULT);
        boolean useLogging = sharedPreferences.getBoolean(PreferencesKeys.OPTION_LOG_BLOCKING, PreferencesKeys.OPTION_LOG_BLOCKING_DEFAULT);
        normalModeProcessor.useWarnWindows = useWarnWindows;
        normalModeProcessor.useLogging = useLogging;
    }

}
