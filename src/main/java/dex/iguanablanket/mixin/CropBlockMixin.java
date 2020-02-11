package dex.iguanablanket.mixin;

import net.minecraft.block.*;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({CropBlock.class, StemBlock.class, PlantBlock.class})
public abstract class CropBlockMixin {

    @Inject(at = @At("HEAD"), cancellable = true, method = "canPlantOnTop(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;)Z")
    public void modifyPlantOn(BlockState floor, BlockView view, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(true);
    }

    /*@Inject(at = @At("HEAD"), cancellable = true, method = "getMaxAge()I")
    public void modifyMaxAge(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(11);
    }*/

    /*@Inject(at = @At("HEAD"), cancellable = true, method = "applyGrowth(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)V")
    public void modifyApplyGrowth(World world, BlockPos pos, BlockState state, CallbackInfo ci) {

    }

    @Inject(at = @At("HEAD"), cancellable = true, method = "getAvailableMoisture(Lnet/minecraft/block/Block;Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;)F")
    private static void modifyAvailableMoisture(Block block, BlockView world, BlockPos pos, CallbackInfoReturnable<Float> cir) {

    }

    @Inject(at = @At("HEAD"), cancellable = true, method = "canPlaceAt(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/WorldView;Lnet/minecraft/util/math/BlockPos;)Z")
    public void modifyCanPlaceAt(BlockState state, WorldView world, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {

    }*/
}
