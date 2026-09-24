# AGENTS.md - SolarFlow Frontend

## Commands

- **Dev server**: `npm run dev` (vite, serves at http://localhost:5173)
- **Build**: `npm run build` (vite build, outputs to `dist/`)
- **Tests**: `npm test` (vitest run)
- **Preview**: `npm run preview` (preview production build)
- **Lint/typecheck**: No separate lint script. Use `npm run build` to verify CSS/JS compiles, and `npm test` for unit tests.

## Project Structure

- `frontend/src/main.jsx` - All React components (single-file)
- `frontend/src/style.css` - All styles (single-file)
- `frontend/src/data.js` - Static fallback data (service catalog, FAQ)
- `frontend/src/services/` - API integration (api.js, catalog.js, admin.js, adminAuth.js)

## Breakpoints (mobile-first target)

- Mobile: >=360px (320px min)
- Tablet: 768px - 1023px
- Desktop: 1024px+

## Notes

- CSS is hand-written (no Tailwind/Bootstrap) in a single `style.css` file.
- Desktop styles are in base rules; mobile overrides use `@media(max-width:767px)` and `@media(max-width:480px)`.
- The dev server proxies `/api` to `http://localhost:8080`.
- Admin pages have their own responsive rules at `@media(max-width:900px)` and `@media(max-width:520px)`.
