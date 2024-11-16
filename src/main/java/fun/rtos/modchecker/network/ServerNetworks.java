package fun.rtos.modchecker.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class ServerNetworks {
    public static void register() {
        ServerPlayNetworking.registerGlobalReceiver(Networks.MODS, ModsPacketListener::onMods);
    }
}
