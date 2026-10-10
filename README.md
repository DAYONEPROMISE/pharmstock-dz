# PharmStockDZ

Offline-first Android app for Algerian pharmacies: **stock-lot tracking with expiry dates and local near-expiry alerts**. Not a sales/POS tool.

- Kotlin, Jetpack Compose, Material 3, Room, WorkManager
- French + Arabic (RTL), 100% offline, no account
- MIT license, clean-room code

## Features
- Products with supplier notes; stock lots with quantity + expiry date
- Dashboard: expired / critical (≤30d) / warning (≤90d) buckets, thresholds adjustable in Settings
- Daily on-device expiry check (WorkManager + BootReceiver), no network
- French default, Arabic RTL layout

## Build
- `compileSdk 34`, `minSdk 26`, JDK 17, Gradle 8.9
- Local: `.\gradlew.bat :app:assembleDebug` → `app/build/outputs/apk/debug/app-debug.apk`
- CI (`.github/workflows/android.yml`): unit tests + lint + assembleDebug, uploads APK artifact

## Tests
- `ExpiryStatusTest` (10 tests): bucket boundaries, custom thresholds, epoch-day round trip, leap day
- Logic cross-checked with an independent PowerShell port: ALL-LOGIC-CHECKS-PASS
- Full JVM test run pending (Gradle hangs in this sandbox; CI will run it)

## Status
- Local `assembleDebug`: BUILD SUCCESSFUL, APK 19.7 MB (2026-10-10)
- GitHub repo creation via API: 403 (integration token lacks permission) — push + cloud build pending
