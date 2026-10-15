# Fabric 1.21.1 build

This branch is based on the latest `master` commit and is separate from the NeoForge compatibility branches.

## Build

Use Java 21 and run:

```bash
bash ./gradlew build
```

The JAR is written to `build/libs/regions-unexplored-terrain-diffusion-fabric-1.1.0-fabric.1.21.1.jar`.

## Compatibility status

The supplied Fabric Terrain Diffusion JAR declares Minecraft `1.21.1` and requires Fabric Loader `0.18.1` or newer. Its bytecode contains `com.github.xandergos.terraindiffusionmc.world.TerrainDiffusionBiomeSource`, the private `requireBiomeIdMap` method, and the `biomeLookup` / `biomeIdMap` fields targeted by this branch's optional mixin. The branch metadata now requires Fabric Loader `0.18.1` or newer as well.

The bridge uses reflection so Terrain Diffusion is not a compile-time dependency and the compatibility mod can load without it. The Regions Unexplored biome names in the mapping candidates are still best-effort and have not been verified against the exact Fabric 1.21.1 RU registry. A successful compile does not prove the mixin applies, that the mappings are appropriate, or that the original world-generation 0% hang is fixed. Test with matching Fabric 1.21.1 builds of both mods in a fresh world and inspect the log for the bridge message.
