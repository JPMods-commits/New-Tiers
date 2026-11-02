package com.tiers.misc;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.tiers.TiersClient;
import com.tiers.textures.Icons;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

/** Settings for the McTiers fork; the original Tiers settings are independent. */
public final class ConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("new-tiers.json");
    private static Config config;

    private static final class Config {
        boolean toggleMod = TiersClient.toggleMod;
        boolean toggleIcons = TiersClient.toggleIcons;
        boolean toggleTab = TiersClient.toggleTab;
        boolean toggleChat = TiersClient.toggleChat;
        boolean toggleAdaptiveSeparator = TiersClient.toggleAdaptiveSeparator;
        boolean toggleAutoKitDetect = TiersClient.toggleAutoKitDetect;
        TiersClient.ModesTierDisplay displayMode = TiersClient.displayMode;
        Icons.Type activeIcons = Icons.Type.MCTIERS;
        TiersClient.DisplayStatus positionMCTiers = TiersClient.positionMCTiers;
        Mode activeMCTiersMode = TiersClient.activeMCTiersMode;
        TiersClient.DisplayStatus positionSubtiers = TiersClient.positionSubtiers;
        Mode activeSubtiersMode = TiersClient.activeSubtiersMode;
        String version = FabricLoader.getInstance().getModContainer("new_tiers")
                .map(mod -> mod.getMetadata().getVersion().getFriendlyString()).orElse("0.0.1+1.21.11");
    }

    public static void loadConfig() {
        config = new Config();
        if (Files.isRegularFile(CONFIG_PATH)) {
            try (Reader reader = Files.newBufferedReader(CONFIG_PATH, StandardCharsets.UTF_8)) {
                Config loaded = GSON.fromJson(reader, Config.class);
                if (loaded != null)
                    config = loaded;
            } catch (IOException | JsonSyntaxException exception) {
                TiersClient.LOGGER.warn("Could not read New Tiers settings; using defaults", exception);
            }
        }

        TiersClient.toggleMod = config.toggleMod;
        TiersClient.toggleIcons = config.toggleIcons;
        TiersClient.toggleTab = config.toggleTab;
        TiersClient.toggleChat = config.toggleChat;
        TiersClient.toggleAdaptiveSeparator = config.toggleAdaptiveSeparator;
        TiersClient.toggleAutoKitDetect = config.toggleAutoKitDetect;
        TiersClient.activeIcons = Icons.Type.MCTIERS;
        if (config.displayMode != null)
            TiersClient.displayMode = config.displayMode;
        if (config.positionMCTiers != null)
            TiersClient.positionMCTiers = config.positionMCTiers;
        if (config.positionSubtiers != null)
            TiersClient.positionSubtiers = config.positionSubtiers;
        if (Arrays.asList(Mode.getMCTiersValues()).contains(config.activeMCTiersMode))
            TiersClient.activeMCTiersMode = config.activeMCTiersMode;
        if (Arrays.asList(Mode.getSubtiersValues()).contains(config.activeSubtiersMode))
            TiersClient.activeSubtiersMode = config.activeSubtiersMode;
        if (TiersClient.positionMCTiers != TiersClient.DisplayStatus.OFF
                && TiersClient.positionMCTiers == TiersClient.positionSubtiers)
            TiersClient.positionSubtiers = TiersClient.DisplayStatus.OFF;
        saveConfig();
    }

    public static synchronized void saveConfig() {
        config = new Config();
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            Files.writeString(CONFIG_PATH, GSON.toJson(config), StandardCharsets.UTF_8);
        } catch (IOException exception) {
            TiersClient.LOGGER.warn("Could not save New Tiers settings", exception);
        }
        TiersClient.updateAllTags();
    }

    public static String getCurrentConfig() {
        return GSON.toJson(new Config());
    }
}
