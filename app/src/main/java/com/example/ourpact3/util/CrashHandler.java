package com.example.ourpact3.util;

import android.app.NotificationManager;
import android.content.Context;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.ourpact3.MainActivity;

public class CrashHandler implements Thread.UncaughtExceptionHandler {

    private static final String CRASH_REPORT_FOLDER = "crashes";
    private static final String CRASH_REPORT_EXTENSION = ".txt";
    private static final String CHANNEL_ID = "crash_notification_channel";
    private static final int NOTIFICATION_ID = 1;
    private Context context;

    public CrashHandler(Context context) {
        this.context = context;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        writeCrashReport(throwable);
        // You can also call the default exception handler here if you want
        // Thread.getDefaultUncaughtExceptionHandler().uncaughtException(thread, throwable);
    }

    private String getCrashReportFolder() {
        File[] mediaDirs = context.getExternalMediaDirs();
        if (mediaDirs != null && mediaDirs.length > 0) {
            File crashReportFolder = new File(mediaDirs[0], CRASH_REPORT_FOLDER);
            if (!crashReportFolder.exists()) {
                crashReportFolder.mkdirs();
            }
            return crashReportFolder.getAbsolutePath();
        } else {
            // Fallback to a different directory, such as getExternalStorageDirectory()
            return "";
        }
    }

    private void writeCrashReport(Throwable throwable) {
        String crashReportFolder = getCrashReportFolder();
        if (crashReportFolder == null) {
            return;
        }

        String crashReportFileName = getCrashReportFileName();
        File crashReportFile = new File(crashReportFolder, crashReportFileName);

        try (PrintWriter writer = new PrintWriter(new FileWriter(crashReportFile))) {
            writer.println(getCrashReportHeader());
            writer.println(getStackTrace(throwable));
        } catch (IOException e) {
            // Handle the exception
        }
    }
/*
    private String getCrashReportFolder() {
        File externalStorage = Environment.getExternalStorageDirectory();
        if (externalStorage == null) {
            return null;
        }

        File crashReportFolder = new File(externalStorage, CRASH_REPORT_FOLDER);
        if (!crashReportFolder.exists()) {
            crashReportFolder.mkdirs();
        }

        return crashReportFolder.getAbsolutePath();
    }*/

    private String getCrashReportFileName() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        return "crash_" + dateFormat.format(new Date()) + CRASH_REPORT_EXTENSION;
    }

    private String getCrashReportHeader() {
        return "Crash Report\n" +
                "-----------\n" +
                "Date: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "\n" +
                "Device: " + android.os.Build.DEVICE + "\n" +
                "Model: " + android.os.Build.MODEL + "\n" +
                "Android Version: " + android.os.Build.VERSION.RELEASE + "\n";
    }

    private String getStackTrace(Throwable throwable) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        throwable.printStackTrace(printWriter);
        return stringWriter.toString();
    }

    private void showCrashNotification() {
        // Create a notification manager
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Create a notification channel for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Crash Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            notificationManager.createNotificationChannel(channel);
        }

        // Create an intent to open the app when the notification is clicked
        Intent intent = new Intent(context, MainActivity.class); // Replace with your main activity
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_alert) // Set your own icon
                .setContentTitle("Application Crash")
                .setContentText("The application has crashed. Tap to reopen.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        // Show the notification
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}
