package com.example.ourpact3;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import com.example.ourpact3.learn_mode.LearnModeComponent;
import com.example.ourpact3.model.CheatKeyManager;
import com.example.ourpact3.service.AppPermission;
import com.example.ourpact3.smart_filter.SpecialSmartFilterBase;
import com.example.ourpact3.util.CrashHandler;
import com.example.ourpact3.pipeline.PipelineResultBase;
import com.example.ourpact3.service.AppKiller;
import com.example.ourpact3.service.IContentFilterService;
import com.example.ourpact3.service.NormalModeComponent;
import com.example.ourpact3.topics.Topic;
import com.example.ourpact3.topics.TopicLoader;
import com.example.ourpact3.topics.TopicManager;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeMap;

// https://developer.android.com/guide/topics/ui/accessibility/service

/**
 * NOTE: do not rename class then app_dev.sh doesn't work anymore
 */
public class ContentFilterService extends AccessibilityService implements IContentFilterService
{
    private NormalModeComponent normalModeProcessor;
    ;
    private AppKiller appKillerService;
    private LearnModeComponent learnModeComponent;
    private Mode mode = Mode.NORMAL_MODE;
    private final TopicManager topicManager = new TopicManager();
    private CrashHandler crashHandler;
    private CheatKeyManager cheatKeyManager;
    private TreeMap<String, AppPermission> usedAppPermissions;
    //    private boolean isRunning = false;
    @Override
    public void onServiceConnected()
    {
        // setup crash handler
        crashHandler = new CrashHandler(this, this);
        Thread.setDefaultUncaughtExceptionHandler(crashHandler);
        //
        learnModeComponent = new LearnModeComponent(this, this,this);
        this.setNewMode(Mode.NORMAL_MODE);
        Log.i("FOO", "Stating service");

        normalModeProcessor = new NormalModeComponent(this, this);

        appKillerService = new AppKiller(this, this);
        cheatKeyManager = new CheatKeyManager(this, 45); //TODO: constant

// get WindowManager needed for creating overlay window
// Load all system topics
        TopicLoader topicLoader = new TopicLoader();
        String[] usedLanguages = {"de", "en"};
        ArrayList<TopicLoader.TopicDescriptor> allAvailableTopics = null;
        try
        {
            allAvailableTopics = topicLoader.getAllLoadableTopics(getApplicationContext(), Set.of(usedLanguages));
            // Check if all topics are not null
            for (TopicLoader.TopicDescriptor descriptor : allAvailableTopics)
            {
                Topic topic = topicLoader.loadTopicFile(getApplicationContext(), descriptor);
                if (topic != null)
                {
                    topicManager.addTopic(topic);
                }
            }
            // check all topics
            topicManager.checkAllTopics();
            // load all example filters
            ExampleAppKeywordFilters exampleFilters = new ExampleAppKeywordFilters(this, this.topicManager);
            this.usedAppPermissions = exampleFilters.getAppPermissions();
            exampleFilters.addExampleTopics();
            for (AppFilter filter : exampleFilters.getAllExampleFilters())
            {
                filter.setCallback(normalModeProcessor);
                normalModeProcessor.appFilters.put(filter.getPackageName(), filter);
            }
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
        // the a cheat key is used then don't filter
        if (cheatKeyManager.isServiceIsDisabled())
        {
            return;
        }

        // Schedule the execution of processPipelineResults in the foreground after 500ms
        // Create a new runnable to execute in the foreground after 500ms
        // If a handler is already scheduled, remove it
        normalModeProcessor.processPipelineResults();
        if (isHandlerScheduled) {
            handler.removeCallbacks(scheduledRunnable);
        }
        scheduledRunnable = new Runnable() {
            @Override
            public void run() {
                // Execute the method in the foreground
                normalModeProcessor.processPipelineResults();
                isHandlerScheduled = false; // Reset the flag after execution
            }
        };
        isHandlerScheduled = true; // Set the flag to indicate a handler is scheduled

        // Schedule the new runnable with a delay of 500ms
        handler.postDelayed(scheduledRunnable, 750);


        // never process this for UI control reasons
        if (event == null || event.getPackageName() == null || event.getPackageName().equals(this.getPackageName()))
        {
            return;
        }
        if(mode == Mode.LEARN_OVERLAY_MODE)
        {
            this.normalModeProcessor.onAccessibilityEvent(event);
            this.learnModeComponent.onAccessibilityEvent(event);
        } else if(mode == Mode.NORMAL_MODE)
        {
            if(!this.isPackageIgnoredForNormalMode(event.getPackageName().toString()))
            {
                this.normalModeProcessor.onAccessibilityEvent(event);
            }

        } else if(mode == Mode.APP_KILL_MODE_1)
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
    }


/*
    public void playSoundAndOverwriteMedia(Context context, String soundFileName) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int result = audioManager.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            MediaPlayer mediaPlayer = new MediaPlayer();
            try {
                AssetFileDescriptor afd = context.getAssets().openFd(soundFileName);
                mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.prepare();
                mediaPlayer.start();
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        audioManager.abandonAudioFocus(null);
                        mp.release();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }*/

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

    public boolean isPackageIgnoredForNormalMode(String id)
    {
        AppPermission permission = this.usedAppPermissions.get(id);
        if(id != null && permission != null)
        {
            return permission == AppPermission.USER_IGNORE_LIST;
        }
        return false;
    }

    @Override
    public boolean isPackagedIgnoredForLearning(String id)
    {
        AppPermission permission = this.usedAppPermissions.get(id);
        if(permission != null)
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

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {

        return START_STICKY;
    }


}
