package dex.iguanablanket.mixin;

import dex.iguanablanket.helpers.Data;
import dex.iguanablanket.config.LuaConfigCompilation;
import dex.iguanablanket.helpers.ModifierHelper;
import net.minecraft.block.Block;
import net.minecraft.enchantment.EnchantmentHelper;
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

import java.util.Objects;

@Mixin(Block.class)
public abstract class BlockMixin {
    @Inject(at=@At("HEAD"), method = "onSteppedOn(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/Entity;)V")
    public void modifySteppedOn(World world, BlockPos pos, Entity entity, CallbackInfo ci) {
        if (entity instanceof LivingEntity) {
            String enchantment = LuaConfigCompilation.enchants.getOrDefault(Registry.BLOCK.getId(((Block) (Object) this)).toString(), "");
            boolean doIgnore = false;
            if (Identifier.tryParse(enchantment) != null) {
                if (Registry.ENCHANTMENT.get(Identifier.tryParse(enchantment)) != null) {
                    doIgnore = EnchantmentHelper.getEquipmentLevel(Objects.requireNonNull(Registry.ENCHANTMENT.get(Identifier.tryParse(enchantment))), (LivingEntity) entity) > 0;
                }
            }

            float factor = doIgnore ? 0f : LuaConfigCompilation.blockslowdown.getOrDefault(Registry.BLOCK.getId(((Block) (Object) this)).toString(), 0f);
            double defaultMovementSpeed = ((LivingEntity) entity).getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)
                    .getBaseValue();
            ModifierHelper.changeMovementSpeed((LivingEntity) entity, Data.AttributeModifier.TERRAIN_SLOWDOWN, -factor * defaultMovementSpeed);
        }
    }
}
