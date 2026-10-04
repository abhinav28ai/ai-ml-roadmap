# AI ML Roadmap — Native Android Widget

A native Android companion app + Home Screen widget for the 130-day AI/ML roadmap and NeetCode 150.

## What the widget does
- Shows the current roadmap day and target.
- Shows the 2 DSA questions for the day with NC + LeetCode numbers.
- Shows circular AI/ML completion.
- `‹` = previous day.
- `✓ DONE` = complete current day and advance.
- `›` = next day.
- Tapping the target opens the full app.

The app also has:
- Previous/next navigation.
- Done / Move Ahead / Reschedule.
- AI/ML project and revision.
- Section details, certification/proof and research reading.
- 130-day progress.
- 150-problem DSA count.
- Link to the full GitHub Pages roadmap.

## No Android Studio required
The included GitHub Actions workflow builds a debug APK in GitHub.

From the repository root:
1. Push this project to GitHub.
2. Actions → `Build AI ML Roadmap Android Widget` → Run workflow, or push changes under `app/`.
3. Download the `ai-ml-roadmap-debug-apk` artifact.
4. Install the APK on the Pixel 9.
5. Long-press the Pixel Home Screen → Widgets → AI ML Roadmap.

## Build configuration
- Android Gradle Plugin 9.0.1
- Gradle 9.1.0
- compileSdk 36 / targetSdk 36
- minSdk 26
- Java 17

Android's current widget model uses an AppWidgetProvider + widget metadata + a RemoteViews layout; interactive buttons are delivered through widget broadcasts. The project deliberately uses the platform View/RemoteViews API to avoid extra third-party dependencies.
