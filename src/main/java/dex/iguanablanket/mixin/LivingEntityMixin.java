package dex.iguanablanket.mixin;

import dex.iguanablanket.EntityHealthChangeCallback;
import dex.iguanablanket.IguanaEntityAttributes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    @ModifyConstant(method = "jump()V", constant = @Constant(floatValue = 0.2F))
    private float setHJumpModifier(float m) {
        double speed = ((LivingEntity) (Object) this).getAttributes().get(EntityAttributes.MOVEMENT_SPEED).getValue();
        double defaultMovementSpeed = ((LivingEntity) (Object) this).getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).getBaseValue();

        return (float) (m * (speed/defaultMovementSpeed));
    }

    @Inject(method = "getJumpVelocity()F", at = @At("HEAD"))
    private void setJumpHeightModifier(CallbackInfoReturnable<Float> cir) {
        double speed = ((LivingEntity) (Object) this).getAttributes().get(EntityAttributes.MOVEMENT_SPEED).getValue();
        double defaultMovementSpeed = ((LivingEntity) (Object) this).getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).getBaseValue();

        ((LivingEntity) (Object) this).setVelocity(((LivingEntity) (Object) this).getVelocity().multiply(speed/defaultMovementSpeed, 1D, speed/defaultMovementSpeed));

    }


    public LivingEntityMixin(EntityType<?> entityType_1, World world_1) {
        super(entityType_1, world_1);
    }

    @Inject(method = "initAttributes()V", at = @At("TAIL"))
    private void initAttributes(CallbackInfo ci) {
        ((LivingEntity) (Object) this).getAttributes().register(IguanaEntityAttributes.MAX_WEIGHT);
        ((LivingEntity) (Object) this).getAttributes().register(IguanaEntityAttributes.WEIGHT);
    }

    @Inject(at=@At("INVOKE"), method = "setHealth(F)V", cancellable = true)
    private void playerInvChange(float health, CallbackInfo ci) {
        ActionResult result = EntityHealthChangeCallback.EVENT.invoker().health(((LivingEntity) (Object) this), health);
        if (result == ActionResult.FAIL) {
            ci.cancel();
        }
    }
}
