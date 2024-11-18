package fun.rtos.modchecker.network.listener;

import fun.rtos.modchecker.ModChecker;
import fun.rtos.modchecker.network.HelloPacket;
import fun.rtos.modchecker.network.Networks;
import fun.rtos.modchecker.utils.PlayerStatus;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class HelloPacketListener {
    public static void onHello(@NotNull HelloPacket packet, ServerConfigurationNetworking.Context context) {
        if (packet.type() != Networks.HELLO) return;
        UUID id = context.networkHandler().getOwner().getId();
        if (!ModChecker.getModVersion().equals(packet.version())) {
            PlayerStatus.onJoin(id, (player) -> {
                player.connection.disconnect(Component.literal("Need ModChecker %s, but find %s!".formatted(ModChecker.getModVersion(), packet.version())));
                return false;
            });
        } else {
            PlayerStatus.onJoin(id, (player) -> {
                ModChecker.LOGGER.info("Player {} join with ModChecker {}", player.getName().getString(), packet.version());
                return true;
            });
            PlayerStatus.hasModChecker(id);
        }
    }
}
