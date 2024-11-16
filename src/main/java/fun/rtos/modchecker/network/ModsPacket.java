package fun.rtos.modchecker.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public record ModsPacket(List<String> mods) implements CustomPacketPayload {
    public static final StreamCodec<FriendlyByteBuf, ModsPacket> CODEC = StreamCodec.of(ModsPacket::write, ModsPacket::new);

    public ModsPacket(@NotNull FriendlyByteBuf buf) {
        this(fromBuf(buf));
    }

    public static void write(@NotNull FriendlyByteBuf buf, @NotNull ModsPacket packet) {
        buf.writeVarInt(packet.mods.size());
        for (String mod : packet.mods) {
            buf.writeUtf(mod);
        }
    }

    private static @NotNull List<String> fromBuf(@NotNull FriendlyByteBuf buf) {
        int size = buf.readVarInt();
        List<String> result = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            result.add(buf.readUtf());
        }
        return result;
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return Networks.MODS;
    }
}
