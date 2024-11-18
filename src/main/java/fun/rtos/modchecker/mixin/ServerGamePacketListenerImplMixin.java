package fun.rtos.modchecker.mixin;

import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
abstract class ServerGamePacketListenerImplMixin {
    @Inject(method = "handleCustomPayload", at = @At("HEAD"))
    private void handleCustomPayload(@NotNull ServerboundCustomPayloadPacket serverboundCustomPayloadPacket, CallbackInfo ci) {
        CustomPacketPayload.Type<? extends CustomPacketPayload> type = serverboundCustomPayloadPacket.payload().type();
        System.out.println(type.id());
    }
}
