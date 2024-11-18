package fun.rtos.modchecker.utils;

import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;

public class PlayerStatus {
    private static final Map<UUID, PlayerStatus> PLAYER_STATUS_LIST = Collections.synchronizedMap(new HashMap<>());

    private boolean hasFabric = false;
    private boolean hasModChecker = false;
    private final Set<Function<ServerPlayer, Boolean>> onJoin = new HashSet<>();

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

    public static void onJoin(@NotNull UUID player, Function<ServerPlayer, Boolean> runnable) {
        PlayerStatus.getOrCreate(player).onJoin.add(runnable);
    }

    public static boolean join(@NotNull ServerPlayer player) {
        boolean result = true;
        for (Function<ServerPlayer, Boolean> function : PlayerStatus.getOrCreate(player.getGameProfile().getId()).onJoin) {
            if (!function.apply(player)) {
                result = false;
                break;
            }
        }
        return result;
    }

    public static void disconnect(@NotNull UUID player) {
        PlayerStatus.PLAYER_STATUS_LIST.remove(player);
    }
}
