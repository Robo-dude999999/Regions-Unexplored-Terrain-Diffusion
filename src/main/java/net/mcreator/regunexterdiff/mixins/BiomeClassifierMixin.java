package net.mcreator.regunexterdiff.mixins;

import net.mcreator.regunexterdiff.worldgen.BiomeRuleRegistry;
import net.mcreator.regunexterdiff.worldgen.CustomBiomeRule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Applies custom data-pack rules after Terrain Diffusion's native classifier.
 * The classifier input arrays are the raw elevation and four climate planes.
 */
@Pseudo
@Mixin(
        targets = "com.github.xandergos.terraindiffusionmc.pipeline.BiomeClassifier",
        remap = false
)
public abstract class BiomeClassifierMixin {
    @Inject(method = "classify", at = @At("RETURN"), cancellable = true, remap = false)
    private static void regUnexTerDiff$applyPriorityRules(
            float[] elevation,
            float[] climate,
            int i0,
            int j0,
            float[] elevationPadded,
            int height,
            int width,
            float pixelSizeMeters,
            CallbackInfoReturnable<short[]> cir
    ) {
        int pixelCount = height * width;
        short[] biomeIds = cir.getReturnValue();
        if (height <= 0 || width <= 0 || pixelCount <= 0
                || elevation == null || elevation.length < pixelCount
                || climate == null || climate.length < pixelCount * 4
                || biomeIds == null || biomeIds.length < pixelCount) {
            return;
        }

        for (int index = 0; index < pixelCount; index++) {
            float elev = elevation[index];
            float climateOne = climate[index];
            float climateTwo = climate[pixelCount + index];
            float climateThree = climate[2 * pixelCount + index];
            float climateFour = climate[3 * pixelCount + index];

            for (CustomBiomeRule rule : BiomeRuleRegistry.rules()) {
                Short customId = BiomeRuleRegistry.biomeIdFor(rule.biomeKey());
                if (customId != null
                        && rule.matches(elev, climateOne, climateTwo, climateThree, climateFour)) {
                    biomeIds[index] = customId;
                    break;
                }
            }
        }

        cir.setReturnValue(biomeIds);
    }
}
