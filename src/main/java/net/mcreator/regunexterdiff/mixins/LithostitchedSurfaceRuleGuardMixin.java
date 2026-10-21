package net.mcreator.regunexterdiff.mixins;

import net.mcreator.regunexterdiff.worldgen.TerrainDiffusionWorldgenGuard;
import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.ArrayList;

/**
 * Skips Lithostitched surface-rule additions only for dimensions whose biome
 * source is Terrain Diffusion. Other dimensions remain unaffected.
 */
@Pseudo
@Mixin(
        targets = "dev.worldgen.lithostitched.worldgen.surface.SurfaceRuleManager",
        remap = false
)
public abstract class LithostitchedSurfaceRuleGuardMixin {
    @Redirect(
            method = "applySurfaceRules",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/levelgen/NoiseBasedChunkGenerator;generatorSettings()Lnet/minecraft/core/Holder;"
            ),
            remap = false
    )
    private static Holder<NoiseGeneratorSettings> regUnexTerDiff$inspectGenerator(
            NoiseBasedChunkGenerator generator
    ) {
        TerrainDiffusionWorldgenGuard.setSurfaceRuleSuppression(
                TerrainDiffusionWorldgenGuard.isTerrainDiffusion(generator)
        );
        return generator.generatorSettings();
    }

    @ModifyArg(
            method = "applySurfaceRules",
            at = @At(
                    value = "INVOKE",
                    target = "Ldev/worldgen/lithostitched/worldgen/surface/SurfaceRuleManager;buildModdedSurfaceRules(Ljava/util/ArrayList;Lnet/minecraft/world/level/levelgen/SurfaceRules$RuleSource;)Lnet/minecraft/world/level/levelgen/SurfaceRules$RuleSource;"
            ),
            index = 0,
            remap = false
    )
    private static ArrayList<?> regUnexTerDiff$dropRulesForTerrainDiffusion(ArrayList<?> injections) {
        return TerrainDiffusionWorldgenGuard.consumeSurfaceRuleSuppression()
                ? new ArrayList<>()
                : injections;
    }
}
