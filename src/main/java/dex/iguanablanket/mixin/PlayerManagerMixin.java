package dex.iguanablanket.mixin;

import dex.iguanablanket.IguanaBlanket;
import dex.iguanablanket.helpers.RespawnHelpers;
import dex.iguanablanket.impl.PlayerJoinCallback;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;
import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicReference;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {
    @Inject(at = @At("TAIL"), method = "onPlayerConnect", cancellable = true)
    private void connect(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
        if (!connection.isLocal()) {
            ActionResult result = PlayerJoinCallback.EVENT.invoker().join(player);
            if (result == ActionResult.FAIL) {
                ci.cancel();
            }
        }
    }

    @Inject(at = @At("RETURN"), method = "respawnPlayer(Lnet/minecraft/server/network/ServerPlayerEntity;Lnet/minecraft/world/dimension/DimensionType;Z)Lnet/minecraft/server/network/ServerPlayerEntity;", cancellable = true)
    private void respawn(ServerPlayerEntity player, DimensionType dimension, boolean alive, CallbackInfoReturnable<ServerPlayerEntity> cir) {
        BlockPos blockPos = player.getSpawnPosition();
        if (blockPos != null) {
            System.out.println(player.world.getBlockState(blockPos).getBlock() instanceof BedBlock);
            if (IguanaBlanket.cfg.destroyBedRespawn() && player.world.getBlockState(blockPos).getBlock() instanceof BedBlock) {
                player.world.setBlockState(blockPos, Blocks.AIR.getDefaultState());
            }
        }

        System.out.println(player.world.getChunk(blockPos).getHeightmap(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES).asLongArray());

        /*Vec3d vec3d = test(player);
        player.refreshPositionAndAngles(vec3d.x, vec3d.y, vec3d.z, 0.0F, 0.0F);
        player.updatePosition(vec3d.x, vec3d.y, vec3d.z);
        player.networkHandler.requestTeleport(vec3d.x, vec3d.y, vec3d.z, player.yaw, player.pitch);
        player.setPos(vec3d.x, vec3d.y, vec3d.z);*/
    }

    private Vec3d test(ServerPlayerEntity playerEntity) {
        Optional<Vec3d> original = Optional.ofNullable(playerEntity.getPosVector());
        double range = IguanaBlanket.cfg.randomRespawnRange();
        AtomicReference<Vec3d> x = new AtomicReference<>();
        System.out.println(original);
        original.ifPresent(vec3d -> {
            Vec3d v = vec3d.add(range * ThreadLocalRandom.current().nextGaussian(), range * ThreadLocalRandom.current().nextGaussian(), range * ThreadLocalRandom.current().nextGaussian());
            System.out.println(v);

            x.set(RespawnHelpers.findWakeUpPosition(EntityType.PLAYER, playerEntity.world, new BlockPos(v), 10).orElse(original.get()));
        });

        return x.get();
    }
}
