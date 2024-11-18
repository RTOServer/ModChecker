package fun.rtos.modchecker.network.client;

import fun.rtos.modchecker.network.Networks;
import fun.rtos.modchecker.network.listener.client.RequestPacketListener;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class ClientNetworks {
    public static void register() {
        ClientPlayNetworking.registerGlobalReceiver(Networks.REQUEST, (packet, context) -> RequestPacketListener.onRequest(packet));
    }
}
