package net.mcreator.regunexterdiff.worldgen;

import net.minecraft.resources.ResourceLocation;

/**
 * A data-pack rule that maps raw Terrain Diffusion elevation/climate channels
 * to a registered biome. Climate channels are the four planes supplied to
 * BiomeClassifier, before its native classification noise is applied.
 */
public record CustomBiomeRule(
        ResourceLocation definitionId,
        ResourceLocation biomeKey,
        int priority,
        NumericRange elevation,
        NumericRange climateVarOne,
        NumericRange climateVarTwo,
        NumericRange climateVarThree,
        NumericRange climateVarFour
) {
    public boolean matches(float elevationValue, float one, float two, float three, float four) {
        return Float.isFinite(elevationValue)
                && Float.isFinite(one)
                && Float.isFinite(two)
                && Float.isFinite(three)
                && Float.isFinite(four)
                && elevation.contains(elevationValue)
                && climateVarOne.contains(one)
                && climateVarTwo.contains(two)
                && climateVarThree.contains(three)
                && climateVarFour.contains(four)
                && HydraulicIsolation.allows(biomeKey, elevationValue, elevation);
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
