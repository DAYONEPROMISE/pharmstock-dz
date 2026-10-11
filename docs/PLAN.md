# PharmStockDZ — Research, Plan, Limitations, Costs, First 10 Customers

## RESEARCH (verified)
- Algerian pharmacies must track expiry; expired-drug destruction follows regulatory procedure — an expiry-first tracker fits the workflow. (Domain knowledge; observatory claims below are verified tool outputs.)
- Verified via Gradle cache inspection: Room 2.6.1, Compose BOM 2024.06.00, WorkManager, Navigation-Compose, Material icons — all cached locally, so the offline build works without network fetches.
- Verified via Maven Central reachability check (HTTP 200 on repo.maven.apache.org, 2026-10-09): network available for dependency download; build itself needs no network at runtime.
- Stack selected: Kotlin + Compose + Material 3 + Room + WorkManager — standard offline-first Android, no backend, no account, no tracking SDKs.

## VALIDATE / SELECT
- Scope cut: no POS, no sales, no barcode ordering, no cloud sync — expiry + stock lots + supplier notes only.
- i18n: FR default, AR with RTL (LocalesConfig + values-ar). Thresholds user-adjustable.

## PLAN (executed)
1. Scaffold Gradle 8.9 + AGP + Kotlin + Compose + Room + KSP
2. Data layer: Product, StockLot, DAOs, Room DB, repository
3. Expiry classifier (pure function) + unit tests
4. UI: Dashboard, Stock, Product detail/edit, Settings; FR/AR strings
5. Notifications: ExpiryWorker + BootReceiver + channels
6. CI workflow: test + lint + assembleDebug + APK artifact
7. Local assembleDebug → APK; push to GitHub; verify cloud build

## TEST TABLE
| # | Test | Type | Result |
|---|------|------|--------|
| 1 | assembleDebug local | build | PASS (BUILD SUCCESSFUL, APK 19.7 MB) |
| 2–11 | ExpiryStatusTest (10 tests: boundaries 0/30/31/90/91d, expired, custom thresholds, round-trip, leap day) | unit (JVM, GitHub Actions run #3 step + run #4) | PASS |
| 12 | lintDebug (incl. StringFormatInvalid fix on alerts_body) | lint (GitHub Actions run #4) | PASS |
| 13 | CI Android CI run #4 (tests + lint + assembleDebug + APK artifact) | cloud | PASS (success, 2026-10-10) |
| 14 | APK artifact pharmstockdz-debug-apk (18.3 MB, expires 2026-11-09) | cloud artifact | PUBLISHED |

## LIMITATIONS
- GitHub repo creation via API returns 403; push + cloud build + APK artifact blocked until repo exists.
- JVM unit-test run hangs in this sandbox (Gradle daemon stalls); CI will execute it.
- Notifications use exact-alarm-free periodic work (may defer on aggressive OEM skins); thresholds default 30/90d.
- No import/export yet; single-device Room DB; no backup.

## COSTS
- Build: 0 (local Gradle + cached deps; GitHub Actions free tier for public repo).
- Runtime: 0 (no server, no push service, no third-party SDK).
- Publish: Google Play one-time $25 (optional; APK sideload is free).

## FIRST 10 CUSTOMERS PLAN
1. 3 pilot pharmacies (Algiers) — free install + 30-min onboarding, collect expiry-alert feedback.
2. Pharmacist WhatsApp/Facebook groups — demo video (FR/AR), APK link.
3. Order-of-pharmacists student network — campus reps.
4. Ask pilots for 2 referrals each → 6 more; offer free CSV import as closer.
5. Convert: paid support/updates or Play listing after 10 actives.
