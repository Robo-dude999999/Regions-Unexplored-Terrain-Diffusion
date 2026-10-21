package net.mcreator.regunexterdiff.worldgen;

import net.minecraft.resources.ResourceLocation;

/**
 * Extra lowland gating for biome keys that clearly describe wetlands or
 * riverbanks. The configured elevation range is always respected; the
 * additional ceiling prevents broad ranges from placing lowland water biomes
 * on high terrain.
 */
public final class HydraulicIsolation {
    private static final float MAX_LOWLAND_HYDRAULIC_ELEVATION_METERS = 300.0F;

    private HydraulicIsolation() {
    }

    public static boolean allows(
            ResourceLocation biomeKey,
            float elevationMeters,
            CustomBiomeRule.NumericRange configuredElevation
    ) {
        if (biomeKey == null || !Float.isFinite(elevationMeters)
                || !configuredElevation.contains(elevationMeters)) {
            return false;
        }

        String path = biomeKey.getPath().toLowerCase(java.util.Locale.ROOT);
        if (!isLowlandHydraulicBiome(path)) {
            return true;
        }

        return elevationMeters <= Math.min(
                configuredElevation.max(),
                MAX_LOWLAND_HYDRAULIC_ELEVATION_METERS
        );
    }

    private static boolean isLowlandHydraulicBiome(String path) {
        return path.contains("riverbank")
                || path.contains("river_bank")
                || path.contains("wetland")
                || path.contains("swamp")
                || path.contains("marsh")
                || path.contains("bog")
                || path.contains("fen")
                || path.contains("bayou")
                || path.contains("mangrove")
                || path.contains("delta");
    }
}
