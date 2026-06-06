# LiquidBounce NeoForge 1.21.1 Port Seed

This directory is intentionally isolated from the current Fabric build. It is a loader-detection
seed for NeoForge 1.21.1, not a completed LiquidBounce port.

Use it to verify that the NeoForge toolchain, metadata, and a minimal `@Mod` entry class work before
moving the existing Kotlin sources, mixins, access changes, and theme bundling into this build.

From the repository root:

```powershell
.\gradlew -p neoforge-port tasks
.\gradlew -p neoforge-port runClient
```

The build is configured with the Foojay toolchain resolver, so Gradle can download a Java 21 JDK
when the local machine does not already have one.

On this machine, Java 21 was also installed with winget:

```powershell
winget install --id EclipseAdoptium.Temurin.21.JDK -e --accept-source-agreements --accept-package-agreements --silent
```

User-level `JAVA_HOME` is set to:

```text
C:\Program Files\Eclipse Adoptium\jdk-21.0.11.10-hotspot
```

Expected first success criterion: NeoForge detects mod id `liquidbounce` and starts a vanilla client.
After that, migrate the real LiquidBounce sources incrementally.

Verified locally:

```powershell
.\gradlew -p neoforge-port build
```
