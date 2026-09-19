# IPO Ninja

A simple consumer-facing Indian IPO companion.

## Android MVP
This build intentionally has **zero network/data-provider dependencies** in the Android app so the APK can build reliably while the UI is finalized.

It includes:
- IPO home
- Open/upcoming cards
- GMP
- GMP percentage
- Estimated listing
- Estimated gain per lot
- Subscription snapshot
- IPO details
- Important dates

The Android MVP uses demo data. The Render backend is separate and ready for the next data-engine phase.

## GitHub Actions
Workflow is manual-only:
GitHub -> Actions -> IPO Ninja Android Build -> Run workflow.

## Render
The `render.yaml` deploys the Node backend from `backend`:
- Build: `npm install`
- Start: `npm start`
- Health: `/health`

## Next phase
Connect the Render API/data collector after the UI build is confirmed.
