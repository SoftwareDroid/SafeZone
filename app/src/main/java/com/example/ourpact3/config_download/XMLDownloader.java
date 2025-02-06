package com.example.ourpact3.config_download;

import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

public class XMLDownloader {

    // Method to download XML from a given URL
    // Method to download XML from a given URL
    public InputStreamReader downloadXml(String urlString) throws UnsupportedEncodingException
    {
        HttpURLConnection connection = null;
        StringBuilder result = new StringBuilder();

        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            // Check for a successful response code
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                try (InputStream inputStream = connection.getInputStream();
                     InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                     BufferedReader reader = new BufferedReader(inputStreamReader)) {

                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                }
            } else {
                Log.e("Error", "Response code: " + connection.getResponseCode());
            }
        } catch (Exception e) {
            Log.e("Error", "Exception: " + e.getMessage());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        // Convert the string to an InputStream
        InputStream inputStream = new ByteArrayInputStream(result.toString().getBytes("UTF-8"));
        return new InputStreamReader(inputStream, "UTF-8");
    }
}