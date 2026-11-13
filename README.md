# Neegy Client — Fabric source project for Minecraft Java 1.21.11

A small client-side utility mod with two controls:

- **G** toggles flight only when Minecraft has already granted flight permission (for example, Creative/Spectator). It does not unlock survival flight on servers.
- **Left Alt** gives a small forward boat boost while held. Multiplayer servers may override client-side movement; this is intended for single-player/private worlds or servers where these mechanics are allowed.

Change the keys under **Options → Controls → Neegy Client**.

## Build
Requirements: JDK 21 and internet access. This is a source project, not a precompiled or tested JAR.

1. Open a terminal in this folder.
2. If Gradle is installed, run `gradle wrapper`.
3. Run `gradlew.bat build` on Windows, or `./gradlew build` on macOS/Linux.
4. The JAR should be generated in `build/libs/` if the selected Fabric toolchain versions resolve and compilation succeeds.

The project has not been compiled in this environment; if Gradle reports dependency or mapping errors, update the Fabric Loom/Loader/API versions for your installation.
