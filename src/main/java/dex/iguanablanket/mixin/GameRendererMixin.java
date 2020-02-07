package dex.iguanablanket.mixin;

import dex.iguanablanket.helpers.Data;
import dex.iguanablanket.helpers.MathHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    //Prevents item weight making FOV do wild changes
    @Inject(at=@At("RETURN"), method = "getFov(Lnet/minecraft/client/render/Camera;FZ)D", cancellable = true)
    public void fovFix(Camera camera, float tickDelta, boolean changingFov, CallbackInfoReturnable<Double> cir) {
        double stdFov = MinecraftClient.getInstance().options.fov;
        Double x = cir.getReturnValue();
        cir.setReturnValue(MathHelper.clampValue(x, stdFov * (1 - Data.fovClamp), stdFov * (1 + Data.fovClamp)));
    }
}
