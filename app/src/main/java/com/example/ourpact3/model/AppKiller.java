package com.example.ourpact3.model;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.media.session.MediaSessionManager;
import android.media.session.PlaybackState;
import android.os.Process;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class AppKiller {
    public static void forceStopApp(Context context,String packageName) {
        PackageManager pm = context.getPackageManager();
        pm.setApplicationEnabledSetting(packageName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, 0);
    }

    public static void killBackgroundProcess(Context context, String packageName) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        activityManager.killBackgroundProcesses(packageName);
        stopMedia(context,packageName);
    }
    public static void stopMedia(Context context, String packageName)
    {
        MediaSessionManager mediaSessionManager = (MediaSessionManager) context.getSystemService(Context.MEDIA_SESSION_SERVICE);

        for (MediaController mediaController :  mediaSessionManager.getActiveSessions(null)) {
            if(!Objects.equals(mediaController.getPackageName(), packageName))
            {
                continue;
            }
            // Check if the media controller is connected and has a playback state
            PlaybackState playbackState = mediaController.getPlaybackState();
            if (playbackState != null) {
                // Check if the media is currently playing
                if (playbackState.getState() == PlaybackState.STATE_PLAYING) {
                    // Pause the media
                    mediaController.getTransportControls().pause();
                } else if (playbackState.getState() == PlaybackState.STATE_PAUSED) {
                    // Optionally, you can stop the media instead
                    mediaController.getTransportControls().stop();
                }
            }}

    }
}
