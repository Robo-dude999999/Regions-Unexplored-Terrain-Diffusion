package net.mcreator.regunexterdiff.mixins;

import net.mcreator.regunexterdiff.RegUnexTerDiffMod;
import net.mcreator.regunexterdiff.worldgen.BiomeRuleRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Bridges data-pack registered custom biome keys into Terrain Diffusion's
 * short-ID map. No third-party biome is added implicitly unless a rule names it.
 */
@Pseudo
@Mixin(targets = "com.github.xandergos.terraindiffusionmc.world.TerrainDiffusionBiomeSource", remap = false)
public abstract class TerrainDiffusionBridge {
    @Shadow private HolderGetter<Biome> biomeLookup;
    @Shadow private Map<Short, Holder<Biome>> biomeIdMap;

    @Unique
    private Map<Short, Holder<Biome>> regUnexTerDiff$baseBiomeIdMap;

    @Unique
    private final Set<ResourceLocation> regUnexTerDiff$warnedMissingBiomes = new HashSet<>();

    @Inject(method = "requireBiomeIdMap", at = @At("TAIL"), remap = false)
    private void regUnexTerDiff$applyConfiguredBiomes(CallbackInfo ci) {
        if (biomeIdMap == null || biomeLookup == null) {
            return;
        }

        // Preserve the native IDs once, then rebuild from them on every call so
        // data-pack reloads can remove stale custom IDs as well as add new ones.
        if (regUnexTerDiff$baseBiomeIdMap == null) {
            regUnexTerDiff$baseBiomeIdMap = new HashMap<>(biomeIdMap);
        }

        Map<Short, Holder<Biome>> mapped = new HashMap<>(regUnexTerDiff$baseBiomeIdMap);
        Set<ResourceLocation> availableKeys = new HashSet<>();
        for (Map.Entry<ResourceLocation, Short> entry : BiomeRuleRegistry.biomeIds().entrySet()) {
            ResourceKey<Biome> key = ResourceKey.create(Registries.BIOME, entry.getKey());
            Optional<Holder.Reference<Biome>> holder = biomeLookup.get(key);
            if (holder.isPresent()) {
                mapped.put(entry.getValue(), holder.get());
                availableKeys.add(entry.getKey());
            } else if (regUnexTerDiff$warnedMissingBiomes.add(entry.getKey())) {
                RegUnexTerDiffMod.LOGGER.warn(
                        "Terrain Diffusion biome rule references missing biome '{}'; rules for this key will be skipped",
                        entry.getKey()
                );
            }
        }

        BiomeRuleRegistry.setAvailableBiomeKeys(availableKeys);
        biomeIdMap = Map.copyOf(mapped);
    }
}
