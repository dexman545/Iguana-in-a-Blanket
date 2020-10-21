package dex.iguanablanket.mixin;

import dex.iguanablanket.IguanaBlanket;
import dex.iguanablanket.impl.PlayerJoinCallback;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Blocks;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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

    @Inject(at = @At("RETURN"), method = "respawnPlayer", cancellable = true)
    public void respawnPlayer(ServerPlayerEntity player, boolean alive, CallbackInfoReturnable<ServerPlayerEntity> cir) {
        BlockPos blockPos = player.getSpawnPointPosition();
        if (blockPos != null) {
            if (IguanaBlanket.cfg.destroyBedRespawn() && player.world.getBlockState(blockPos).getBlock() instanceof BedBlock) {
                player.world.setBlockState(blockPos, Blocks.AIR.getDefaultState());
            }
        }
    }

}
