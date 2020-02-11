package dex.iguanablanket.mixin;

import dex.iguanablanket.IguanaBlanket;
import dex.iguanablanket.impl.EntityHealthChangeCallback;
import dex.iguanablanket.impl.IguanaEntityAttributes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ElytraItem;
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

    @Inject(method = "getJumpVelocity()F", at = @At("HEAD"), cancellable = true)
    private void setJumpHeightModifier(CallbackInfoReturnable<Float> cir) {
        double speed = ((LivingEntity) (Object) this).getAttributes().get(EntityAttributes.MOVEMENT_SPEED).getValue();
        double defaultMovementSpeed = ((LivingEntity) (Object) this).getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).getBaseValue();

        double weight = ((LivingEntity) (Object) this).getAttributeInstance(IguanaEntityAttributes.WEIGHT).getValue();
        double maxWeight = ((LivingEntity) (Object) this).getAttributeInstance(IguanaEntityAttributes.MAX_WEIGHT).getValue();

        if (weight >= maxWeight) {
            cir.cancel();
        } else {
            ((LivingEntity) (Object) this).setVelocity(((LivingEntity) (Object) this).getVelocity().multiply(speed/defaultMovementSpeed, 1D, speed/defaultMovementSpeed));
        }
    }

    @Inject(method = "tick()V", at = @At("TAIL"))
    private void modifyFlight(CallbackInfo ci) {
        if ((!(((LivingEntity) (Object) this).world.isClient)) && IguanaBlanket.cfg.doesWeightEffectElytra()) {
            double weight = ((LivingEntity) (Object) this).getAttributeInstance(IguanaEntityAttributes.WEIGHT).getValue();
            double maxWeight = ((LivingEntity) (Object) this).getAttributeInstance(IguanaEntityAttributes.MAX_WEIGHT).getValue();
            double scale = (weight + maxWeight) / maxWeight;

            if (((LivingEntity) (Object) this).isFallFlying() && !((LivingEntity) (Object) this).onGround) {
                if (weight > maxWeight) {
                    ((LivingEntity) (Object) this).getArmorItems().forEach(t -> {
                        if (t.getItem() instanceof ElytraItem) {
                            t.setDamage(t.getMaxDamage() - 1);
                        }
                    });
                } else {
                    //for making entitied fall faster even with elytra when heavy
                }
            }
        }

    }


    public LivingEntityMixin(EntityType<?> entityType_1, World world_1) {
        super(entityType_1, world_1);
    }

    @Inject(method = "initAttributes()V", at = @At("TAIL"))
    private void initAttributes(CallbackInfo ci) {
        ((LivingEntity) (Object) this).getAttributes().register(IguanaEntityAttributes.MAX_WEIGHT);
        ((LivingEntity) (Object) this).getAttributes().register(IguanaEntityAttributes.WEIGHT);
        ((LivingEntity) (Object) this).getAttributes().register(IguanaEntityAttributes.SUSCEPTIBILITY);
    }

    @Inject(at=@At("INVOKE"), method = "setHealth(F)V", cancellable = true)
    private void entityHealthChange(float health, CallbackInfo ci) {
        ActionResult result = EntityHealthChangeCallback.EVENT.invoker().health(((LivingEntity) (Object) this), health);
        if (result == ActionResult.FAIL) {
            ci.cancel();
        }
    }

}
