package fun.rtos.modchecker.utils;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerStatus {
    private static final Map<UUID, PlayerStatus> PLAYER_STATUS_LIST = Collections.synchronizedMap(new HashMap<>());

    private boolean hasFabric = false;
    private boolean hasModChecker = false;

    private boolean shouldKick() {
        return hasFabric != hasModChecker;
    }

    private boolean shouldIgnore() {
        return !hasFabric && !hasModChecker;
    }

    public static PlayerStatus getOrCreate(@NotNull UUID player) {
        return PLAYER_STATUS_LIST.computeIfAbsent(player, (k) -> new PlayerStatus());
    }

    public static boolean shouldKick(@NotNull UUID player) {
        return PlayerStatus.getOrCreate(player).shouldKick();
    }

    public static boolean shouldIgnore(@NotNull UUID player) {
        return PlayerStatus.getOrCreate(player).shouldIgnore();
    }

    public static void hasFabric(@NotNull UUID player) {
        PlayerStatus.getOrCreate(player).hasFabric = true;
    }

    public static void hasModChecker(@NotNull UUID player) {
        PlayerStatus.getOrCreate(player).hasModChecker = true;
    }

    public static void disconnect(@NotNull UUID player) {
        PlayerStatus.PLAYER_STATUS_LIST.remove(player);
    }
}
