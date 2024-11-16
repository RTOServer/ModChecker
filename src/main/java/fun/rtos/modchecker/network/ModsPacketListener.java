package fun.rtos.modchecker.network;

import fun.rtos.modchecker.ModChecker;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.commands.Commands;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ModsPacketListener {
    public static void onMods(@NotNull ModsPacket packet, ServerPlayNetworking.Context context) {
        List<String> illegal = ModChecker.CONFIG.checkMods(packet.mods());
        if (illegal.isEmpty()) return;
        ServerPlayer player = context.player();
        if (player.hasPermissions(Commands.LEVEL_GAMEMASTERS)) return;
        player.connection.disconnect(ModChecker.CONFIG.getMessage(illegal));
        ModChecker.LOGGER.info("Player %s has illegal mods: %s".formatted(player.getName().getString(), illegal));
    }
}
