package dex.iguanablanket.mixin;

import dex.iguanablanket.IguanaBlanket;
import dex.iguanablanket.helpers.RespawnHelpers;
import dex.iguanablanket.impl.IguanaEntityAttributes;
import dex.iguanablanket.impl.ItemWeight;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {

    @Inject(at = @At("RETURN"), method = "findRespawnPosition(Lnet/minecraft/world/WorldView;Lnet/minecraft/util/math/BlockPos;Z)Ljava/util/Optional;", cancellable = true)
    private static void respawnLoc(WorldView world, BlockPos spawnPos, boolean allowNonBed, CallbackInfoReturnable<Optional<Vec3d>> cir) {
        Optional<Vec3d> original = cir.getReturnValue();
        double range = IguanaBlanket.cfg.randomRespawnRange();
        original.ifPresent(vec3d -> {
            Vec3d v = vec3d.add(range * ThreadLocalRandom.current().nextGaussian(), range * ThreadLocalRandom.current().nextGaussian(), range * ThreadLocalRandom.current().nextGaussian());

            Optional<Vec3d> f = RespawnHelpers.findWakeUpPosition(EntityType.PLAYER, world, new BlockPos(v), 10);
            f.ifPresent(vec3d1 -> {
                BlockPos pos = world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, new BlockPos(vec3d1));
                cir.setReturnValue(Optional.of(new Vec3d(pos)));
            });
        });
    }

    @Inject(slice = @Slice(from = @At(value = "INVOKE", target = "Ljava/lang/Math;sin(D)D")),
            at = @At(value = "TAIL", target = "Lnet/minecraft/entity/Entity;setVelocity(DDD)V", ordinal = 0),
            method = "dropItem(Lnet/minecraft/item/ItemStack;ZZ)Lnet/minecraft/entity/ItemEntity;",
            locals = LocalCapture.CAPTURE_FAILSOFT)
    private void modifyDropItem(ItemStack stack, boolean bl, boolean bl2, CallbackInfoReturnable<ItemEntity> cir, double d, ItemEntity itemEntity) {
        float f = 0.3F;
        Random random = ThreadLocalRandom.current();
        float g = MathHelper.sin(((PlayerEntity)(Object)this).pitch * 0.017453292F);
        float j = MathHelper.cos(((PlayerEntity)(Object)this).pitch * 0.017453292F);
        float k = MathHelper.sin(((PlayerEntity)(Object)this).yaw * 0.017453292F);
        float l = MathHelper.cos(((PlayerEntity)(Object)this).yaw * 0.017453292F);
        float m = random.nextFloat() * 6.2831855F;
        float n = 0.02F * random.nextFloat();
        Vec3d v = new Vec3d((double) (-k * j * 0.3F) + Math.cos((double) m) * (double) n, (double) (-g * 0.3F + 0.1F + (random.nextFloat() - random.nextFloat()) * 0.1F), (double) (l * j * 0.3F) + Math.sin((double) m) * (double) n);
        double q = ((PlayerEntity) (Object) this).getAttributeInstance(IguanaEntityAttributes.MAX_WEIGHT).getValue() - ((ItemWeight) (Object) stack).getWeight();
        double scale = (q / ((PlayerEntity) (Object) this).getAttributeInstance(IguanaEntityAttributes.MAX_WEIGHT).getValue() * 2);
        itemEntity.setVelocity(v.multiply(scale));
    }
}
