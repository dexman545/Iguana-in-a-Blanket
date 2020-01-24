package dex.iguanablanket.mixin;

import dex.iguanablanket.WeightEntityAttributes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    @ModifyConstant(method = "jump()V", constant = @Constant(floatValue = 0.2F))
    private float setHJumpModifier(float m) {
        System.out.println(m);
        return 0.0F;
    }

    public LivingEntityMixin(EntityType<?> entityType_1, World world_1) {
        super(entityType_1, world_1);
    }

    @Inject(method = "initAttributes()V", at = @At("TAIL"))
    private void initAttributes(CallbackInfo ci) {
        ((LivingEntity) (Object) this).getAttributes().register(WeightEntityAttributes.MAX_WEIGHT);
        ((LivingEntity) (Object) this).getAttributes().register(WeightEntityAttributes.WEIGHT);
    }
}
