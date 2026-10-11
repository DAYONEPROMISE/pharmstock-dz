# OpenPencil pass — pharmstock-dz

## Design (Shared/OpenPencil/pharmstock-dz/)
- `design.html` (expiry alert + stock card + 3-tab nav, DZ tokens) → `design.fig`
- `lint --preset accessibility`: 0 errors, 0 warnings

## Code changes
- `ui/Nav.kt`: 3 bottom-nav icons (Home/List/Settings) used `contentDescription = null`;
  now use the matching string resources (`nav_dashboard`/`nav_stock`/`nav_settings`).
  Other screens already described.

## APKs
`android.yml` adds `assembleRelease` (unsigned) + release artifact + versioned
`v*-b*` GitHub Release with notes + both APKs.
