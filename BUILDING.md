# Build targets

This project now has two build targets. Each target produces a distinct JAR because Minecraft 1.21.1 and 1.21.11 use different NeoForge lines.

## Minecraft 1.21.1

```bash
./gradlew -PmcTarget=1.21.1 build
```

The output is written to `build/libs/regions-unexplored-terrain-diffusion-mc1.21.1-1.0.2.jar`.

This target declares Minecraft 1.21.1, NeoForge 21.1.255+, Regions Unexplored 0.6.2+, Terrain Diffusion 2.3.x, and Lithostitched 1.7.9+.

## Minecraft 1.21.11

```bash
./gradlew -PmcTarget=1.21.11 build
```

The output is written to `build/libs/regions-unexplored-terrain-diffusion-mc1.21.11-1.0.2.jar`. If you want another NeoForge 21.11 build, override it with `-PneoForgeVersion=VERSION`.

**Known compatibility limitation:** as of this change, Regions Unexplored's published NeoForge files list Minecraft 1.21.1, but no official 1.21.11 build. Therefore the 1.21.11 artifact marks Regions Unexplored as optional and can be used with Terrain Diffusion and Lithostitched, but Regions Unexplored biome remapping cannot activate unless a compatible RU port is installed. This is not a claim that all three mods are currently supported together on 1.21.11.

## Validation

Build both targets with Java 21. A successful Gradle build verifies compilation only; test world creation and generation in a fresh instance for each target before distributing either JAR. In particular, the separate world-generation hang at 0% still needs an in-game reproduction test.
