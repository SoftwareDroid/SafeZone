# SafeSearch

SafeSearch is a productivity-boosting application designed to help users manage their time and focus by restricting access to distracting apps and content.
It was created out of the need for a robust solution that can effectively block unwanted applications and screens, including those with NSFW content, while ensuring that these restrictions are difficult to bypass once set, and also addressing gaps in traditional filtering methods, such as content scoring, multi-language support, and compatibility beyond browser-based applications, with customizable exception screens for settings and other essential areas.

## Content Blocking Features
- **Advanced Content Blocking**: Unlike other apps that only filter blacklisted keywords, SafeSearch offers a robust content blocking system that supports regular expressions (regex) and multi-language support.
- **Scoring**: To prevent false negatives, SafeSearch scores found terms, allowing them to appear a specified number of times before blocking.
- **All app support**: SafeSearch works seamlessly across all apps on all screens, including YouTube, and not just limited to browsers.
- **Customizable Exceptions**: Allow specific screens, such as search history or settings, to be exempt from blocking.
- **Rule Management**: Easily expand, export, and import content block rules to suit your needs.
- **Anti Cheat Mode**: Once locked and installed, the app cannot be easily removed, ensuring that users stay focused.
- **UI Automation**: Employs UI Automation to automatically close unwanted apps, particularly those playing NSFW audio or video content that may bypass traditional filtering methods.

## Other Features
- **Usage Restrictions**:  limits apps in time and usage, e.g. block an app after 3min usage for every 2 hours.

Initially developed to block NSFW, e.g. pornographic content, SafeSearch aims to expand its capabilities to include blocking social media, gambling, and other user-defined content in future updates.

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

The apps active prevent seeing USB Debugging because it can be use to diasable the service
If there is ap problem with the layout perhaps the text parameter contains new lines.
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
