package fun.rtos.modchecker.mixin.client;

import fun.rtos.modchecker.network.vanilla.HelloPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConnectScreen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.client.gui.screens.ConnectScreen$1")
public class ConnectScreen$1Mixin {
    @Shadow
    @Final
    ConnectScreen field_2416;

    @Inject(method = "run", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/Connection;send(Lnet/minecraft/network/protocol/Packet;)V", shift = At.Shift.AFTER))
    private void run(CallbackInfo ci) {
        ((ConnectScreenAccessor) this.field_2416).getConnection().send(new HelloPacket(Minecraft.getInstance().getGameProfile().getId()));
    }
}
