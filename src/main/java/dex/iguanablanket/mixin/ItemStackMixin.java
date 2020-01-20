package dex.iguanablanket.mixin;

import dex.iguanablanket.ItemWeight;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements ItemWeight {

	@Inject(at=@At("HEAD"), method = "getMaxCount()I", cancellable = true)
	public void getMaxCount(CallbackInfoReturnable<Integer> cir) {
		cir.setReturnValue(3);
	}

	@Override
	public float getWeight() {
		return ((ItemStack) (Object) this).getCount() * 3.0f;
	}

}

