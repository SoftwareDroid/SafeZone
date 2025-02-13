package com.example.ourpact3.unused.learn_mode;
import android.content.Context;

import com.example.ourpact3.util.ReadFilesInMediaFolder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

public class LearnProgressSaver
{
    private static final String FILE_ENDING = ".txt";
    public static AppLearnProgress load(Context context, String app)
    {
        if(app != null && app.length() > 1)
        {
            try
            {
                String file = convertAppNameToFileMame(app,context);
                String jsonString = ReadFilesInMediaFolder.loadFileContent(context, file);
                if (jsonString != null)
                {
                    JSONObject jsonObject = new JSONObject(jsonString);
                    return AppLearnProgress.fromJson(jsonObject);
                }
            } catch (Exception e)
            {
                return null;
            }
        }
        return null;
    }


    public static boolean save(Context context, String app, AppLearnProgress learnProgress) {
        if (app != null && learnProgress != null) {
            try {
                String fileName = convertAppNameToFileMame(app,context);
                JSONObject jsonObject = learnProgress.toJson(); // Convert to JSON
                String jsonString = jsonObject.toString(); // Convert JSONObject to String

                // Create the file in the external media directory
                File file = new File(getAndCreateFolder(context), fileName);
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(jsonString.getBytes());
                fos.close();
                return true; // Save successful
            } catch (IOException | JSONException e) {
                return false; // Save failed
            }
        }
        return false; // Invalid input
    }
    private static String getAndCreateFolder(Context context) {
        File[] mediaDirs = context.getExternalMediaDirs();
        if (mediaDirs != null && mediaDirs.length > 0) {
            File learnedFolder = new File(mediaDirs[0], "learned");
            if (!learnedFolder.exists()) {
                learnedFolder.mkdirs();
            }
            return learnedFolder.getAbsolutePath();
        } else {
            // Fallback to a different directory, such as getExternalStorageDirectory()
            return "";
        }
    }


    private static String convertAppNameToFileMame(String app,Context context)
    {
        String baseFileName = app.replace(".", "_");
        // Add a prefix or suffix if needed
        return baseFileName + "_learned_"  + FILE_ENDING;
    }
}
