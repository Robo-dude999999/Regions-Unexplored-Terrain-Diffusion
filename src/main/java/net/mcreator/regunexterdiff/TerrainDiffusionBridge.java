package net.mcreator.regunexterdiff.mixins;

import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Optional Fabric bridge for Terrain Diffusion's biome source.
 *
 * Reflection is deliberate: the external Terrain Diffusion jar is not a
 * compile-time dependency, and this mod must still load if that mod is absent.
 * Runtime names are resolved in the named Fabric environment.
 */
@Pseudo
@Mixin(targets = "com.github.xandergos.terraindiffusionmc.world.TerrainDiffusionBiomeSource", remap = false)
public abstract class TerrainDiffusionBridge {
    @Inject(method = "requireBiomeIdMap", at = @At("TAIL"), remap = false, require = 0)
    private void regUnexTerDiff$mapRegionsUnexploredBiomes(CallbackInfo ci) {
        Object source = this;
        try {
            Field lookupField = findField(source.getClass(), "biomeLookup");
            Field mapField = findField(source.getClass(), "biomeIdMap");
            lookupField.setAccessible(true);
            mapField.setAccessible(true);

            Object lookup = lookupField.get(source);
            Object existingMap = mapField.get(source);
            if (!(existingMap instanceof Map<?, ?> original) || lookup == null) {
                return;
            }

            Map<Object, Object> mapped = new HashMap<>(original);
            int changed = 0;
            changed += mapFirstAvailable(mapped, lookup, (short) 1, "clover_plains", "barley_fields", "prairie");
            changed += mapFirstAvailable(mapped, lookup, (short) 3, "cold_deciduous_forest", "frozen_pine_taiga", "cold_boreal_taiga");
            changed += mapFirstAvailable(mapped, lookup, (short) 5, "dry_brushland", "arid_mountains");
            changed += mapFirstAvailable(mapped, lookup, (short) 6, "bayou", "fen", "marsh", "old_growth_bayou");
            changed += mapFirstAvailable(mapped, lookup, (short) 8, "deciduous_forest", "autumnal_maple_forest", "eucalyptus_forest", "alpha_grove");
            changed += mapFirstAvailable(mapped, lookup, (short) 15, "blackwood_taiga", "boreal_taiga", "pine_taiga");
            changed += mapFirstAvailable(mapped, lookup, (short) 16, "cold_boreal_taiga", "frozen_pine_taiga");
            changed += mapFirstAvailable(mapped, lookup, (short) 17, "baobab_savanna");
            changed += mapFirstAvailable(mapped, lookup, (short) 19, "chalk_cliffs", "arid_mountains");
            changed += mapFirstAvailable(mapped, lookup, (short) 23, "bamboo_forest", "sparse_rainforest", "tropical_river");
            changed += mapFirstAvailable(mapped, lookup, (short) 26, "dry_brushland", "ashen_woodland");
            changed += mapFirstAvailable(mapped, lookup, (short) 29, "flower_fields", "clover_plains", "barley_fields");
            changed += mapFirstAvailable(mapped, lookup, (short) 31, "alpha_grove", "temperate_grove");
            changed += mapFirstAvailable(mapped, lookup, (short) 32, "arid_mountains", "chalk_cliffs");
            changed += mapFirstAvailable(mapped, lookup, (short) 33, "arid_mountains", "chalk_cliffs");
            changed += mapFirstAvailable(mapped, lookup, (short) 35, "chalk_cliffs");
            changed += mapFirstAvailable(mapped, lookup, (short) 41, "rocky_reef");
            changed += mapFirstAvailable(mapped, lookup, (short) 44, "rocky_reef");
            changed += mapFirstAvailable(mapped, lookup, (short) 46, "cold_river");
            changed += mapFirstAvailable(mapped, lookup, (short) 48, "cold_river");

            if (changed > 0) {
                mapField.set(source, Map.copyOf(mapped));
                net.mcreator.regunexterdiff.RegUnexTerDiffMod.LOGGER.info(
                        "Mapped {} Terrain Diffusion biome IDs to available Regions Unexplored biomes.", changed);
            }
        } catch (ReflectiveOperationException | RuntimeException e) {
            net.mcreator.regunexterdiff.RegUnexTerDiffMod.LOGGER.warn(
                    "Terrain Diffusion biome compatibility bridge could not apply; keeping its default biome map.", e);
        }
    }

    private static Field findField(Class<?> type, String name) throws NoSuchFieldException {
        for (Class<?> current = type; current != null; current = current.getSuperclass()) {
            try {
                return current.getDeclaredField(name);
            } catch (NoSuchFieldException ignored) {
                // Continue up the hierarchy.
            }
        }
        throw new NoSuchFieldException(name);
    }

    private static int mapFirstAvailable(Map<Object, Object> target, Object lookup, short terrainId, String... biomeNames)
            throws ReflectiveOperationException {
        Method getMethod = lookup.getClass().getMethod("get", RegistryKey.class);
        for (String biomeName : biomeNames) {
            RegistryKey<?> key = RegistryKey.of(RegistryKeys.BIOME, Identifier.of("regions_unexplored", biomeName));
            Object result = getMethod.invoke(lookup, key);
            if (result instanceof Optional<?> optional && optional.isPresent()) {
                target.put(terrainId, optional.get());
                return 1;
            }
        }
        return 0;
    }
}
