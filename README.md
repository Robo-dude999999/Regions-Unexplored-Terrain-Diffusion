<img width="1920" height="1009" alt="2026-10-10_22-51-12-png" src="https://github.com/user-attachments/assets/29f56f86-8a2d-434b-abf8-5af2d5aa850b" />

# Regions Unexplored: Terrain Diffusion

This NeoForge 1.21.1 mod provides a data-driven compatibility layer between Terrain Diffusion and third-party biome registries.

Terrain Diffusion uses its own biome classifier, so this mod adds an optional custom rule layer that can map the classifier's raw elevation and climate values to registered biome keys. This mod does not bundle biome rules for vanilla biomes; mods that need additional biome mappings should provide data-pack rules or inject into Terrain Diffusion directly.

## Custom biome rules and fallback

Rules are loaded from data-pack resources under:

`data/<namespace>/terrain_diffusion_biomes/<rule_name>.json`

When no valid custom rules are loaded, or no rule matches a pixel, Terrain Diffusion's original biome classification is preserved. This is the vanilla biome result that would otherwise have been selected, rather than forcing the pixel to `minecraft:the_void`. Invalid rule files are skipped with warnings; if no valid rules remain, a non-fatal warning is logged and native classification continues.

## Custom biome rule format

Each rule maps the raw elevation and four climate channels passed to Terrain Diffusion's `BiomeClassifier` to a registered biome. The climate fields use the exact names documented in Terrain Diffusion's classifier:

- `temp`: temperature channel
- `t_season`: temperature seasonality channel
- `precip`: precipitation channel
- `p_cv`: precipitation coefficient-of-variation channel

These fields are read from the per-pixel elevation and climate arrays supplied to `BiomeClassifier`. `elevation` is the decoded elevation output; `temp` is the derived per-pixel temperature after the pipeline's elevation lapse-rate adjustment; `t_season`, `precip`, and `p_cv` are the corresponding climate channels from the model output. The classifier later adds its own noise to temperature and precipitation for its native biome decision, but custom JSON rules compare against the values in these arrays exactly.

Example rule:

```json
{
  "biome_key": "any_biome_mod:any_biome",
  "priority": 100,
  "elevation": { "min": 10, "max": 300 },
  "temp": { "min": 10.0, "max": 30.0 },
  "t_season": { "min": 0.0, "max": 45.0 },
  "precip": { "min": 0.0, "max": 45.0 },
  "p_cv": { "min": -5.0, "max": 20.0 }
}
```

- `priority` is optional and defaults to `0`.
- All five ranges must match at the same pixel. Bounds are inclusive; no additional hidden elevation gate is applied.
- When multiple rules match, higher priority wins. Ties are resolved by alphabetically ascending `biome_key`, then by the data-pack resource ID for deterministic ordering.
- Elevation and climate values are read directly from Terrain Diffusion's classifier input arrays; vanilla multi-noise parameters are not used.
- A custom biome only receives a Terrain Diffusion ID when a valid rule references its key. Missing biome keys are logged and skipped.

## External worldgen injection

When Lithostitched wraps a Terrain Diffusion biome source, its automatic biome injectors and surface-rule additions are skipped for that Terrain Diffusion dimension. Other biome sources and dimensions keep Lithostitched's default behavior. The guard is scoped to Lithostitched; it does not disable unrelated mods' global registry or worldgen behavior.

Terrain Diffusion is a neural-network-based terrain generator that replaces the standard vanilla biome-selection path with its own classifier.

## Build

Uses Java 21 and NeoForge 21.1.255. Run `./gradlew build` (or `gradlew.bat build`) on Windows.
