package dex.iguanablanket.mixin;

import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {

    @Inject(method = "dropSelectedItem(Z)Z", cancellable = true, at = @At("INVOKE"))
    private void modifyDropItem(boolean dropEntireStack, CallbackInfoReturnable<Boolean> cir) {
        if ((!dropEntireStack)) {
            // && !(IguanaBlanket.playerDropPower.getOrDefault(((ClientPlayerEntity)(PlayerEntity)(Object)this).getUuid(), 0) > 0)
            cir.cancel();
        }
    }
}
