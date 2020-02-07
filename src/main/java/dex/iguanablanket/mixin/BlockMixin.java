package dex.iguanablanket.mixin;

import dex.iguanablanket.Data;
import dex.iguanablanket.LuaConfigCompilation;
import dex.iguanablanket.ModifierHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
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
            float factor = LuaConfigCompilation.blockhardness.getOrDefault(Registry.BLOCK.getId(((Block) (Object) this)).toString(), 0f);
            double defaultMovementSpeed = ((LivingEntity) entity).getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).getBaseValue();
            ModifierHelper.changeMovementSpeed((LivingEntity) entity, Data.AttributeModifier.TERRAIN_SLOWDOWN, -factor * defaultMovementSpeed);
        }
    }
}
