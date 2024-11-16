package fun.rtos.modchecker.network.client;

import fun.rtos.modchecker.network.ModsPacket;
import fun.rtos.modchecker.network.RequestPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class RequestPacketListener {
    public static void onRequest(@NotNull RequestPacket packet) {
        if (!packet.check()) return;
        List<String> mods = new ArrayList<>();
        for (ModContainer mod : FabricLoader.getInstance().getAllMods()) {
            ModMetadata metadata = mod.getMetadata();
            mods.add(metadata.getId());
        }
        ModsPacket packet1 = new ModsPacket(mods);
        ClientPlayNetworking.send(packet1);
    }
}
