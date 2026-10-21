package net.mcreator.regunexterdiff.worldgen;

import net.minecraft.resources.ResourceLocation;
import net.mcreator.regunexterdiff.RegUnexTerDiffMod;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Immutable snapshot of data-pack rules. IDs are allocated deterministically
 * from sorted biome keys and kept outside Terrain Diffusion's built-in IDs.
 */
public final class BiomeRuleRegistry {
    private static final short FIRST_CUSTOM_ID = 1000;
    public static final short FALLBACK_BIOME_ID = Short.MAX_VALUE;
    public static final ResourceLocation FALLBACK_BIOME_KEY = ResourceLocation.withDefaultNamespace("the_void");
    private static volatile List<CustomBiomeRule> rules = List.of();
    private static volatile Map<ResourceLocation, Short> biomeIds = Map.of();
    // null means the biome registry has not yet been checked by a biome source.
    private static volatile Set<ResourceLocation> availableBiomeKeys = null;
    private static final Set<ResourceLocation> warnedMissingBiomes = ConcurrentHashMap.newKeySet();

    private static final Comparator<CustomBiomeRule> RULE_ORDER =
            Comparator.comparingInt(CustomBiomeRule::priority).reversed()
                    .thenComparing(rule -> rule.biomeKey().toString())
                    .thenComparing(rule -> rule.definitionId().toString());

    private BiomeRuleRegistry() {
    }

    public static List<CustomBiomeRule> rules() {
        return rules;
    }

    public static Map<ResourceLocation, Short> biomeIds() {
        return biomeIds;
    }

    public static Short biomeIdFor(ResourceLocation biomeKey) {
        Short id = biomeIds.get(biomeKey);
        Set<ResourceLocation> available = availableBiomeKeys;
        return id != null && (available == null || available.contains(biomeKey)) ? id : null;
    }

    public static void setAvailableBiomeKeys(Set<ResourceLocation> keys) {
        availableBiomeKeys = Set.copyOf(keys);
    }

    public static boolean markMissingBiomeWarning(ResourceLocation key) {
        return warnedMissingBiomes.add(key);
    }

    public static synchronized void replaceRules(List<CustomBiomeRule> loadedRules) {
        List<CustomBiomeRule> sortedRules = new ArrayList<>(loadedRules);
        sortedRules.sort(RULE_ORDER);

        TreeSet<ResourceLocation> uniqueBiomeKeys = new TreeSet<>(
                Comparator.comparing(ResourceLocation::toString)
        );
        for (CustomBiomeRule rule : sortedRules) {
            uniqueBiomeKeys.add(rule.biomeKey());
        }

        Map<ResourceLocation, Short> assignedIds = new LinkedHashMap<>();
        if (sortedRules.isEmpty()) {
            // Keep the fallback available even when every rule file is absent or invalid.
            assignedIds.put(FALLBACK_BIOME_KEY, FALLBACK_BIOME_ID);
        }
        int nextId = FIRST_CUSTOM_ID;
        for (ResourceLocation key : uniqueBiomeKeys) {
            if (nextId > Short.MAX_VALUE) {
                RegUnexTerDiffMod.LOGGER.warn(
                        "Custom biome ID space exhausted; some Terrain Diffusion biome keys will be ignored"
                );
                break;
            }
            assignedIds.put(key, (short) nextId++);
        }

        rules = List.copyOf(sortedRules);
        biomeIds = Map.copyOf(assignedIds);
        availableBiomeKeys = null;
    }
}
