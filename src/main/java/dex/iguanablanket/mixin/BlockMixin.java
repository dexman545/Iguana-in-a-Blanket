package dex.iguanablanket.mixin;

import dex.iguanablanket.Data;
import dex.iguanablanket.ModifierHelper;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
public abstract class BlockMixin {
    @Inject(at=@At("HEAD"), method = "onSteppedOn(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/Entity;)V")
    public void modifySteppedOn(World world, BlockPos pos, Entity entity, CallbackInfo ci) {
        if (entity instanceof LivingEntity) {
            double defaultMovementSpeed = ((LivingEntity) entity).getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).getBaseValue();
            if (Registry.BLOCK.get(Identifier.tryParse("minecraft:stone")) == world.getBlockState(entity.getBlockPos().down()).getBlock()) {
                ModifierHelper.changeMovementSpeed((LivingEntity) entity, Data.AttributeModifier.TERRAIN_SLOWDOWN, -0.1 * defaultMovementSpeed);
            } else {
                ModifierHelper.changeMovementSpeed((LivingEntity) entity, Data.AttributeModifier.TERRAIN_SLOWDOWN, 0D);
            }
        }
    }
}
