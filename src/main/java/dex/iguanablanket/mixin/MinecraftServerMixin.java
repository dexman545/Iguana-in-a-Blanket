package dex.iguanablanket.mixin;

import dex.iguanablanket.impl.ServerReloadCallback;
import net.minecraft.server.MinecraftServer;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
    @Inject(method = "reloadResources", at = @At("TAIL"))
    public void onReload(Collection<String> collection, CallbackInfoReturnable<CompletableFuture<Void>> clr) {
        ServerReloadCallback.EVENT.invoker().onServerReload((MinecraftServer) (Object) this);
    }
}
