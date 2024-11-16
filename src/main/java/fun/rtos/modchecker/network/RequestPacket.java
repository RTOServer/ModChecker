package fun.rtos.modchecker.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

public record RequestPacket(boolean check) implements CustomPacketPayload {
    public static final StreamCodec<FriendlyByteBuf, RequestPacket> CODEC = StreamCodec.of(RequestPacket::write, RequestPacket::new);

    public static void write(@NotNull FriendlyByteBuf buf, @NotNull RequestPacket packet) {
        buf.writeBoolean(packet.check);
    }

    public RequestPacket(@NotNull FriendlyByteBuf buf) {
        this(buf.readBoolean());
    }

    public RequestPacket() {
        this(true);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return Networks.REQUEST;
    }
}
