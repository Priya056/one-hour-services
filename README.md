# one-hour-services

A 1-hour local services marketplace: customers book verified helpers for
short jobs (errands, repairs, tutoring, etc.) via an Android app; helpers
manage jobs and payouts from the same app; admins approve helpers and
handle disputes from a web dashboard.

## Structure

- `laravel-backend/` — the API (Laravel 13 / PHP 8.4). Deployed on Render
  at `one-hour-services-backend-staging.onrender.com`, SQLite-backed
  (ephemeral — resets on every redeploy, fine for staging).
- `helper-app/` — the Android app (Kotlin, Jetpack Compose), covering
  both the customer and helper roles. Distributed via Firebase App
  Distribution, built against the live staging backend above.
- `admin-dashboard/` — the admin web panel (React + Vite + TypeScript),
  used to approve helper KYC, review disputes, and see platform stats.
  Not yet deployed anywhere — run `npm install && npm run dev` inside it
  against the same backend.
- `database-design/` — a reference DDL schema and migration listing for
  documentation purposes. The real schema lives in
  `laravel-backend/database/migrations`.

## Local setup

Each subdirectory is a self-contained project — see the commands each
tool expects (`composer install` / `php artisan serve` for the backend,
Gradle for the Android app, `npm install` / `npm run dev` for the admin
dashboard).
