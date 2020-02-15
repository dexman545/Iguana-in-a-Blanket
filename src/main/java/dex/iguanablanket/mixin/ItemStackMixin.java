package dex.iguanablanket.mixin;

import dev.emi.iteminventory.api.ItemInventory;
import dex.iguanablanket.config.LuaConfigCompilation;
import dex.iguanablanket.impl.ItemWeight;
import net.minecraft.item.ItemStack;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.atomic.AtomicReference;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements ItemWeight {

	//Max stack size
	@Inject(at=@At("HEAD"), method = "getMaxCount()I", cancellable = true)
	public void getMaxCount(CallbackInfoReturnable<Integer> cir) {
		cir.setReturnValue(LuaConfigCompilation.stacksizes.getOrDefault(Registry.ITEM.getId(((ItemStack) (Object) this).getItem()).toString(), 1));

	}

	//Weight of the stack
	@Override
	public float getWeight() {
		AtomicReference<Float> sum = new AtomicReference<>(0f);
		sum.updateAndGet(v -> v + ((ItemWeight) (Object) this).getSingleWeight());

		if (((ItemStack) (Object) this).getItem() instanceof ItemInventory) {
			int size = ((ItemInventory) ((ItemStack) (Object) this).getItem()).getInvSize(((ItemStack) (Object) this));
			ItemInventory inv = (ItemInventory) ((ItemStack) (Object) this).getItem();

			for (int i = 0; i < size; i++) {
				int finalI = i;
				sum.updateAndGet(v -> v + (cfg.shulkerboxWeightReductionFactor() * ((ItemWeight) (Object) inv.getStack(((ItemStack) (Object) this), finalI)).getWeight()));
			}
		}

		return sum.get();
	}

	//weight of an item
	@Override
	public float getSingleWeight() {
		return ((ItemStack) (Object) this).getCount() * LuaConfigCompilation.weights.getOrDefault(Registry.ITEM.getId(((ItemStack) (Object) this).getItem()).toString(), 0f);
	}

}

