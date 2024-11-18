package fun.rtos.modchecker.mixin;

import fun.rtos.modchecker.utils.PlayerStatus;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking;
import net.fabricmc.fabric.impl.networking.CommonPacketsImpl;
import net.fabricmc.fabric.impl.networking.CommonVersionPayload;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("UnstableApiUsage")
@Mixin(value = CommonPacketsImpl.class, remap = false)
public class CommonPacketsImplMixin {
    @Inject(method = "lambda$init$0", at = @At("HEAD"))
    private static void init(@NotNull CommonVersionPayload payload, ServerConfigurationNetworking.@NotNull Context context, CallbackInfo ci) {
        PlayerStatus.hasFabric(context.networkHandler().getOwner().getId());
    }
}
