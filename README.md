# IPO Pulse
Simple Indian IPO tracker: GMP, update time, estimated gain/lot, subscription, dates and IPO details.

## Render
Create a Render Web Service with root directory `backend`, build `npm install`, start `npm start`.
The backend contains DEMO data. Replace it with licensed/authorized live data before production.

## Android
Set `API_BASE_URL` in `android/app/build.gradle.kts` to your Render URL.
Push to GitHub; `.github/workflows/android.yml` builds a debug APK artifact.
