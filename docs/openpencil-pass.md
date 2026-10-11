# OpenPencil pass — solar-dz

## Design (Shared/OpenPencil/solar-dz/)
- `design.html` (PV sizing input + result card, DZ tokens) → `design.fig` via `openpencil import`
- `lint --preset accessibility`: 0 errors, 0 warnings

## Code review (no rewrite needed)
`MainActivity.kt` already meets the 2026 standard: `enableEdgeToEdge`, dynamic color with
static fallback, system-following + in-app dark toggle (`rememberSaveable`), FR/AR toggle,
`WindowSizeClass`-style adaptive (`BoxWithConstraints` 600dp two-pane), 48dp touch targets,
`contentDescription` on icon buttons, `supportingText` errors, `Decimal` keyboards, headings semantics.
No code change — forcing edits would only risk a green build.

## APKs
`android-ci.yml` now also runs `assembleRelease` (unsigned) and uploads `solardz-release-apk`
alongside `solardz-debug-apk`. Both per workflow run in Artifacts.
