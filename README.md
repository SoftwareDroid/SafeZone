# SafeSearch

SafeSearch is a productivity-boosting application designed to help users manage their time and focus by restricting access to distracting apps and content.
It was created out of the need for a robust solution that can effectively block unwanted applications and screens while ensuring that these restrictions are difficult to bypass once set.

## Features
- **Comprehensive App Blocking**: Search for and block any app or screen on your device.
- **Persistent Locking**: Once locked and installed, the app cannot be easily removed, ensuring that users stay focused.
- **UI Automation**: Utilizes UI Automation to navigate back to previous screens and kill apps, providing a seamless user experience.

Initially developed to block NSFW, e.g. pornographic content, SafeSearch aims to expand its capabilities to include blocking social media, gambling, and other user-defined content in future updates.
Time restrictions rules are available too e.g. block an app after 3min usage for every 2 hours.

## For Developers
Installation and Permissions
To reset permissions, you may need to reinstall the app. The Draw Overlay Permission is required for certain functionalities.

##ä Build release variant
Generate Signed App Bundle/ Apk > select release
password is in Keepassxs

Reinstalling in Debug revokes the accessibility permission (a way to disable the apps functions).
It does not in release version. 

### Add new vector from google
To get Vector icons right click on res folder New>Vector Asset then click on clip art to select from cataloge

## Grant Permissions via ADB
To grant the necessary permissions, use the following command:
adb shell settings put secure enabled_accessibility_services com.example.ourpact3/com.example.ourpact3.ContentFilterService

You can also use the provided script:
app_dev.sh # Needs an icon under Ubuntu for the start menu

## Version Management
To change the app version, update the build.gradle.kts file and then modify the changelog.txt.

## Development Tips
If the emulator runs once, restart Android Studio to avoid potential issues.
Note that killing foreground apps and controlling media playback is not possible without system or root permissions.


Create new activities easily with a right-click to generate boilerplate code.
## For Users
Changing Language is disabled, as this disables the locking features as they search for text on the screen in english.
Using SafeSearch
To use SafeSearch as your search engine in browsers like Firefox, set the search URL to:

safe.duckduckgo.com/?q=%s
Most other search engines are blocked by the safe search rule.

## Functionality
SafeSearch employs UI automation to kill apps, which is the only method available without system or root permissions to prevent access to adult content in browsers.

## FAQ
Q: Safe Search block is blocking my search?

A: Ensure you are using the correct search URL: safe.duckduckgo.com/?q=%s.

## Preventing Uninstallation
To prevent users from uninstalling the app:

First, grant accessibility permission.
Then, enable device admin and exit learn mode.

License:
GPL

## Credits
App Icon: SVG Repo - Lock Icon
https://www.svgrepo.com/svg/530512/lock
