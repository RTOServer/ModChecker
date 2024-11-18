package fun.rtos.modchecker.mixin.client;

import fun.rtos.modchecker.network.HelloPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.impl.networking.CommonVersionPayload;
import net.fabricmc.fabric.impl.networking.client.ClientNetworkingImpl;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("UnstableApiUsage")
@Mixin(ClientNetworkingImpl.class)
abstract class ClientNetworkingImplMixin {
    @Inject(method = "handleVersionPacket", at = @At("HEAD"))
    private static void handleVersionPacket(CommonVersionPayload payload, @NotNull PacketSender packetSender, CallbackInfoReturnable<Integer> cir) {
        packetSender.sendPacket(new HelloPacket());
    }
}
