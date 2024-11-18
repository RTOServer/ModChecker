package fun.rtos.modchecker.network;

import fun.rtos.modchecker.network.listener.HelloPacketListener;
import fun.rtos.modchecker.network.listener.ModsPacketListener;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class ServerNetworks {
    public static void register() {
        ServerPlayNetworking.registerGlobalReceiver(Networks.MODS, ModsPacketListener::onMods);
        ServerPlayNetworking.registerGlobalReceiver(Networks.HELLO, HelloPacketListener::onHello);
    }
}
