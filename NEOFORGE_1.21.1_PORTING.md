# NeoForge 1.21.1 Porting Notes

Target:

- Minecraft: `1.21.1`
- NeoForge line: `21.1.x`
- Last checked NeoForge artifact: `net.neoforged:neoforge:21.1.233`
- Java: `21`
- Kotlin: keep project baseline at `2.0.0` unless NeoForge/Kotlin runtime forces a change

This is not a one-file conversion. LiquidBounce currently depends on Fabric Loom, Fabric metadata,
Yarn named mappings, Fabric API entry points, Fabric access wideners, and a large set of Minecraft
client mixins.

## Current Fabric Anchors

- Build toolchain: `build.gradle`
  - Uses `fabric-loom`
  - Uses Yarn mappings via `net.fabricmc:yarn`
  - Depends on Fabric Loader, Fabric API, and Fabric Language Kotlin
- Mod metadata: `src/main/resources/fabric.mod.json`
  - Declares Fabric loader metadata
  - Declares `liquidbounce.mixins.json`
  - Declares `liquidbounce.accesswidener`
- Runtime bootstrap:
  - `src/main/java/net/ccbluex/liquidbounce/injection/mixins/minecraft/client/MixinMinecraftClient.java`
  - Injects `ClientStartEvent` and `ClientShutdownEvent`
  - `src/main/kotlin/net/ccbluex/liquidbounce/LiquidBounce.kt` handles real client startup/shutdown
- Theme build:
  - `src-theme` is Svelte/Vite
  - Gradle builds it into `src-theme/resources/assets/liquidbounce/default_theme.zip`
  - This part can mostly be reused

## Known Fabric-Specific Code

Direct Fabric imports found in Kotlin:

- `features/itemgroup/ClientItemGroup.kt`
  - `net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup`
- `features/misc/HideAppearance.kt`
  - `net.fabricmc.loader.impl.FabricLoaderImpl`
- `utils/client/SignTranslationFix.kt`
  - `net.fabricmc.fabric.api.resource.ModResourcePack`
- `utils/inventory/InventoryUtils.kt`
  - `net.fabricmc.loader.api.FabricLoader`
- `utils/mappings/Remapper.kt`
  - `net.fabricmc.mappings.*`

These are the first compile blockers after switching away from Fabric.

## Migration Strategy

1. Create a separate NeoForge build path first.
   Do not overwrite the current Fabric build until the NeoForge client can compile and launch.

2. Replace metadata.
   Add `src/main/resources/META-INF/neoforge.mods.toml` for NeoForge and keep
   `fabric.mod.json` only for the Fabric build path.

3. Add a NeoForge mod entry class.
   The existing startup logic can stay in `LiquidBounce.kt`, but NeoForge still needs an `@Mod`
   entry class to make the loader recognize the mod. Avoid double-starting the client if the
   existing `MixinMinecraftClient` bootstrap remains active.

4. Move from Fabric Loom/Yarn to a NeoForge-compatible mapping setup.
   Most source references are currently Yarn names. Expect many Minecraft class, method, and field
   name changes when moving to Mojang/Parchment mappings.

5. Convert `liquidbounce.accesswidener`.
   NeoForge does not consume Fabric access wideners directly. Convert entries to access transformers
   where possible, or replace specific entries with mixin accessors/invokers.

6. Port or replace Fabric APIs.
   Use NeoForge equivalents for item groups, loader queries, resource pack access, and lifecycle hooks.

7. Validate mixins one group at a time.
   The project has many Minecraft client mixins. The mixin framework exists in NeoForge, but target
   names and descriptors must match the NeoForge 1.21.1 mapped environment.

8. Reuse the theme pipeline.
   The `src-theme` Vite build can continue producing `default_theme.zip`; only the Gradle wiring
   needs to be copied into the NeoForge build.

## First Practical Milestone

The first useful milestone is a NeoForge 1.21.1 build that:

- Is detected by a NeoForge 1.21.1 client as mod id `liquidbounce`
- Loads mixin configuration without crashing before Minecraft reaches the title screen
- Starts the Netty local server and theme manager
- Opens the title screen without enabling gameplay modules

Do not try to fix all combat, movement, render, and exploit modules in the first pass. Get the shell
loading first, then port module categories incrementally.

## Suggested Version Constants

```properties
minecraft_version=1.21.1
neo_version=21.1.233
mod_version=0.11.0-neoforge
archives_base_name=liquidbounce-neoforge
kotlin_version=2.0.0
```

