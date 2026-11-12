# Invis Health Indicators (Fabric, Minecraft 1.21.11)

Shows a row of hearts above players and mobs — **including players who are invisible
from a potion** (they get a purple `[Invis]` tag). Client-side only.

- Red hearts = health (dark red = half heart), gold hearts = absorption, plus `current/max` HP
- Mobs with more than 20 HP are scaled down to a 10-heart row
- Range: 48 blocks. Your own hearts only show in third person.
- **H** toggles everything, **J** toggles mob hearts (players are always shown).
  Rebind under Options → Controls → Key Binds.

## Build
Needs Java 25 (JDK) to build (the mod itself runs on Java 21+). In this folder run:

- Windows: `gradlew.bat build`
- Mac/Linux: `./gradlew build`

The mod is `build/libs/invis-health-indicators-1.0.0.jar` (not the `-sources` one).

No Java? Push this folder to a GitHub repo — the included Actions workflow builds it
and attaches the jar under the run's **Artifacts**.

## Install
Fabric Loader for 1.21.11 + Fabric API, then drop the jar in `.minecraft/mods`.
