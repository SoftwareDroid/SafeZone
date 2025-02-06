package com.example.ourpact3.service;

import android.util.Log;

import com.example.ourpact3.db.WordEntity;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStreamReader;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class XMLDownloader {

    // Method to download XML from a given URL
    public InputStreamReader downloadXml(String urlString) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            // Check for a successful response code
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = connection.getInputStream(); // Get the InputStream
                return new InputStreamReader(inputStream); // Return the InputStreamReader for parsing
            } else {
                Log.e("Error", "Response code: " + connection.getResponseCode());
                return null;
            }
        } catch (Exception e) {
            Log.e("Error", "Exception: " + e.getMessage());
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}