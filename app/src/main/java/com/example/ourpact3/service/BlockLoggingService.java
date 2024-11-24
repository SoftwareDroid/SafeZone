package com.example.ourpact3.service;

import android.content.Context;

import com.example.ourpact3.pipeline.PipelineResultBase;

import org.json.JSONException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BlockLoggingService
{
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private static final String LOG_FOLDER = "logs";
    private final Context context;

    public BlockLoggingService(Context ctx)
    {
        this.context = ctx;
    }
    private String getLogFolder() {
        File[] mediaDirs = context.getExternalMediaDirs();
        if (mediaDirs != null && mediaDirs.length > 0) {
            File crashReportFolder = new File(mediaDirs[0], LOG_FOLDER);
            if (!crashReportFolder.exists()) {
                crashReportFolder.mkdirs();
            }
            return crashReportFolder.getAbsolutePath();
        } else {
            // Fallback to a different directory, such as getExternalStorageDirectory()
            return "";
        }
    }
    public void logScreenBG(ScreenInfoExtractor.Screen screen, PipelineResultBase result2)
    {
        executorService.execute(() -> {
        this.logScreen(screen,result2);
        });
    }

    private void logScreen(ScreenInfoExtractor.Screen screen, PipelineResultBase result2)
    {
        Date currentDate = new Date();
        // Create a SimpleDateFormat instance with the desired format
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd_MM_yyyy__HH_mm_SSS", Locale.getDefault());

        // Format the current date
        ;
        String line = "";
        String timestamp = dateFormat.format(currentDate);
        line = timestamp + result2.convertToLogEntry(this.context) + "\n";
        KeywordScoreWindowCalculator scoreExplainer = new KeywordScoreWindowCalculator();
        line += scoreExplainer.getDebugFilterState(result2.getScreen(), result2.getCurrentAppFilter());
        String jsonString = "";
        try
        {
            jsonString = screen.toJson().toString();
            line += "\n" + jsonString + "\n";

        } catch (JSONException e)
        {

        }

        String crashReportFileName = getLogFileName();
        File logFile = new File(getLogFolder(), crashReportFileName);
        try (PrintWriter writer = new PrintWriter(new FileWriter(logFile))) {
            writer.println(line);
        } catch (IOException e) {
            // Handle the exception
        }

        // write Screen
        if(!jsonString.isEmpty())
        {
            File screenLog = new File(getLogFolder(), getScreenLogName());

            try (PrintWriter writer2 = new PrintWriter(new FileWriter(screenLog)))
            {
                writer2.println(jsonString);
            } catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }
    }
    private String getLogFileName() {
        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd_MM_yyyy__HH_mm_ss");
        return "app_" +  dateFormat.format(currentDate) + ".txt";
    }
    private String getScreenLogName() {
        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd_MM_yyyy__HH_mm_ss");
        return "screen_" + dateFormat.format(currentDate) + ".txt";
    }
}
