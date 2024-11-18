package fun.rtos.modchecker.network;

import fun.rtos.modchecker.ModChecker;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public class Networks {
    public static final CustomPacketPayload.Type<RequestPacket> REQUEST = new CustomPacketPayload.Type<>(ModChecker.of("request"));
    public static final CustomPacketPayload.Type<ModsPacket> MODS = new CustomPacketPayload.Type<>(ModChecker.of("mods"));
    public static final CustomPacketPayload.Type<HelloPacket> HELLO = new CustomPacketPayload.Type<>(ModChecker.of("hello"));

    public static void register() {
        PayloadTypeRegistry.configurationC2S().register(Networks.HELLO, HelloPacket.CODEC);
        PayloadTypeRegistry.playS2C().register(Networks.REQUEST, RequestPacket.CODEC);
        PayloadTypeRegistry.playC2S().register(Networks.MODS, ModsPacket.CODEC);
    }
}
