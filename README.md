# Trail Sense

> Use your Android phone's sensors to assist with wilderness treks or survival situations. Designed for entirely offline use.

[![](https://github.com/kylecorry31/Trail-Sense/workflows/Android%20CI/badge.svg)](https://github.com/kylecorry31/Trail-Sense/actions/workflows/android.yml)
[![](https://hosted.weblate.org/widgets/trail-sense/-/trail-sense-android/svg-badge.svg)](https://hosted.weblate.org/projects/trail-sense/trail-sense-android)
[![Nightly](https://github.com/kylecorry31/Trail-Sense/actions/workflows/nightly.yml/badge.svg)](https://github.com/kylecorry31/Trail-Sense/actions/workflows/nightly.yml)

Trail Sense is a tool, and just like any other tool that you bring into the wilderness, it's essential to have backup equipment and skills.

As featured in the [#WeArePlay](http://g.co/play/weareplay-usa) campaign!

See the [Technical Blog / Research](https://kylecorry.com/research/categories/trail-sense/)

<table>
    <tr>
        <th>F-Droid</th>
        <th>Google Play</th>
    </tr>
    <tr>
        <td>
            <a href="https://f-droid.org/en/packages/com.kylecorry.trail_sense">
                <img alt="Get it on F-Droid" src="https://fdroid.gitlab.io/artwork/badge/get-it-on.png" height="60" align="middle">
            </a>
        </td>
        <td>
            <a href="https://play.google.com/store/apps/details?id=com.kylecorry.trail_sense">
                <img alt="Get it on Google Play" src="https://play.google.com/intl/en_us/badges/static/images/badges/en_badge_web_generic.png" height="60" align="middle">
            </a>
        </td>
    </tr>
</table>

<img src="fastlane/metadata/android/en-US/images/featureGraphic.png">

## Table of Contents

- [Feature Roadmap](#feature-roadmap)
- [Goals](#goals)
- [Features](#features)
- [Privacy](#privacy)
- [Contributing](#contributing)
- [FAQ](#faq)
- [Support](#support)
- [Open Source Credits](#open-source-credits)
- [License](#license)

## Feature Roadmap
- [x] Q2 2023: Photo Maps
- [ ] Q3-Q4 2023: Usability (at a glance, tutorials, data export)
- [ ] Q1-Q2 2024: Augmented reality
- [ ] Q3 2024: Path navigation

## Goals
- Trail Sense must not use the Internet in any way, as I want the entire app usable when there is no Internet connection
- Features must provide some benefits to people using the app while hiking, in a survival situation, etc.
- Features should make use of the sensors on a phone rather than relying on stored information such as guides
- Features must be based on peer-reviewed science or be verified against real world data
- [Use Cases](https://github.com/kylecorry31/Trail-Sense/wiki/Use-Cases)

## Features

- Navigation
- Weather
- Astronomy
- Tides
- Clinometer
- And many more tools

See the need for a new feature? [Post it here!](https://github.com/kylecorry31/Trail-Sense/issues/59)

### Navigation

Trail Sense's compass helps you determine the direction of North and navigate to saved locations, known as beacons. In addition to navigating to beacons, you can also use the Backtrack feature to record waypoints and retrace your steps. This tool makes it easy to navigate and find your way in the great outdoors.

Example beacons: home, work, trailhead, campsite

<img src="fastlane/metadata/android/en-US/images/phoneScreenshots/1.png" alt="Navigation Screenshot" height="500"/>

### Weather

Trail Sense helps you stay informed about the weather, while completely offline. You can use it to predict if what the weather will be or be notified if a storm is likely to occur. It also displays the historical daily temperatures for your location, so you can be better prepared. This feature is only available on phones that have a barometer.

<img src="fastlane/metadata/android/en-US/images/phoneScreenshots/5.png" alt="Weather Screenshot" height="500"/>

### Astronomy

View reliable and accurate sunrise and sunset times for your location. In addition, you can stay informed about the phases of the moon, lunar eclipses, and meteor showers. These tools can help you plan your outdoor activities and ensure you have the necessary light for your endeavors.

<img src="fastlane/metadata/android/en-US/images/phoneScreenshots/4.png" alt="Astronomy Screenshot" height="500"/>

# Privacy

Location information gathered by this application does not leave your device (as a matter of fact, this app doesn't use the Internet at all). The altitude and pressure history for the last 48 hours is stored in local app storage - this is used to determine weather forecasts. The last known location is also stored in app preferences to allow faster load times and support app functionality when the GPS can not be reached. The beacons and paths store their location information in a local SQLite database. All of this information is cleared when you clear the app storage or delete it.

## Permissions
### Sensitive
- **POST_NOTIFICATIONS**
  - Allows Trail Sense to display notifications (backtrack, weather, sunset alerts, astronomy events, water boil timer, etc)
  - **When denied**: Alerts will not be displayed and some services may not function properly depending on your device manufacturer.
- **ACCESS_FINE_LOCATION / ACCESS_COARSE_LOCATION**
  - Allows Trail Sense to retrieve your location for navigation, weather (sea level calibration), and astronomy. 
  - **When denied**: You will have the ability to mock your location under Settings > Sensors > GPS. On Android 14+, backtrack and weather will be unavailable due to Android OS constraints.
- **ACCESS_BACKGROUND_LOCATION**
  - Allows Trail Sense to retrieve your location for sunset alerts while in the background. On some devices, this will also improve the reliability of backtrack and weather monitor (though shouldn't be needed on most devices).
  - **When denied**: If you travel and do not open Trail Sense, but have Sunset Alerts enabled, the times will likely be inaccurate.
- **ACTIVITY_RECOGNITION**
  - Allows Trail Sense to use your phone's pedometer for distance calculation.
  - **When denied**: The pedometer will not work.
- **CAMERA**
  - Allows Trail Sense to use your camera on the sighting compass, clinometer, and for taking photos used by the Cloud Scanner, QR Code Scanner, and Photo Maps.
  - **When denied**: You will not be able to use the sighting compass, camera clinometer, or QR Code Scanner. You will need to pick an existing photo to use for the Cloud Scanner or Photo Maps.
- **SCHEDULE_EXACT_ALARM**
  - Allows Trail Sense to post a notification at an exact time. This is used by the Clock tool (when updating system time) and Sunset Alerts.
  - **When denied**: The clock and sunset alerts may not be accurate (can be off by several minutes).
 
### Not sensitive (always granted)
- **RECEIVE_BOOT_COMPLETED**
  - Allows Trail Sense to restart when you reboot your device. This will re-enable backtrack, weather monitor, and several other background services.
- **FOREGROUND_SERVICE**
  - Allows Trail Sense to start foreground services, such as backtrack and weather monitor.
- **FLASHLIGHT**
  - Allows Trail Sense to control the phone's flashlight.
- **VIBRATE**
  - Allows Trail Sense to vibrate the phone. Used for haptic feedback on dials and on the metal detector tool.
- **WAKE_LOCK**
  - Allows Trail Sense to reliably run services such as backtrack and weather monitor, especially when the frequency is under 15 minutes.


# Debug features
Only available on debug APKs / builds via Android Studio
- Weather tool's barometer chart shows unsmoothed readings in background
- Weather history, elevation history, path elevations, and latest cloud scan are logged to the files/debug folder in Trail Sense data as CSV files
- Weather settings shows statistics timing (for weather monitor service)
- Paths show statistics about timing (for backtrack service)

# Contributing

- [Request a new feature](https://github.com/kylecorry31/Trail-Sense/issues/59)
- [Submit an issue](https://github.com/kylecorry31/Trail-Sense/issues)
- [Translate Trail Sense on Weblate](https://hosted.weblate.org/projects/trail-sense/trail-sense-android)
- [Test out new features](https://github.com/kylecorry31/Trail-Sense/issues/74)

If you choose to write a new feature yourself, send me a message to verify that it is something that I will accept into Trail Sense before your write it (if not, you can always fork this repo and create your own version of Trail Sense!). I will conduct a code review on incoming pull requests to verify they align nicely with the rest of the code base and the feature works as intended.

Issues marked with the help-wanted label are open for community contribution at any time (just submit a PR to main and I will review it), or leave a comment on the story to say you are working on it / ask for more details. Please leave a comment on any other issue before you work on them because they might not have all the details, I may not want it implemented yet, or I may have to implement it myself - for fun.

If an issue has a milestone and you would like to work on it, please leave a comment before working on it or creating a pull request. If you do not have the feature completed within 4 days of when I plan to release, I will implement it.

If you submit an issue, please be civil and constructive - I will consider all feedback, and if I choose not to implement your suggestion I will post my reasoning. If you are experiencing an issue, please include all relevant details to help me understand and reproduce the issue. If you disagree with a change, please describe why you disagree and how I can improve it (if applicable, please provide research / evidence so I can cross verify).

# FAQ
The FAQ has moved [to the wiki](https://github.com/kylecorry31/Trail-Sense/wiki/Frequently-Asked-Questions-(FAQ))

# Support

The best way to support Trail Sense is to send me your feedback, share how you are using it, test nightly builds, or post your ideas for new features.

# Open Source Credits

- Thank you to everyone who tried out this app and opened issues, suggested features, provided translations, or tested debug builds for me
- Thanks to @qwerty287 and @Portagoras for implementing several features and bugfixes
- Please see the in app licenses for all open source licenses
- Contributors and translators: https://github.com/kylecorry31/Trail-Sense/graphs/contributors

# License

[![License](https://img.shields.io/:license-mit-blue.svg?style=flat-square)](https://badges.mit-license.org)

- **[MIT license](LICENSE)**
