package fun.rtos.modchecker;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fun.rtos.modchecker.event.ServerEventListener;
import fun.rtos.modchecker.event.ServerPlayConnectionEventsListener;
import fun.rtos.modchecker.network.Networks;
import fun.rtos.modchecker.network.ServerNetworks;
import fun.rtos.modchecker.utils.ModCheckerConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Optional;

public class ModChecker implements ModInitializer {
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static final String MOD_ID = "modchecker";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("%s.json".formatted(MOD_ID));
    public static final ModCheckerConfig CONFIG = new ModCheckerConfig(CONFIG_PATH, ModCheckerConfig.Mode.BLACKLIST);

    @Override
    public void onInitialize() {
        ModChecker.CONFIG.loadConfig();
        Networks.register();
        ServerNetworks.register();
        ServerEventListener.register();
        ServerPlayConnectionEventsListener.register();
    }

    public static @NotNull ResourceLocation of(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    public static String getModVersion() {
        Optional<ModContainer> container = FabricLoader.getInstance().getModContainer(ModChecker.MOD_ID);
        if (container.isPresent()) {
            ModContainer modContainer = container.get();
            return modContainer.getMetadata().getVersion().getFriendlyString();
        }
        return "unknown";
    }
}
