package fun.rtos.modchecker.network.vanilla;

import net.minecraft.network.protocol.login.ServerLoginPacketListener;

public interface IHelloPacketListener extends ServerLoginPacketListener {
    void handleCheckHello(HelloPacket packet);
}
