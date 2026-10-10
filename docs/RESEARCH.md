# RESEARCH — SolarDZ (verified 2026-10-09)

| Claim | Value | Source |
|---|---|
| GHI Algeria | 4.6–6.6 kWh/m²/day north→Sahara; ~3500h sunshine/yr | Global Solar Atlas via Unitn paper iris.unitn.it (Guermoui) |
| N. Algeria NZE home optimal | 4.218 kWp + 5.578 kWh battery, 0.1204 USD/kWh | Laour et al., journals.pan.pl |
| PV output per kW | Annaba ~4.1 winter / ~7.4 summer kWh/day; Abadla 4.65/8.21; tilt 27–32°S | profilesolar.com/countries/DZ (NASA POWER API) |
| Panel prices DZ 2025 | 400W 25,000 DA; 465W 27,000; 500W 30,000; battery ~50,000 DA/u | algerie360.com 17/02/2025 (expert Ben Jedda) |
| Kit prices 2025 | 1kW 60–120k; 3kW 150–300k; 5kW 300–450k DZD; install +15–25% | prixpanneausolaire.fr 18/02/2025 |
| Tariff/fuel/yield planning seeds | 4.7 DZD/kWh; 46 DZD/l fuel; 50,000 DZD/kW install; 6.5 PSH; PR 0.78 | afrotools.com/tools/solar-roi/algeria (Mar 2026, low/med confidence — used as cross-check only) |
| Policy | 15,000 MW renewables by 2035; ~13.5 GW solar PV by 2030; 3.2 GW from 2025 | Unitn paper §2.1 |

Selected engineering defaults: PSH Littoral 4.8 / Plateaux 5.5 / Sahara 6.4; PR 0.75 (conservative vs 0.78);
DoD 0.5 lead-acid default (LiFePO4 0.9 option in UI roadmap); panel 500W/30k DZD; formula PV=kWh/(PSH×PR).
Unverified / limitations: installer labor varies; no net-metering in app v1; battery LiFePO4 pricing volatile.
