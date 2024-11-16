package fun.rtos.modchecker.network.vanilla;

import fun.rtos.modchecker.ModChecker;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.PacketType;

public class VanillaNetworks {
    public static final PacketType<HelloPacket> HELLO = new PacketType<>(PacketFlow.SERVERBOUND, ModChecker.of("hello"));
}
