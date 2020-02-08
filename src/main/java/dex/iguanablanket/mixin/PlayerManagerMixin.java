package dex.iguanablanket.mixin;

import dex.iguanablanket.impl.PlayerJoinCallback;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {
    @Inject(at = @At("TAIL"), method = "onPlayerConnect", cancellable = true)
    private void connect(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
        ActionResult result = PlayerJoinCallback.EVENT.invoker().join(player);
        if (result == ActionResult.FAIL) {
            ci.cancel();
        }
    }
}
