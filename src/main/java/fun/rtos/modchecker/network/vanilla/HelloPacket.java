package fun.rtos.modchecker.network.vanilla;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record HelloPacket(UUID uuid) implements Packet<IHelloPacketListener> {
    public static final StreamCodec<FriendlyByteBuf, HelloPacket> STREAM_CODEC = Packet.codec(
        HelloPacket::write, HelloPacket::new
    );


    private HelloPacket(@NotNull FriendlyByteBuf friendlyByteBuf) {
        this(friendlyByteBuf.readUUID());
    }

    private void write(@NotNull FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeUUID(this.uuid);
    }

    @Override
    public @NotNull PacketType<HelloPacket> type() {
        return VanillaNetworks.HELLO;
    }

    @Override
    public void handle(@NotNull IHelloPacketListener packetListener) {
        packetListener.handleCheckHello(this);
    }
}
