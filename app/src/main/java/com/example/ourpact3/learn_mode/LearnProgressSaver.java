package com.example.ourpact3.learn_mode;
import android.content.Context;
import android.os.Environment;

import com.example.ourpact3.util.ReadFilesInMediaFolder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

public class LearnProgressSaver
{
    public static AppLearnProgress load(Context context, String app, AppLearnProgress learnProgress) throws JSONException
    {
        if(app != null && app.length() > 1)
        {
            try
            {
                String file = convertAppNameToFileMame(app);
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
                String fileName = convertAppNameToFileMame(app);
                JSONObject jsonObject = learnProgress.toJson(); // Convert to JSON
                String jsonString = jsonObject.toString(); // Convert JSONObject to String

                // Get the external media directory
                File externalDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
                if (!externalDir.exists()) {
                    externalDir.mkdirs(); // Create the directory if it doesn't exist
                }

                // Create the file in the external media directory
                File file = new File(externalDir, fileName);
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(jsonString.getBytes());
                fos.close();
                return true; // Save successful
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                return false; // Save failed
            }
        }
        return false; // Invalid input
    }
    private static String convertAppNameToFileMame(String app)
    {
        String baseFileName = app.replace(".", "_");
        // Add a prefix or suffix if needed
        return baseFileName + "_learned_" + ".json";
    }
}
