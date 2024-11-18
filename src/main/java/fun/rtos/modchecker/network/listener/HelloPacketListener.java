package fun.rtos.modchecker.network.listener;

import fun.rtos.modchecker.ModChecker;
import fun.rtos.modchecker.network.HelloPacket;
import fun.rtos.modchecker.network.Networks;
import fun.rtos.modchecker.utils.PlayerStatus;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

public class HelloPacketListener {
    public static void onHello(@NotNull HelloPacket packet, ServerPlayNetworking.@NotNull Context context) {
        if (packet.type() != Networks.HELLO) return;
        ServerPlayer player = context.player();
        if (!ModChecker.getModVersion().equals(packet.version())) {
            player.connection.disconnect(Component.literal("Need ModChecker %s, but find %s!".formatted(ModChecker.getModVersion(), packet.version())));
        } else PlayerStatus.hasModChecker(player.getUUID());
    }
}
