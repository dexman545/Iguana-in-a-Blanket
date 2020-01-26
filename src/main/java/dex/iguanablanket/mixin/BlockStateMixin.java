package dex.iguanablanket.mixin;

import dex.iguanablanket.IguanaBlanket;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockState.class)
public abstract class BlockStateMixin {


	@Shadow public abstract Block getBlock();

	@Inject(at=@At("HEAD"), method = "getHardness(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;)F", cancellable = true)
	public void getHardness(BlockView view, BlockPos pos, CallbackInfoReturnable<Float> cir) {
		cir.setReturnValue(this.getBlock().getHardness(((BlockState) (Object) this), view, pos) * 1);
	}

	/*@Inject(at=@At("HEAD"), method = "onEntityCollision(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/Entity;)V", cancellable = true)
	public void modifyCollisionSpeed(World world, BlockPos pos, Entity entity, CallbackInfo ci) {
		if (entity instanceof PlayerEntity) {
			if (Registry.BLOCK.get(Identifier.tryParse("minecraft:stone")) == world.getBlockState(entity.getBlockPos().down()).getBlock()) {
				((PlayerEntity) entity).getAttributes().get(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.01f);
				System.out.println("1");
			} else {
				((PlayerEntity) entity).getAttributes().get(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.1f);
				System.out.println("2");
			}
		}
	}*/

}

