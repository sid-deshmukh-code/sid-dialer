# Sid Dialer

A simple native Android phone dialer app — dialpad, call log (recents), and contacts — written in Kotlin.

Architecture and permission model are based on the open-source **[Fossify Phone](https://github.com/FossifyOrg/Phone)** project (AGPL-3.0), simplified into a single-module app for easy building and learning.

## Features
- Dialpad with tap-to-call
- Call history (recents) read from `CallLog`
- Contacts list read from `ContactsContract`, tap to call
- Falls back to the system dialer (`ACTION_DIAL`) if call permission isn't granted

## Getting an APK — 3 ways

### 1. GitHub Actions (easiest, no local setup)
1. Push this folder to a new GitHub repo.
2. Actions will run automatically (workflow at `.github/workflows/build-apk.yml`), or trigger it manually from the **Actions** tab → "Build Debug APK" → **Run workflow**.
3. Once it finishes, open the run and download the `SidDialer-debug-apk` artifact — that's your installable `.apk`.

### 2. Android Studio
1. Open this folder in Android Studio (Koala or newer).
2. Let it sync Gradle (downloads the wrapper automatically).
3. `Build → Build Bundle(s) / APK(s) → Build APK(s)`.
4. Find it in `app/build/outputs/apk/debug/app-debug.apk`.

### 3. Command line (if you have the Android SDK + Gradle installed)
```bash
gradle assembleDebug
# APK at app/build/outputs/apk/debug/app-debug.apk
```

## Installing
Enable "Install unknown apps" for your file manager/browser on your Android phone, then open the APK. It'll ask for Phone, Contacts, and Call Log permissions on first launch — grant them for full functionality.

## Notes
- This is a **debug build** (unsigned with a debug key) — fine for personal installs, not for the Play Store.
- To become the **default system dialer** (answer/reject calls, in-call UI), you'd additionally need to implement `InCallService` and `ConnectionService` — a much bigger scope. This app is a fully working "make calls / view history / view contacts" dialer, which covers what most people mean by "phone dialer app."
