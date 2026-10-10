# DESIGN — SolarDZ screens (implements shared `dz-apps-design-2026.md`)

Single-activity app, one screen with two panes (adaptive):

## Screen 1 — Sizing (only screen v1)
- TopAppBar: title + FR/AR toggle (Language icon) + dark-mode toggle.
- Input pane: kWh/j field (decimal, error slot) → zone dropdown (Littoral 4.8h / Plateaux 5.5h / Sahara 6.4h) → sun-hours caption → autonomy field → full-width Calculer button (48dp).
- Result pane: "Résultat" heading → empty state hint, or ElevatedCard rows (PV kWc + panel count / battery kWh + Ah / inverter kVA / cost DZD, dividers between rows) → demo loads list + total.
- Narrow (<600dp): single scroll column. Wide (≥600dp): input | result side-by-side.
- Dark mode: system + toggle. RTL: full mirror on AR. 100% offline.
