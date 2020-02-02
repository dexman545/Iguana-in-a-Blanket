package dex.iguanablanket.mixin;

import dex.iguanablanket.ServerReloadCallback;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
    @Inject(method = "reload()V", at = @At("TAIL"))
    public void afterReload(CallbackInfo info) {
        ServerReloadCallback.EVENT.invoker().onServerReload((MinecraftServer) (Object) this);
    }
}
