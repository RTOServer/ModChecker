package fun.rtos.modchecker;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fun.rtos.modchecker.event.ServerPlayConnectionEventsListener;
import fun.rtos.modchecker.network.Networks;
import fun.rtos.modchecker.network.ServerNetworks;
import fun.rtos.modchecker.utils.ModCheckerConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ModChecker implements ModInitializer {
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static final String MOD_ID = "modchecker";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final Path CONFIGS = FabricLoader.getInstance().getConfigDir();
    public static final Path CONFIG_PATH = CONFIGS.resolve("%s.json".formatted(MOD_ID));
    public static final ModCheckerConfig CONFIG = new ModCheckerConfig(ModCheckerConfig.Mode.BLACKLIST);
    public static final List<UUID> PLAYERS = new ArrayList<>();

    @Override
    public void onInitialize() {
        ModChecker.loadConfig();
        ServerLifecycleEvents.BEFORE_SAVE.register(ModChecker::saveConfig);
        Networks.register();
        ServerNetworks.register();
        ServerPlayConnectionEventsListener.register();
    }

    public static void loadConfig() {
        if (!CONFIG_PATH.toFile().exists()) ModChecker.saveConfig(null, false, false);
        try (var reader = Files.newBufferedReader(CONFIG_PATH)) {
            ModCheckerConfig config = ModCheckerConfig.fromRecord(GSON.fromJson(reader, ModCheckerConfig.Record.class));
            ModChecker.CONFIG.setMode(config.getMode());
            ModChecker.CONFIG.clear();
            ModChecker.CONFIG.addMods(config.getMods());
        } catch (IOException e) {
            LOGGER.error("Failed to load config", e);
            throw new RuntimeException(e);
        }
    }

    public static void saveConfig(MinecraftServer server, boolean flush, boolean force) {
        try (var writer = Files.newBufferedWriter(CONFIG_PATH)) {
            GSON.toJson(ModCheckerConfig.toRecord(CONFIG), writer);
        } catch (IOException e) {
            LOGGER.error("Failed to save config", e);
            throw new RuntimeException(e);
        }
    }

    public static @NotNull ResourceLocation of(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
