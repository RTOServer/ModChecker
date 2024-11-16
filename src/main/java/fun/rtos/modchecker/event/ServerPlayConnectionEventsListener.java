package fun.rtos.modchecker.event;

import fun.rtos.modchecker.ModChecker;
import fun.rtos.modchecker.network.RequestPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.jetbrains.annotations.NotNull;

public class ServerPlayConnectionEventsListener {
    public static void register() {
        ServerPlayConnectionEvents.JOIN.register(ServerPlayConnectionEventsListener::onJoin);
        ServerPlayConnectionEvents.DISCONNECT.register(ServerPlayConnectionEventsListener::onDisconnect);
    }

    public static void onJoin(@NotNull ServerGamePacketListenerImpl handler, PacketSender sender, @NotNull MinecraftServer server) {
        if (server.isSingleplayer()) return;
        ServerPlayer player = handler.getPlayer();
        if (!ModChecker.PLAYERS.contains(player.getGameProfile().getId())) {
            player.connection.disconnect(Component.literal("Not has ModChecker, please install!"));
            return;
        }
        ServerPlayNetworking.send(player, new RequestPacket());
    }

    public static void onDisconnect(@NotNull ServerGamePacketListenerImpl handler, MinecraftServer server) {
        ModChecker.PLAYERS.remove(handler.getPlayer().getGameProfile().getId());
    }
}
