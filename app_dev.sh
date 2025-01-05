#!/bin/bash
echo "hallo welt"

#adb shell dpm set-device-owner com.example.ourpact3/.MyDeviceAdminReceiver
adb shell appops get com.example.ourpact3
adb shell appops set com.example.ourpact3 SYSTEM_ALERT_WINDOW allow
adb shell pm grant com.example.ourpact3 android.permission.SYSTEM_ALERT_WINDOW
adb shell pm grant com.softwaredroid.dictationmaster android.permission.SYSTEM_ALERT_WINDOW
#adb shell dumpsys com.example.ourpact3
adb shell settings put secure android_permission_system_alert_window 1
adb shell settings put secure enabled_accessibility_services com.example.ourpact3/com.example.ourpact3.ContentFilterService
sleep 1
adb shell settings put secure enabled_accessibility_services com.softwaredroid.dictationmaster/com.softwaredroid.dictationmaster.DictationService
# Start other app for testing
#adb shell am start -n au.com.shiftyjelly.pocketcasts/.ui.MainActivity

# Start firefox
#adb shell am start -n org.mozilla.firefox/.App
#sleep 2
# touch on the screen to trigger filter
#adb shell input touchscreen tap 300 200

# Go to home screen
#adb shell input keyevent 3
sleep 5
