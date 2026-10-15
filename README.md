# Regions Unexplored: Terrain Diffusion compatibility

This repository contains a Fabric 1.21.1 compatibility bridge for Regions Unexplored and Terrain Diffusion.

## Fabric 1.21.1

See [FABRIC_BUILD.md](FABRIC_BUILD.md) for build instructions and the current testing status. Build with Java 21 using `bash ./gradlew build`.

The Fabric implementation is maintained on `compat/fabric-1.21.1`. The existing NeoForge branches are intentionally left unchanged.

The supplied Fabric Terrain Diffusion JAR identifies Minecraft `1.21.1` as its target, and its `TerrainDiffusionBiomeSource` contains the `requireBiomeIdMap` method and `biomeLookup` / `biomeIdMap` fields expected by the optional bridge. This verifies the target symbols exist in the supplied artifact; it does not yet prove the mixin applies in-game or that the candidate biome mappings are correct.
