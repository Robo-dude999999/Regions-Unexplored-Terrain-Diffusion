package net.mcreator.regunexterdiff.mixins;

import net.mcreator.regunexterdiff.worldgen.TerrainDiffusionWorldgenGuard;
import net.minecraft.world.level.biome.BiomeSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Prevents Lithostitched biome injectors from wrapping Terrain Diffusion's
 * classifier. Other biome sources retain Lithostitched's normal behavior.
 */
@Pseudo
@Mixin(
        targets = "dev.worldgen.lithostitched.impl.worldgen.biomeinjector.internal.InjectorBiomeSource",
        remap = false
)
public abstract class LithostitchedInjectorGuardMixin {
    @Shadow @Final private BiomeSource directDelegate;

    @Inject(method = "applyInjectors", at = @At("HEAD"), cancellable = true, remap = false)
    private void regUnexTerDiff$skipInjectorsForTerrainDiffusion(CallbackInfo ci) {
        if (TerrainDiffusionWorldgenGuard.isTerrainDiffusion(directDelegate)) {
            ci.cancel();
        }
    }
}
