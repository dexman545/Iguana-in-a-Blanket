package dex.iguanablanket.mixin;

import dex.iguanablanket.IguanaBlanket;
import dex.iguanablanket.helpers.RespawnHelpers;
import dex.iguanablanket.impl.IguanaEntityAttributes;
import dex.iguanablanket.impl.ItemWeight;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {

    @Inject(at = @At("RETURN"), method = "findRespawnPosition", cancellable = true)
    private static void respawnLoc(ServerWorld world, BlockPos pos1, float f1, boolean bl, boolean bl2, CallbackInfoReturnable<Optional<Vec3d>> cir) {
        Optional<Vec3d> original = cir.getReturnValue();
        double range = IguanaBlanket.cfg.randomRespawnRange();
        original.ifPresent(vec3d -> {
            Vec3d v = vec3d.add(range * ThreadLocalRandom.current().nextGaussian(), range * ThreadLocalRandom.current().nextGaussian(), range * ThreadLocalRandom.current().nextGaussian());

            Optional<Vec3d> f = RespawnHelpers.findWakeUpPosition(EntityType.PLAYER, world, new BlockPos(v), 10);
            f.ifPresent(vec3d1 -> {
                BlockPos pos = world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, new BlockPos(vec3d1));
                cir.setReturnValue(Optional.of(Vec3d.of(pos)));
            });
        });
    }

    @Inject(slice = @Slice(from = @At(value = "INVOKE", target = "Ljava/lang/Math;sin(D)D")),
            at = @At(value = "TAIL", target = "Lnet/minecraft/entity/Entity;setVelocity(DDD)V", ordinal = 0),
            method = "dropItem(Lnet/minecraft/item/ItemStack;ZZ)Lnet/minecraft/entity/ItemEntity;",
            locals = LocalCapture.CAPTURE_FAILSOFT)
    private void modifyDropItem(ItemStack stack, boolean bl, boolean bl2, CallbackInfoReturnable<ItemEntity> cir, double d, ItemEntity itemEntity) {
        if (IguanaBlanket.playerDropPower.getOrDefault(((ServerPlayerEntity)(PlayerEntity)(Object)this).getUuid(), 0f) > 0f) {
            Random random = ThreadLocalRandom.current();
            float g = MathHelper.sin(((PlayerEntity)(Object)this).pitch * 0.017453292F);
            float j = MathHelper.cos(((PlayerEntity)(Object)this).pitch * 0.017453292F);
            float k = MathHelper.sin(((PlayerEntity)(Object)this).yaw * 0.017453292F);
            float l = MathHelper.cos(((PlayerEntity)(Object)this).yaw * 0.017453292F);
            float m = 0;//(float) (random.nextGaussian() * .2831855F);
            float n = 0;//(float) (0.02F * random.nextGaussian());
            float power = IguanaBlanket.playerDropPower.get(((ServerPlayerEntity)(PlayerEntity)(Object)this).getUuid());
            /*Vec3d vel = new Vec3d(((PlayerEntity) (Object) this).getX() - ((PlayerEntity) (Object) this).prevX,
                ((PlayerEntity) (Object) this).getY() - ((PlayerEntity) (Object) this).prevY,
                ((PlayerEntity) (Object) this).getZ() - ((PlayerEntity) (Object) this).prevZ);*/
            //v.y = (double) (-g * 0.3F + 0.1F)
            Vec3d v = new Vec3d((double) (-k * j * 0.3F) + Math.cos((double) m) * (double) n, (double) -g + 0.1F, (double) (l * j * 0.3F) + Math.sin((double) m) * (double) n);
            v = v.multiply(0.6); //correct for large numbers
            double maxWeight = ((PlayerEntity) (Object) this).getAttributeInstance(IguanaEntityAttributes.MAX_WEIGHT).getValue();
            v = v.add(v.multiply((maxWeight - Math.min(maxWeight, 10*((ItemWeight) (Object) stack).getWeight())) / maxWeight));
            itemEntity.setVelocity(v.multiply(power, 1, power));
            IguanaBlanket.playerDropPower.put(((ServerPlayerEntity)(PlayerEntity)(Object)this).getUuid(), 0f);
        }
    }

    @Inject(at = @At("HEAD"), method = "dropItem(Lnet/minecraft/item/ItemStack;ZZ)Lnet/minecraft/entity/ItemEntity;", cancellable = true)
    private void cancelItemDrop(ItemStack stack, boolean bl, boolean bl2, CallbackInfoReturnable<ItemEntity> cir) {
        if (((PlayerEntity) (Object) this).world.isClient && (IguanaBlanket.playerDropPower.getOrDefault(((PlayerEntity)(Object)this).getUuid(), 0f) < 1f)) {
            cir.cancel();
        }
    }

    @Inject(method = "addExhaustion(F)V", at = @At("TAIL"))
    private void modifyExhaustion(float exhaustion, CallbackInfo ci) {
        double currentWeight = ((PlayerEntity)(Object)this).getAttributeInstance(IguanaEntityAttributes.WEIGHT).getValue();
        double maxWeight = ((PlayerEntity)(Object)this).getAttributeInstance(IguanaEntityAttributes.MAX_WEIGHT).getValue();
        float scale = (float) ((maxWeight + Math.min(maxWeight, currentWeight)) / maxWeight) - 1;
        ((PlayerEntity)(Object)this).getHungerManager().addExhaustion(scale * exhaustion);
    }
}
