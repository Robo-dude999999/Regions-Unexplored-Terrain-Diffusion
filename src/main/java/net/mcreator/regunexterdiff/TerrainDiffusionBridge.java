package net.mcreator.regunexterdiff.mixins;

import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Optional integration point for Terrain Diffusion versions that expose the
 * Mojang-mapped NeoForge BiomeSource API. The upstream Fabric build uses a
 * different API and cannot be patched by this mixin.
 *
 * This mixin intentionally does not replace biome holders without access to
 * the active biome registry: constructing a key alone does not resolve a biome.
 */
@Pseudo
@Mixin(targets = "com.github.xandergos.terraindiffusionmc.world.TerrainDiffusionBiomeSource", remap = false)
public abstract class TerrainDiffusionBridge {
    // Do not inject a guessed method signature here. The current upstream
    // Terrain Diffusion source is Fabric/Yarn and does not have getNoiseBiome.
    // A compatible NeoForge/Mojmap upstream build is required before a safe
    // injection can be enabled.
}
