package fun.rtos.modchecker.utils;

import fun.rtos.modchecker.ModChecker;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ModCheckerConfig {
    private final Path path;
    private Mode mode;
    private final List<String> mods = new ArrayList<>();

    public ModCheckerConfig(Path path, Mode mode) {
        this.path = path;
        this.mode = mode;
    }

    public void loadConfig() {
        if (!path.toFile().exists()) ModChecker.CONFIG.saveConfig();
        try (var reader = Files.newBufferedReader(path)) {
            this.fromRecord(ModChecker.GSON.fromJson(reader, ModCheckerConfig.Record.class));
        } catch (IOException e) {
            ModChecker.LOGGER.error("Failed to load config", e);
            throw new RuntimeException(e);
        }
    }

    public void saveConfig() {
        try (var writer = Files.newBufferedWriter(path)) {
            ModChecker.GSON.toJson(this.toRecord(), writer);
        } catch (IOException e) {
            ModChecker.LOGGER.error("Failed to save config", e);
            throw new RuntimeException(e);
        }
    }

    public void fromRecord(@NotNull Record record) {
        this.mode = Mode.fromString(record.mode);
        this.clear();
        this.mods.addAll(record.mods);
    }

    public @NotNull Record toRecord() {
        return new Record(this.mode.toString(), this.mods);
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public Mode getMode() {
        return mode;
    }

    public List<String> getMods() {
        return mods;
    }

    public void clear() {
        mods.clear();
    }

    public void addMod(@NotNull String mod) {
        mods.add(mod.trim());
    }

    public void addMods(@NotNull Collection<String> mod) {
        mod.forEach(this::addMod);
    }

    public void removeMod(@NotNull String mod) {
        mods.remove(mod);
    }

    public List<String> checkMods(@NotNull List<String> mods) {
        List<String> illegal = new ArrayList<>();
        for (String mod : mods) {
            if (mode == Mode.BLACKLIST && this.mods.contains(mod)) illegal.add(mod);
            else if (mode == Mode.WHITELIST && !this.mods.contains(mod)) illegal.add(mod);
        }
        return illegal;
    }

    public Component getMessage(List<String> illegal) {
        String msg = switch (this.mode) {
            case WHITELIST -> "Detected mods that are not on the whitelist:";
            case BLACKLIST -> "Detected mods on the blacklist:";
        };
        MutableComponent component = Component.literal(msg);
        int size = 0;
        for (String illegalMod : illegal) {
            if (size >= 7) {
                component.append("\nand %s more...".formatted(illegal.size() - size));
                break;
            }
            component.append("\n- ").append(illegalMod);
            size++;
        }
        return component;
    }

    public record Record(String mode, List<String> mods) {
    }

    public enum Mode {
        WHITELIST,
        BLACKLIST;

        public static Mode fromString(@NotNull String type) {
            return valueOf(type.toUpperCase());
        }

        @Override
        public @NotNull String toString() {
            return name().toLowerCase();
        }
    }
}
