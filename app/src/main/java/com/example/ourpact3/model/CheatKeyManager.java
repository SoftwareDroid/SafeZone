package com.example.ourpact3.model;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.ourpact3.util.PreferencesKeys;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CheatKeyManager {
    private Context context;
    private final String KEY_FILE = "key.txt";
    // Should match with: https://www.texttool.com/sha256-online
    private final String EXPECTED_HASH_DISABLE_FILTERING = "507ce4f8b5eade3904fb1695132fe9cc4adae1d89e7f273c106ce80b8a15ed4a"; // replace with your static hash string
    private long cacheExpirationTime;
    private boolean isFilteringDisabledCache;
    private long cacheTimestamp;

    public CheatKeyManager(Context context, int cacheTimeInSeconds) {
        this.context = context;
        this.cacheExpirationTime = cacheTimeInSeconds * 1000; // convert to milliseconds
    }

    private String getFolder() {
        File[] mediaDirs = context.getExternalMediaDirs();
        if (mediaDirs != null && mediaDirs.length > 0) {
            return mediaDirs[0].getPath();
        }
        return null;
    }

    // Method to calculate hash from a file
    public static String calculateHash(String filePath) {
        try (FileInputStream fis = new FileInputStream(filePath)) {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                md.update(buffer, 0, bytesRead);
            }
            return bytesToHex(md.digest());
        } catch (IOException | NoSuchAlgorithmException e) {
            return null;
        }
    }

    // Overloaded method to calculate hash from a normal string
    public static String calculateHashFromString(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(input.getBytes());
            return bytesToHex(md.digest());
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    // Helper method to convert byte array to hex string
    private static String bytesToHex(byte[] hashBytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : hashBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public boolean isServiceIsDisabled(Context context) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - cacheTimestamp < cacheExpirationTime) {
            return isFilteringDisabledCache;
        }
        // Access SharedPreferences
        SharedPreferences sharedPreferences = context.getSharedPreferences(PreferencesKeys.MAIN_PREFERENCES, MODE_PRIVATE);
        boolean preventDisableing = sharedPreferences.getBoolean(PreferencesKeys.PREVENT_DISABLING, PreferencesKeys.PREVENT_DISABLING_DEFAULT_VALUE);
        if(!preventDisableing)
        {
            isFilteringDisabledCache = true;
            cacheTimestamp = currentTime;
            return true;
        }
        String folder = getFolder();
        if (folder == null) {
            return false;
        }

        String filePath = folder + "/" + KEY_FILE;
        String hash = calculateHash(filePath);
        if (hash == null) {
            return false;
        }

        isFilteringDisabledCache = hash.equals(EXPECTED_HASH_DISABLE_FILTERING);
        cacheTimestamp = currentTime;
        return isFilteringDisabledCache;
    }
}