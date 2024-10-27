package com.example.ourpact3.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CurrentTimestamp
{
    public static String getCurrentTimestamp()
    {
        Date currentDate = new Date();

        // Create a SimpleDateFormat instance with the desired format
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault());

        // Format the current date
        return dateFormat.format(currentDate);
    }
}
