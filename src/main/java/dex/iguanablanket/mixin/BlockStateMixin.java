package dex.iguanablanket.mixin;

import dex.iguanablanket.config.LuaConfigCompilation;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockState.class)
public abstract class BlockStateMixin {

	@Shadow public abstract Block getBlock();

	@Inject(at=@At("HEAD"), method = "getHardness(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;)F", cancellable = true)
	public void getHardness(BlockView view, BlockPos pos, CallbackInfoReturnable<Float> cir) {
		float factor = LuaConfigCompilation.blockhardness.getOrDefault(Registry.BLOCK.getId(((BlockState) (Object) this).getBlock()).toString(), 1f);
		cir.setReturnValue(this.getBlock().getHardness(((BlockState) (Object) this), view, pos) * factor);
	}

}

