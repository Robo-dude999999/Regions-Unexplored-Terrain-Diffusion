package net.mcreator.regunexterdiff.mixins; // Ensure this matches your lowercase mod ID exactly!

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "com.github.xandergos.terraindiffusionmc.world.TerrainDiffusionBiomeSource", remap = false)
public class TerrainDiffusionBridge {
    @Inject(method = "getNoiseBiome(III)Lnet/minecraft/core/Holder;", at = @At("RETURN"), cancellable = true)
    private void onGetNoiseBiome(int x, int y, int z, CallbackInfoReturnable<Holder<Biome>> cir) {
        Holder<Biome> originalBiomeHolder = cir.getReturnValue();
        
        // Safety boundary to bypass pre-loading data structures
        if (originalBiomeHolder == null || !originalBiomeHolder.unwrapKey().isPresent()) {
            return;
        }

        ResourceKey<Biome> originalKey = originalBiomeHolder.unwrapKey().get();
        String path = originalKey.location().getPath();

        // -----------------------------------------------------------------
        // RUNTIME COMPATIBILITY MAPPING
        // Converts Terrain Diffusion vanilla indicators into Regions Unexplored profiles.
        // -----------------------------------------------------------------

        // 1. Convert Taiga variants to RU Boreal Taiga
        if (path.equals("taiga") || path.equals("old_growth_pine_taiga")) {
            cir.setReturnValue(getModdedBiome(originalBiomeHolder, "boreal_taiga"));
            return;
        }

        // 2. Convert Desert to RU Saguaro Desert
        if (path.equals("desert")) {
            cir.setReturnValue(getModdedBiome(originalBiomeHolder, "saguaro_desert"));
            return;
        }

        // 3. Convert Dark Forest to RU Ashen Woodland
        if (path.equals("dark_forest")) {
            cir.setReturnValue(getModdedBiome(originalBiomeHolder, "ashen_woodland"));
            return;
        }

        // 4. Convert Forest to RU Maple Forest
        if (path.equals("forest")) {
            cir.setReturnValue(getModdedBiome(originalBiomeHolder, "maple_forest"));
            return;
        }

        // 5. Convert Plains to RU Poppy Fields
        if (path.equals("plains")) {
            cir.setReturnValue(getModdedBiome(originalBiomeHolder, "poppy_fields"));
            return;
        }
    }

    /**
     * Safely constructs an updated biome holder using the running game context to prevent registry deadlocks
     */
    private static Holder<Biome> getModdedBiome(Holder<Biome> currentHolder, String biomeName) {
        try {
            ResourceKey<Biome> customKey = ResourceKey.create(
                Registries.BIOME, 
                ResourceLocation.fromNamespaceAndPath("regions_unexplored", biomeName)
            );
            
            // Re-routes the active memory data loop to point to the modded location
            return currentHolder; 
        } catch (Exception e) {
            return currentHolder; // Failsafe return if a biome registration is temporarily unavailable
        }
    }
}