package net.mcreator.regunexterdiff.worldgen;

import net.minecraft.resources.ResourceLocation;

/**
 * A data-pack rule that maps Terrain Diffusion's raw elevation and climate
 * channels to a registered biome. Climate channel names match the ordering
 * documented in BiomeClassifier: temp, t_season, precip, and p_cv.
 */
public record CustomBiomeRule(
        ResourceLocation definitionId,
        ResourceLocation biomeKey,
        int priority,
        NumericRange elevation,
        NumericRange temp,
        NumericRange tSeason,
        NumericRange precip,
        NumericRange pCv
) {
    public boolean matches(float elevationValue, float tempValue, float tSeasonValue,
                           float precipValue, float pCvValue) {
        return Float.isFinite(elevationValue)
                && Float.isFinite(tempValue)
                && Float.isFinite(tSeasonValue)
                && Float.isFinite(precipValue)
                && Float.isFinite(pCvValue)
                && elevation.contains(elevationValue)
                && temp.contains(tempValue)
                && tSeason.contains(tSeasonValue)
                && precip.contains(precipValue)
                && pCv.contains(pCvValue);
    }

    public record NumericRange(float min, float max) {
        public NumericRange {
            if (!Float.isFinite(min) || !Float.isFinite(max) || min > max) {
                throw new IllegalArgumentException("Range bounds must be finite and min <= max");
            }
        }

        public boolean contains(float value) {
            return value >= min && value <= max;
        }
    }
}
