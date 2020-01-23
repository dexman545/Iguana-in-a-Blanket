package dex.iguanablanket.mixin;

import dex.iguanablanket.ItemWeight;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

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

	@Inject(at=@At("RETURN"), method = "getTooltip(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/client/item/TooltipContext;)Ljava/util/List;", cancellable = true)
	public void getTooltip(PlayerEntity player, TooltipContext context, CallbackInfoReturnable<List<Text>> cir) {
		List<Text> toolTip = cir.getReturnValue();
		//Add weight to tooltip
		toolTip.add((new TranslatableText("iguana.tooltip.weight", ((ItemWeight) (Object) this).getWeight())).formatted(Formatting.DARK_GRAY));
		cir.setReturnValue(toolTip);
	}

}

