package net.mcreator.regunexterdiff.worldgen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.mcreator.regunexterdiff.RegUnexTerDiffMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Loads data-pack files from data/<namespace>/terrain_diffusion_biomes/*.json.
 */
public final class BiomeRuleReloadListener extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new GsonBuilder().create();

    public BiomeRuleReloadListener() {
        super(GSON, "terrain_diffusion_biomes");
    }

    @Override
    protected void apply(
            Map<ResourceLocation, JsonElement> jsonFiles,
            ResourceManager resourceManager,
            ProfilerFiller profiler
    ) {
        List<CustomBiomeRule> loaded = new ArrayList<>();
        for (Map.Entry<ResourceLocation, JsonElement> entry : jsonFiles.entrySet()) {
            try {
                loaded.add(parse(entry.getKey(), entry.getValue()));
            } catch (RuntimeException exception) {
                RegUnexTerDiffMod.LOGGER.warn(
                        "Ignoring invalid Terrain Diffusion biome rule '{}': {}",
                        entry.getKey(), exception.getMessage()
                );
            }
        }

        BiomeRuleRegistry.replaceRules(loaded);
        if (loaded.isEmpty()) {
            RegUnexTerDiffMod.LOGGER.warn(
                    "No valid Terrain Diffusion biome JSON rules could be loaded. "
                            + "World generation will use minecraft:the_void as the fallback biome everywhere. "
                            + "This is non-fatal; add or fix files under data/<namespace>/terrain_diffusion_biomes/."
            );
        } else {
            RegUnexTerDiffMod.LOGGER.info("Loaded {} Terrain Diffusion biome compatibility rule(s)", loaded.size());
        }
    }

    private static CustomBiomeRule parse(ResourceLocation definitionId, JsonElement element) {
        if (element == null || !element.isJsonObject()) {
            throw new JsonParseException("expected a JSON object");
        }

        JsonObject json = element.getAsJsonObject();
        if (!json.has("biome_key") || !json.get("biome_key").isJsonPrimitive()
                || !json.getAsJsonPrimitive("biome_key").isString()) {
            throw new JsonParseException("missing string field 'biome_key'");
        }

        ResourceLocation biomeKey = ResourceLocation.tryParse(json.get("biome_key").getAsString());
        if (biomeKey == null) {
            throw new JsonParseException("invalid biome_key resource location");
        }

        int priority = 0;
        if (json.has("priority")) {
            JsonElement value = json.get("priority");
            if (!value.isJsonPrimitive() || !value.getAsJsonPrimitive().isNumber()) {
                throw new JsonParseException("'priority' must be an integer");
            }
            try {
                priority = value.getAsInt();
                if (value.getAsDouble() != priority) {
                    throw new JsonParseException("'priority' must be an integer");
                }
            } catch (NumberFormatException exception) {
                throw new JsonParseException("'priority' must be an integer", exception);
            }
        }

        return new CustomBiomeRule(
                definitionId,
                biomeKey,
                priority,
                readRange(json, "elevation"),
                readRange(json, "temp"),
                readRange(json, "t_season"),
                readRange(json, "precip"),
                readRange(json, "p_cv")
        );
    }

    private static CustomBiomeRule.NumericRange readRange(JsonObject parent, String name) {
        if (!parent.has(name) || !parent.get(name).isJsonObject()) {
            throw new JsonParseException("missing object field '" + name + "'");
        }
        JsonObject range = parent.getAsJsonObject(name);
        float min = readFiniteNumber(range, "min", name);
        float max = readFiniteNumber(range, "max", name);
        try {
            return new CustomBiomeRule.NumericRange(min, max);
        } catch (IllegalArgumentException exception) {
            throw new JsonParseException("invalid range '" + name + "': " + exception.getMessage(), exception);
        }
    }

    private static float readFiniteNumber(JsonObject parent, String field, String rangeName) {
        if (!parent.has(field) || !parent.get(field).isJsonPrimitive()
                || !parent.getAsJsonPrimitive(field).isNumber()) {
            throw new JsonParseException("range '" + rangeName + "' requires numeric '" + field + "'");
        }
        float value = parent.get(field).getAsFloat();
        if (!Float.isFinite(value)) {
            throw new JsonParseException("range '" + rangeName + "' has non-finite '" + field + "'");
        }
        return value;
    }
}
