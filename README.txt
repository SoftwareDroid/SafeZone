# For devs
# grant permission
adb shell settings put secure enabled_accessibility_services com.example.ourpact3/com.example.ourpact3.ContentFilerService

use: app_dev.sh # needs icon under ubuntu for start menu

TODO
1. XML einlesen und verwenden
2. App Close button schließt die APP
3b. DeviceADmin + Abschalt Prevention einbauen

# Filter
AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED
action.getId() == AccessibilityNodeInfo.AccessibilityAction.ACTION_IME_ENTER.getId()
PocketCast search und URL search abfangen

TODO: Duckduckgo click
Ich weiß nicht wie das abfangen soll
