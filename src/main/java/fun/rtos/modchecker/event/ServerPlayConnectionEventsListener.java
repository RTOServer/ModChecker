package fun.rtos.modchecker.event;

import fun.rtos.modchecker.ModChecker;
import fun.rtos.modchecker.network.RequestPacket;
import fun.rtos.modchecker.utils.PlayerStatus;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class ServerPlayConnectionEventsListener {
    public static void register() {
        ServerPlayConnectionEvents.JOIN.register(ServerPlayConnectionEventsListener::onJoin);
        ServerPlayConnectionEvents.DISCONNECT.register(ServerPlayConnectionEventsListener::onDisconnect);
    }

    public static void onJoin(@NotNull ServerGamePacketListenerImpl handler, PacketSender sender, @NotNull MinecraftServer server) {
        if (server.isSingleplayer()) return;
        ServerPlayer player = handler.getPlayer();
        UUID id = player.getGameProfile().getId();
        if (!PlayerStatus.join(player)) return;
        if (PlayerStatus.shouldKick(id)) {
            player.connection.disconnect(Component.literal("Please install ModChecker!"));
            ModChecker.LOGGER.info("Player {} has been kicked for not having ModChecker", player.getName().getString());
            return;
        } else if (PlayerStatus.shouldIgnore(id)) {
            return;
        }
        ServerPlayNetworking.send(player, new RequestPacket());
    }

    public static void onDisconnect(@NotNull ServerGamePacketListenerImpl handler, MinecraftServer server) {
        PlayerStatus.disconnect(handler.getPlayer().getGameProfile().getId());
    }
}
