package dex.iguanablanket.mixin;

import com.google.common.collect.ImmutableSet;
import dex.iguanablanket.config.LuaConfigCompilation;
import dex.iguanablanket.impl.ItemWeight;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ProjectileUtil;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.shape.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Set;
import java.util.stream.Stream;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {

    @Inject(at = @At("TAIL"), method = "applyBuoyancy()V", locals = LocalCapture.CAPTURE_FAILSOFT)
    private void modifyBuoyancy(CallbackInfo ci, Vec3d vec3d) {
        ItemStack stack = ((ItemEntity) (Object) this).getStack();

        float weight = stack.getCount() * LuaConfigCompilation.weights.getOrDefault(Registry.ITEM.getId(stack.getItem()).toString(), 0f);
        float waterWeight = 1.091f * LuaConfigCompilation.weights.getOrDefault("minecraft:ice", 1f);

        float scale = (-weight + waterWeight) / waterWeight;

        double deltaYVel = scale * (vec3d.y < 0.05999999865889549D ? 5.0E-4F : 0.0F);

        double yVel = vec3d.y + deltaYVel;

        ((ItemEntity)(Object)this).setVelocity(vec3d.x * 0.9900000095367432D, yVel, vec3d.z * 0.9900000095367432D);

    }

    private static EntityHitResult rayTraceEntity(Entity entity, float partialTicks) {
        Vec3d from = entity.getPos();
        Vec3d v = entity.getVelocity();
        Vec3d to = from.add(v);

        return ProjectileUtil.getEntityCollision(entity.world, entity, from, to, (new Box(from, to)).stretch(3, 3, 3).expand(5), EntityPredicates.VALID_ENTITY);
    }

    @Inject(at = @At("TAIL"), method = "tick()V")
    private void modifyTick(CallbackInfo ci) {
        Vec3d v = ((ItemEntity) (Object) this).getVelocity();


        EntityHitResult h = rayTraceEntity(((ItemEntity) (Object) this), 1.0F);
        if (h != null) {
            Entity e = h.getEntity();
            if (e instanceof LivingEntity) {
                ItemStack s = ((ItemEntity)(Object)this).getStack();
                double amount = ((ItemWeight) (Object) s).getWeight() / v.length();
                ((LivingEntity)e).damage(DamageSource.FALLING_BLOCK, (float) amount);
            }
        }
    }
}
