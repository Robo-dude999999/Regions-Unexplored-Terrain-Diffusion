package net.mcreator.regunexterdiff;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class RegUnexTerDiffMod implements ModInitializer {
    public static final String MODID = "reg_unex_ter_diff";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    @Override
    public void onInitialize() {
        LOGGER.info("Regions Unexplored: Terrain Diffusion Fabric compatibility bridge loaded.");
    }
}
