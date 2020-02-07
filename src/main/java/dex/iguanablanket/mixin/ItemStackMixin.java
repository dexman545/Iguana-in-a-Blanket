package dex.iguanablanket.mixin;

import dex.iguanablanket.ItemWeight;
import dex.iguanablanket.LuaConfigCompilation;
import net.minecraft.block.Block;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Identifier;
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
		cir.setReturnValue(LuaConfigCompilation.stacksizes.getOrDefault(Registry.ITEM.getId(((ItemStack) (Object) this).getItem()).toString(), 0));

	}

	//Weight of the stack
	@Override
	public float getWeight() {
		AtomicReference<Float> sum = new AtomicReference<>(0f);
		sum.updateAndGet(v -> v + ((ItemWeight) (Object) this).getSingleWeight());
		if (Block.getBlockFromItem(((ItemStack) (Object) this).getItem()) instanceof ShulkerBoxBlock) {
			DefaultedList<ItemStack> defaultedList = DefaultedList.ofSize(27, ItemStack.EMPTY);
			Inventories.fromTag(((ItemStack) (Object) this).getOrCreateTag().getCompound("BlockEntityTag"), defaultedList);
			defaultedList.forEach(stack -> {
				sum.updateAndGet(v -> v + (cfg.shulkerboxWeightReductionFactor() * ((ItemWeight) (Object) stack).getSingleWeight()));
			});
		}

		return sum.get();
	}

	//weight of an item
	@Override
	public float getSingleWeight() {
		return ((ItemStack) (Object) this).getCount() * LuaConfigCompilation.weights.getOrDefault(Registry.ITEM.getId(((ItemStack) (Object) this).getItem()).toString(), 0f);
	}

}

