package dex.iguanablanket.mixin;

import dex.iguanablanket.ItemWeight;
import net.minecraft.block.Block;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Formatting;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.PrimitiveIterator;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements ItemWeight {

	@Inject(at=@At("HEAD"), method = "getMaxCount()I", cancellable = true)
	public void getMaxCount(CallbackInfoReturnable<Integer> cir) {
		cir.setReturnValue(3);
	}

	@Override
	public float getWeight() {
		AtomicReference<Float> sum = new AtomicReference<>(0f);
		sum.updateAndGet(v -> (float) (v + ((ItemWeight) (Object) this).getSingleWeight()));
		if (Block.getBlockFromItem(((ItemStack) (Object) this).getItem()) instanceof ShulkerBoxBlock) {
			DefaultedList<ItemStack> defaultedList = DefaultedList.ofSize(27, ItemStack.EMPTY);
			Inventories.fromTag(((ItemStack) (Object) this).getOrCreateTag().getCompound("BlockEntityTag"), defaultedList);
			defaultedList.forEach(stack -> {
				sum.updateAndGet(v -> (float) (v + ((ItemWeight) (Object) stack).getSingleWeight()));
			});
		}

		return sum.get();
	}

	@Override
	public float getSingleWeight() {
		return ((ItemStack) (Object) this).getCount() * 3.0f;
	}

	@Inject(at=@At("RETURN"), method = "getTooltip(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/client/item/TooltipContext;)Ljava/util/List;", cancellable = true)
	public void getTooltip(PlayerEntity player, TooltipContext context, CallbackInfoReturnable<List<Text>> cir) {
		List<Text> toolTip = cir.getReturnValue();
		//Add weight to tooltip
		toolTip.add((new TranslatableText("iguana.tooltip.weight", ((ItemWeight) (Object) this).getWeight())).formatted(Formatting.ITALIC));
		cir.setReturnValue(toolTip);
	}

}

