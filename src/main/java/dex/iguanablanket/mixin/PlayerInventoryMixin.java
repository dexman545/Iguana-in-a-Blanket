package dex.iguanablanket.mixin;

import dex.iguanablanket.PlayerInvChangeCallback;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerInventory.class)
public abstract class PlayerInventoryMixin {
    @Inject(at=@At("INVOKE"), method = "setInvStack(ILnet/minecraft/item/ItemStack;)V", cancellable = true)
    private void playerInvChange(int slot, ItemStack stack, CallbackInfo ci) {
        ActionResult result = PlayerInvChangeCallback.EVENT.invoker().change(((PlayerInventory) (Object) this).player, slot, stack);
        if (result == ActionResult.FAIL) {
            ci.cancel();
        }
    }
}
