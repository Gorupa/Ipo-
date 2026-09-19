# IPO Ninja

Consumer-first Indian IPO companion app.

## Current MVP
- Beautiful simple home screen
- Open, upcoming and closing-soon IPO sections
- GMP and GMP percentage
- GMP updated time
- Estimated listing price
- Estimated gain per lot
- Subscription snapshot
- IPO detail page
- Important dates
- Watchlist/favorites
- Local demo data so the UI works without a live API

## Architecture

Android app -> Render API -> database/data collectors

The `scraper/` folder is a starter collector architecture. Only collect data from sources you are permitted to access and redistribute. Do not bypass robots.txt, authentication, CAPTCHAs, rate limits or access controls.

## Render backend
Root directory: `backend`
Build command: `npm install`
Start command: `npm start`

The backend currently serves demo data. Replace it with your authorized collector/database when the UI is ready.

## Android
`API_BASE_URL` in `android/app/build.gradle.kts` is set to `https://ipo-o8g1.onrender.com/`. Change it if the Render service URL changes.

App icon: adaptive icon generated from the ninja mark, with a matching navy (`#02132E`) launch background.

UX: dark-navy/teal Material 3 theme matched to the app icon, filter chips (All/Open/Closing soon/Upcoming/Favorites), search, pull-to-refresh, a working back button and favorite toggle (saved locally), loading/error/empty states with retry.

## GitHub Actions
Push the repository to GitHub. Actions builds a debug APK and uploads it as an artifact.

## Product note
GMP is unofficial and can differ between sources. IPO Ninja displays it as an estimate, never as a guaranteed return or recommendation.
