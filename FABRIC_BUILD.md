# Fabric 1.21.1 build

This branch is based on the latest `master` commit and is separate from the NeoForge compatibility branches.

## Build

Use Java 21 and run:

```bash
bash ./gradlew build
```

The JAR is written to `build/libs/regions-unexplored-terrain-diffusion-fabric-1.1.0-fabric.1.21.1.jar`.

## Compatibility status

The uploaded Fabric Terrain Diffusion JAR is built for Minecraft **1.21.11**, with Fabric Loader 0.18.1 and a 1.21.11 Minecraft dependency. It is **not compatible with Minecraft 1.21.1** and is not included in this build. This branch targets Fabric 1.21.1 and uses an optional mixin bridge, but it still needs to be tested with a Terrain Diffusion build specifically compiled for Fabric 1.21.1 and the matching Regions Unexplored Fabric release.

The biome names are best-effort candidates copied from the existing NeoForge branch and need validation against the actual Regions Unexplored 1.21.1 registry. A successful compile does not prove the mixin applies or fix the world-generation 0% hang; test a fresh world in-game.
