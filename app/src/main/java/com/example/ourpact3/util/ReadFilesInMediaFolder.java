package com.example.ourpact3.util;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
public class ReadFilesInMediaFolder
{
    public static String loadFileContent(Context context, String fileName) {
        File[] mediaDirs = context.getExternalMediaDirs();

        for (File mediaDir : mediaDirs) {
            File file = new File(mediaDir, fileName);
            if (file.exists()) {
                return readFileAsString(file);
            }
        }
        return null; // File not found
    }

    private static String readFileAsString(File file) {
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
        } catch (IOException e) {
            Log.e("FileReadError", "Error reading file: " + e.getMessage());
        }
        return stringBuilder.toString();
    }
}
