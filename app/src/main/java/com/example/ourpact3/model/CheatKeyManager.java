package com.example.ourpact3.model;

import android.content.Context;
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

    private String calculateHash(String filePath) {
        try (FileInputStream fis = new FileInputStream(filePath)) {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                md.update(buffer, 0, bytesRead);
            }
            byte[] hashBytes = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (IOException | NoSuchAlgorithmException e) {
            return null;
        }
    }

    public boolean isServiceIsDisabled() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - cacheTimestamp < cacheExpirationTime) {
            return isFilteringDisabledCache;
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