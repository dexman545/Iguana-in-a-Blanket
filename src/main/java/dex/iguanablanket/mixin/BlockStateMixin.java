package dex.iguanablanket.mixin;

import dex.iguanablanket.config.LuaConfigCompilation;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBlock.AbstractBlockState.class)
public abstract class BlockStateMixin {

	@Inject(at=@At("HEAD"), method = "getHardness", cancellable = true)
	public void getHardness(BlockView world, BlockPos pos, CallbackInfoReturnable<Float> cir) {
		float factor = LuaConfigCompilation.blockhardness.getOrDefault(Registry.BLOCK.getId(((BlockState) (Object) this).getBlock()).toString(), 1f);
		cir.setReturnValue(((BlockState)(Object)this).getHardness(world, pos) * factor);
	}

}

