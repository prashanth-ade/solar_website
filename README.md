# Solar Industries

Solar Industries is a responsive solar-energy customer platform built with Spring Boot 3 / Java 17, MySQL/JPA/Flyway, and a React/Vite frontend.

## Run

`docker compose up --build` starts MySQL and the API. For frontend development:

```powershell
cd frontend
npm install
npm run dev
```

API: `http://localhost:8080/api`  
UI: `http://localhost:5173`

## Frontend routes

Public routes include `/`, `/services`, all ten service-detail slugs under `/services/*`, `/calculator`, `/about`, `/quote`, `/contact`, `/faq`, `/login`, and `/register`.

Authenticated customer routes include `/dashboard`, `/profile`, `/quotes`, `/orders`, `/cart`, `/addresses`, and `/notifications`. `/admin` is restricted to users with the `ADMIN` role.

## API and security

The frontend uses the centralized Axios client and `VITE_API_URL`. Customer data is loaded from the Spring Boot API rather than frontend mock arrays. Protected resources require a JWT bearer token; admin resources require the `ADMIN` role. Actuator health is available at `/actuator/health`, and OpenAPI UI at `/swagger-ui.html`.

The calculator and quote flows return indicative estimates only. Payment provider, email provider, and password-reset credentials should be added through server-side environment variables when those integrations are enabled.

The admin dashboard uses live backend data for dashboard metrics/reports, customers, and quote enquiries when the API is running with an `ADMIN` JWT. The public site loads services and company settings from `GET /api/services` and `GET /api/settings`, falling back to the static catalog when the API is unavailable. During development the Vite dev server proxies `/api` to `http://localhost:8080`, so no CORS setup is needed for local work.

Admin login defaults to API mode (`VITE_ADMIN_AUTH_MODE=api`, see `frontend/.env.example`): configure `APP_ADMIN_EMAIL` and `APP_ADMIN_PASSWORD` for the backend, start both applications, and sign in with those bootstrap credentials. For frontend-only development without a backend, set `VITE_ADMIN_AUTH_MODE=mock` and use the mock credentials `admin@solarindustries.local` / `SolarAdmin2026!`. The API mode verifies that the returned account has the `ADMIN` role before opening the dashboard.

Admin persistence endpoints now include:

- `GET/POST/PUT/DELETE /api/admin/services`
- `GET /api/admin/calculator-requests`
- `GET/PUT /api/admin/settings`

Calculator submissions continue to return the estimate response and are also stored in calculator request history. Flyway migration `V4__admin_persistence.sql` creates and seeds the admin service catalog and company settings. `V5__service_details_and_quote_message.sql` adds public service slugs, long descriptions, and benefits, plus `city` and `message` fields on quotes so enquiry details submitted from the quote form are persisted and shown in the admin enquiries table.

Copy `.env.example` to a local `.env` file and set `DB_URL`, `DB_USER`, `DB_PASSWORD`, `JWT_SECRET`, `JWT_EXPIRATION_MS`, and `CORS_ALLOWED_ORIGINS` before running the app. Do not commit real credentials or secrets. The API adds HSTS, frame-options, and referrer-policy headers in production mode. Errors use a consistent `{timestamp,error,status}` response shape.

### Local admin login

To create or promote one admin account during backend startup, set these values in the local environment before starting the API:

```text
APP_ADMIN_EMAIL=your-admin@example.com
APP_ADMIN_PASSWORD=use-a-long-local-password
```

The password must be at least 12 characters. Bootstrap is disabled when either value is blank, and an existing account's password is never overwritten. Open `http://localhost:5173/login`, sign in with the configured email and password, then open `http://localhost:5173/admin`. Public registration always creates `CUSTOMER` accounts.
# solar_website
