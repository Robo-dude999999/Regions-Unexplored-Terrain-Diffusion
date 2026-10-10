package net.mcreator.regunexterdiff.mixins;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Integrates the Mojmap NeoForge 1.21.1 Terrain Diffusion biome source with
 * Regions Unexplored. Terrain Diffusion's classifier emits a fixed set of
 * short biome IDs, so each supported ID is mapped to an RU biome when one of
 * the known biome keys exists; vanilla remains the safe fallback.
 *
 * This is intentionally registry-lookup based: it does not force-load RU
 * classes and it does not assume a biome exists just because its key is known.
 */
@Pseudo
@Mixin(targets = "com.github.xandergos.terraindiffusionmc.world.TerrainDiffusionBiomeSource", remap = false)
public abstract class TerrainDiffusionBridge {
    @Shadow
    private HolderGetter<Biome> biomeLookup;

    @Shadow
    @Mutable
    private Map<Short, Holder<Biome>> biomeIdMap;

    @Inject(method = "requireBiomeIdMap", at = @At("TAIL"), remap = false)
    private void regUnexTerDiff$mapRegionsUnexploredBiomes(CallbackInfo ci) {
        if (biomeIdMap == null || biomeLookup == null) {
            return;
        }

        // Terrain Diffusion initializes this with Map.ofEntries(), which is
        // immutable. Copy it before applying optional compatibility overrides.
        Map<Short, Holder<Biome>> mapped = new HashMap<>(biomeIdMap);
        int replaced = 0;

        replaced += mapFirstAvailable(mapped, (short) 1,
                "clover_plains", "barley_fields", "prairie");
        replaced += mapFirstAvailable(mapped, (short) 3,
                "cold_deciduous_forest", "frozen_pine_taiga", "cold_boreal_taiga");
        replaced += mapFirstAvailable(mapped, (short) 5,
                "dry_brushland", "arid_mountains");
        replaced += mapFirstAvailable(mapped, (short) 6,
                "bayou", "fen", "marsh", "old_growth_bayou");
        replaced += mapFirstAvailable(mapped, (short) 8,
                "deciduous_forest", "autumnal_maple_forest", "eucalyptus_forest", "alpha_grove");
        replaced += mapFirstAvailable(mapped, (short) 15,
                "blackwood_taiga", "boreal_taiga", "pine_taiga");
        replaced += mapFirstAvailable(mapped, (short) 16,
                "cold_boreal_taiga", "frozen_pine_taiga");
        replaced += mapFirstAvailable(mapped, (short) 17,
                "baobab_savanna");
        replaced += mapFirstAvailable(mapped, (short) 19,
                "chalk_cliffs", "arid_mountains");
        replaced += mapFirstAvailable(mapped, (short) 23,
                "bamboo_forest", "sparse_rainforest", "tropical_river");
        replaced += mapFirstAvailable(mapped, (short) 26,
                "dry_brushland", "ashen_woodland");
        replaced += mapFirstAvailable(mapped, (short) 29,
                "flower_fields", "clover_plains", "barley_fields");
        replaced += mapFirstAvailable(mapped, (short) 31,
                "alpha_grove", "temperate_grove");
        replaced += mapFirstAvailable(mapped, (short) 32,
                "arid_mountains", "chalk_cliffs");
        replaced += mapFirstAvailable(mapped, (short) 33,
                "arid_mountains", "chalk_cliffs");
        replaced += mapFirstAvailable(mapped, (short) 35,
                "chalk_cliffs");
        replaced += mapFirstAvailable(mapped, (short) 41,
                "rocky_reef");
        replaced += mapFirstAvailable(mapped, (short) 44,
                "rocky_reef");
        replaced += mapFirstAvailable(mapped, (short) 46,
                "cold_river");
        replaced += mapFirstAvailable(mapped, (short) 48,
                "cold_river");

        if (replaced > 0) {
            biomeIdMap = Map.copyOf(mapped);
        }
    }

    private int mapFirstAvailable(Map<Short, Holder<Biome>> target, short terrainId, String... biomeNames) {
        for (String biomeName : biomeNames) {
            ResourceKey<Biome> key = ResourceKey.create(
                    Registries.BIOME,
                    ResourceLocation.fromNamespaceAndPath("regions_unexplored", biomeName)
            );
            Optional<Holder.Reference<Biome>> holder = biomeLookup.get(key);
            if (holder.isPresent()) {
                target.put(terrainId, holder.get());
                return 1;
            }
        }
        return 0;
    }
}
