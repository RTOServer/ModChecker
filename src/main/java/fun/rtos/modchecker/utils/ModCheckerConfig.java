package fun.rtos.modchecker.utils;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ModCheckerConfig {
    private Mode mode;
    private final List<String> mods = new ArrayList<>();

    public ModCheckerConfig(Mode mode) {
        this.mode = mode;
    }

    public static @NotNull ModCheckerConfig fromRecord(@NotNull Record record) {
        ModCheckerConfig config = new ModCheckerConfig(Mode.fromString(record.mode));
        config.mods.addAll(record.mods);
        return config;
    }

    public static @NotNull Record toRecord(@NotNull ModCheckerConfig config) {
        return new Record(config.mode.toString(), config.mods);
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
        mods.add(mod);
    }

    public void addMods(@NotNull Collection<String> mod) {
        mods.addAll(mod);
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
