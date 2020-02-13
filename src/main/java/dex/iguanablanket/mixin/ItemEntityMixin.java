package dex.iguanablanket.mixin;

import dex.iguanablanket.config.LuaConfigCompilation;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

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
}
