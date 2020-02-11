package dex.iguanablanket.mixin;

import dex.iguanablanket.IguanaBlanket;
import dex.iguanablanket.helpers.RespawnHelpers;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
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
}
