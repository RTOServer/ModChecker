package fun.rtos.modchecker.network;

import fun.rtos.modchecker.ModChecker;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record HelloPacket(String version) implements CustomPacketPayload {
    public static final StreamCodec<FriendlyByteBuf, HelloPacket> CODEC = StreamCodec.of(HelloPacket::write, HelloPacket::new);

    public static void write(@NotNull FriendlyByteBuf buf, @NotNull HelloPacket packet) {
        buf.writeUtf(packet.version);
    }

    public HelloPacket(@NotNull FriendlyByteBuf buf) {
        this(buf.readUtf());
    }

    public HelloPacket() {
        this(ModChecker.getModVersion());
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return Networks.HELLO;
    }
}
