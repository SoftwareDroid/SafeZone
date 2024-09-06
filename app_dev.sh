#!/bin/bash
echo "hallo welt"
adb shell dumpsys com.example.ourpact3
adb shell settings put secure enabled_accessibility_services com.example.ourpact3/com.example.ourpact3.ContentFilterService
sleep 1
# Start other app for testing
#adb shell am start -n au.com.shiftyjelly.pocketcasts/.ui.MainActivity
# Go to home screen
#adb shell input keyevent 3
sleep 5
