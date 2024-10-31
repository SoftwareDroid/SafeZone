package com.example.ourpact3;

/*
// Get the SharedPreferences object
SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);

// Create an editor to write data
SharedPreferences.Editor editor = sharedPreferences.edit();

// Save a boolean value
editor.putBoolean("isUserLoggedIn", true); // Change true to false as needed
editor.apply(); // or editor.commit();
 */
public class PreferencesKeys
{
    public static final String MAIN_PREFERENCES = "main";
    public static final String OPTION_USE_WARN_WINDOWS = "wm";
    public static final boolean OPTION_USE_WARN_WINDOWS_DEFAULT = true;
    public static final String OPTION_LOG_BLOCKING = "log_on";
    public static final boolean OPTION_LOG_BLOCKING_DEFAULT = true;
    public static final String PREVENT_DISABLING = "prevent_disabling";
    public static final boolean PREVENT_DISABLING_DEFAULT_VALUE = true;
    public static final String MASTER_KEY_HASH = "master_key";
    public static final String LOCKED_SINCE = "locked_since";
    public static final String MASTER_KEY_DEFAULT_VALUE = "507ce4f8b5eade3904fb1695132fe9cc4adae1d89e7f273c106ce80b8a15ed4a";
}
