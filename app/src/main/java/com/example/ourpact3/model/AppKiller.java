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
import android.net.Uri;
import android.os.Process;
import android.provider.Settings;
import android.view.accessibility.AccessibilityEvent;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class AppKiller {
    public void onAccessibilityEvent(AccessibilityEvent event)
    {

    }
    public static void openAppSettingsForPackage(Context context, String packageName) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", packageName, null);
        intent.setData(uri);

        // Add the FLAG_ACTIVITY_NEW_TASK flag
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        context.startActivity(intent);
    }

}
