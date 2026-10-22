package net.mcreator.regunexterdiff.worldgen;

import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

/**
 * Detects Terrain Diffusion biome sources without linking against optional
 * Lithostitched internals at compile time.
 */
public final class TerrainDiffusionWorldgenGuard {
    private static final String TERRAIN_DIFFUSION_SOURCE =
            "com.github.xandergos.terraindiffusionmc.world.TerrainDiffusionBiomeSource";
    private static final ThreadLocal<Boolean> SUPPRESS_SURFACE_RULES =
            ThreadLocal.withInitial(() -> Boolean.FALSE);

    private TerrainDiffusionWorldgenGuard() {
    }

    public static boolean isTerrainDiffusion(BiomeSource source) {
        return isTerrainDiffusion(source, Collections.newSetFromMap(new IdentityHashMap<>()));
    }

    public static boolean isTerrainDiffusion(NoiseBasedChunkGenerator generator) {
        return isTerrainDiffusion(findBiomeSource(generator));
    }

    public static void setSurfaceRuleSuppression(boolean suppress) {
        SUPPRESS_SURFACE_RULES.set(suppress);
    }

    public static boolean consumeSurfaceRuleSuppression() {
        boolean suppress = SUPPRESS_SURFACE_RULES.get();
        SUPPRESS_SURFACE_RULES.remove();
        return suppress;
    }

    private static boolean isTerrainDiffusion(BiomeSource source, Set<Object> visited) {
        if (source == null || !visited.add(source)) {
            return false;
        }
        if (TERRAIN_DIFFUSION_SOURCE.equals(source.getClass().getName())) {
            return true;
        }

        for (String fieldName : new String[]{"directDelegate", "rootDelegate", "originalSource"}) {
            Object nested = readField(source, fieldName);
            if (nested instanceof BiomeSource nestedSource
                    && isTerrainDiffusion(nestedSource, visited)) {
                return true;
            }
        }
        return false;
    }

    private static BiomeSource findBiomeSource(NoiseBasedChunkGenerator generator) {
        try {
            Method method = generator.getClass().getMethod("getBiomeSource");
            method.setAccessible(true);
            Object value = method.invoke(generator);
            if (value instanceof BiomeSource source) {
                return source;
            }
        } catch (ReflectiveOperationException | RuntimeException ignored) {
            // Fall through to the mapped field used by Minecraft's generator.
        }

        Object value = readField(generator, "biomeSource");
        return value instanceof BiomeSource source ? source : null;
    }

    private static Object readField(Object instance, String name) {
        Class<?> current = instance.getClass();
        while (current != null) {
            try {
                Field field = current.getDeclaredField(name);
                field.setAccessible(true);
                return field.get(instance);
            } catch (NoSuchFieldException exception) {
                current = current.getSuperclass();
            } catch (ReflectiveOperationException | RuntimeException exception) {
                return null;
            }
        }
        return null;
    }
}
