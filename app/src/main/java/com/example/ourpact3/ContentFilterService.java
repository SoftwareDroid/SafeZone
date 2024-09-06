package com.example.ourpact3;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import android.view.inputmethod.InputMethodManager;

import com.example.ourpact3.model.CheatKeyManager;
import com.example.ourpact3.model.CrashHandler;
import com.example.ourpact3.service.AppKiller;
import com.example.ourpact3.service.IContentFilterService;
import com.example.ourpact3.service.TextFilterService;
import com.example.ourpact3.model.Topic;
import com.example.ourpact3.model.TopicLoader;
import com.example.ourpact3.model.TopicManager;

import java.util.ArrayList;
import java.util.Set;

// https://developer.android.com/guide/topics/ui/accessibility/service

/**
 * NOTE: do not rename class then app_dev.sh doesn't work anymore
 */
public class ContentFilterService extends AccessibilityService implements IContentFilterService
{
    private TextFilterService filterServiceManager;
    private AppKiller appKillerService;



    private Mode mode = Mode.NORMAL_MODE;
    private final TopicManager topicManager = new TopicManager();
    private CrashHandler crashHandler;
    private CheatKeyManager cheatKeyManager;
    private long stopEventProcessingUntil;

    //    private boolean isRunning = false;
    @Override
    public void onServiceConnected()
    {
        Log.i("FOO", "Stating service");
        crashHandler = new CrashHandler(this);
        filterServiceManager = new TextFilterService(this,this);
        appKillerService = new AppKiller(this,this);
        cheatKeyManager = new CheatKeyManager(this, 45);
        Thread.setDefaultUncaughtExceptionHandler(crashHandler);
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
            exampleFilters.addExampleTopics();
            for (AppFilter filter : exampleFilters.getAllExampleFilters())
            {
                filter.setCallback(filterServiceManager);
                filterServiceManager.keywordFilters.put(filter.getPackageName(), filter);
            }
            AccessibilityServiceInfo info = new AccessibilityServiceInfo();
            info.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
            info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
            info.flags = AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS;
            setServiceInfo(info);

            //


        } catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public boolean isKeyboardOpen()
    {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        return imm.isAcceptingText();
    }

    public boolean isMagnificationEnabled()
    {
        ContentResolver cr = getContentResolver();
        return Settings.Secure.getInt(cr, "accessibility_display_magnification_enabled", 0) == 1;
    }


    @SuppressLint("NewApi")
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event)
    {
        // the a cheat key is used then don't filter
        if (cheatKeyManager.isServiceIsDisabled())
        {
            return;
        }
//        AppKiller.openAppSettingsForPackage(getApplicationContext(),"au.com.shiftyjelly.pocketcasts");
        long currentTime = System.currentTimeMillis();
        // never process this for UI control reasons
        if (currentTime < this.stopEventProcessingUntil || event == null || event.getPackageName() == null || event.getPackageName().equals(this.getPackageName()))
        {
            return;
        }
        switch (mode)
        {
            case NORMAL_MODE:
                this.filterServiceManager.onAccessibilityEvent(event);
                break;
            case APP_KILL_MODE_1:
                this.appKillerService.onAccessibilityEvent(event);
                break;
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

    private void pauseEventProcessingFor(long timeInMs)
    {
        this.stopEventProcessingUntil = System.currentTimeMillis() + timeInMs;
    }

    @Override
    public void setMode(Mode m)
    {
        this.mode = m;
    }
}
