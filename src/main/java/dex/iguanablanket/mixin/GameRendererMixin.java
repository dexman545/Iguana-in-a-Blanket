package dex.iguanablanket.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {

    public double clampValue(double value, double min, double max) {
        return Math.min(Math.max(value, min), max);
    }

    //Prevents item weight making FOV do wild changes
    @Inject(at=@At("RETURN"), method = "getFov(Lnet/minecraft/client/render/Camera;FZ)D", cancellable = true)
    public void fovFix(Camera camera, float tickDelta, boolean changingFov, CallbackInfoReturnable<Double> cir) {
        double stdFov = MinecraftClient.getInstance().options.fov;
        Double x = cir.getReturnValue();
        cir.setReturnValue(clampValue(x, stdFov * 0.9, stdFov * 1.1));
    }
}
