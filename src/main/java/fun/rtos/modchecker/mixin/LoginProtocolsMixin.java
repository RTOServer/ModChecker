package fun.rtos.modchecker.mixin;

import fun.rtos.modchecker.network.vanilla.HelloPacket;
import fun.rtos.modchecker.network.vanilla.VanillaNetworks;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.network.protocol.ProtocolInfoBuilder;
import net.minecraft.network.protocol.login.LoginProtocols;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LoginProtocols.class)
public class LoginProtocolsMixin {
    @Redirect(method = "method_56019", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/protocol/ProtocolInfoBuilder;addPacket(Lnet/minecraft/network/protocol/PacketType;Lnet/minecraft/network/codec/StreamCodec;)Lnet/minecraft/network/protocol/ProtocolInfoBuilder;", ordinal = 0))
    private static <T extends PacketListener, B extends ByteBuf, P extends Packet<? super T>> @NotNull ProtocolInfoBuilder<T, B> register(@NotNull ProtocolInfoBuilder<T, B> instance, PacketType<P> packetType, StreamCodec<? super B, P> streamCodec) {
        instance.addPacket(packetType, streamCodec);
        //noinspection unchecked
        instance.addPacket((PacketType<P>) VanillaNetworks.HELLO, (StreamCodec<? super B, P>) HelloPacket.STREAM_CODEC);
        return instance;
    }
}
