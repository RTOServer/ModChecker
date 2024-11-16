package fun.rtos.modchecker.network.vanilla;

import fun.rtos.modchecker.ModChecker;
import fun.rtos.modchecker.mixin.ServerLoginPacketListenerImplAccessor;
import net.minecraft.network.Connection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import org.jetbrains.annotations.NotNull;

public class HelloPacketListener extends ServerLoginPacketListenerImpl implements IHelloPacketListener {
    public HelloPacketListener(MinecraftServer minecraftServer, Connection connection, boolean bl) {
        super(minecraftServer, connection, bl);
    }

    public HelloPacketListener(@NotNull ServerLoginPacketListenerImplAccessor listener) {
        this(listener.getServer(), listener.getConnection(), listener.isTransferred());
    }

    @Override
    public void handleCheckHello(@NotNull HelloPacket packet) {
        ModChecker.PLAYERS.add(packet.uuid());
    }
}
