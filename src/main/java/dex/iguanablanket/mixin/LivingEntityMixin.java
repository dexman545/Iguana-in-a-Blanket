package dex.iguanablanket.mixin;

import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @ModifyConstant(method = "jump()V", constant = @Constant(floatValue = 0.2F))
    private float setHJumpModifier(float m) {
        System.out.println("test");
        return 0.0F;
    }
}
