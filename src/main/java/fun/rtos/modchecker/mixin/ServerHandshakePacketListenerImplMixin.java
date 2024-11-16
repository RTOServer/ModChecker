package fun.rtos.modchecker.mixin;

import fun.rtos.modchecker.network.vanilla.HelloPacketListener;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketListener;
import net.minecraft.network.ProtocolInfo;
import net.minecraft.network.protocol.login.LoginProtocols;
import net.minecraft.server.network.ServerHandshakePacketListenerImpl;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerHandshakePacketListenerImpl.class)
public class ServerHandshakePacketListenerImplMixin {
    @Redirect(method = "beginLogin", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/Connection;setupInboundProtocol(Lnet/minecraft/network/ProtocolInfo;Lnet/minecraft/network/PacketListener;)V"))
    private <T extends PacketListener> void beginLogin(@NotNull Connection instance, ProtocolInfo<T> protocolInfo, T packetListener) {
        instance.setupInboundProtocol(LoginProtocols.SERVERBOUND, new HelloPacketListener((ServerLoginPacketListenerImplAccessor) packetListener));
    }
}
