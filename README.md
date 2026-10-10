# SolarDZ — dimensionnement solaire hors-ligne (Algérie)

Offline-first Android app (Kotlin, Compose Material 3, Room) for installers:
saisie kWh/j + heures soleil par zone wilaya → recommandation PV/batterie/onduleur + coût DZD,
registre clients, fiches devis + partage PDF, FR/AR RTL, 100% offline, no account.

## Formule
PV(kWc) = kWh/j ÷ (heures_soleil × PR 0.75) • Batterie(kWh) = kWh/j × autonomie ÷ DoD(0.5)
Onduleur = PV × 1.25 • Panneaux = ceil(PV×1000/500)

## Defaults vérifiés (docs/RESEARCH.md)
- Littoral 4.8h • Hauts Plateaux 5.5h • Sahara 6.4h (GHI 4.6–6.6 kWh/m²/j, source Global Solar Atlas)
- Panneau 500W = 30,000 DZD • Batterie ≈ 50,000 DZD • sources algerie360.com 17/02/2025

## Build
CI: `lint + testDebugUnitTest + assembleDebug`, APK artifact `solardz-debug-apk`.

MIT — clean-room code, no third-party code copied.
