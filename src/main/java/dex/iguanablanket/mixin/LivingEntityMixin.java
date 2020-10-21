package dex.iguanablanket.mixin;

import dex.iguanablanket.IguanaBlanket;
import dex.iguanablanket.impl.EntityHealthChangeCallback;
import dex.iguanablanket.impl.IguanaEntityAttributes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ElytraItem;
import net.minecraft.tag.FluidTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    @ModifyConstant(method = "jump()V", constant = @Constant(floatValue = 0.2F))
    private float setHJumpModifier(float m) {
        double speed = ((LivingEntity) (Object) this).getAttributes().getCustomInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)
                .getValue();
        double defaultMovementSpeed = ((LivingEntity) (Object) this)
                .getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).getBaseValue();

        return (float) (m * (speed / defaultMovementSpeed));
    }

    @Inject(method = "getJumpVelocity()F", at = @At("HEAD"), cancellable = true)
    private void setJumpHeightModifier(CallbackInfoReturnable<Float> cir) {
        double speed = ((LivingEntity) (Object) this).getAttributes().getCustomInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)
                .getValue();
        double defaultMovementSpeed = ((LivingEntity) (Object) this)
                .getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).getBaseValue();

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

            if (((LivingEntity) (Object) this).isFallFlying() && !((LivingEntity) (Object) this).isOnGround()) {
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

    // @Inject(method = "initAttributes()V", at = @At("TAIL"))
    // private void initAttributes(CallbackInfo ci) {
    //     ((LivingEntity) (Object) this).getAttributes().register(IguanaEntityAttributes.MAX_WEIGHT);
    //     ((LivingEntity) (Object) this).getAttributes().register(IguanaEntityAttributes.WEIGHT);
    //     ((LivingEntity) (Object) this).getAttributes().register(IguanaEntityAttributes.SUSCEPTIBILITY);
    // }

    @Inject(method = "createLivingAttributes", at = @At("RETURN"))
    private static void appendAttributes(CallbackInfoReturnable<DefaultAttributeContainer.Builder> cir) {
        cir.getReturnValue().add(IguanaEntityAttributes.MAX_WEIGHT).add(IguanaEntityAttributes.WEIGHT).add(IguanaEntityAttributes.SUSCEPTIBILITY);
    }

    @Inject(at=@At("INVOKE"), method = "setHealth(F)V", cancellable = true)
    private void entityHealthChange(float health, CallbackInfo ci) {
        ActionResult result = EntityHealthChangeCallback.EVENT.invoker().health(((LivingEntity) (Object) this), health);
        if (result == ActionResult.FAIL) {
            ci.cancel();
        }
    }

    @Inject(method = "swimUpward(Lnet/minecraft/tag/Tag;)V", at = @At("TAIL"), cancellable = true)
    private void nerfSwimUpward(Tag<Fluid> fluid, CallbackInfo ci) {
        if (IguanaBlanket.cfg.doesWeightEffectSwimming()) {
            double weight = ((LivingEntity) (Object) this).getAttributeInstance(IguanaEntityAttributes.WEIGHT).getValue();
            double maxWeight = ((LivingEntity) (Object) this).getAttributeInstance(IguanaEntityAttributes.MAX_WEIGHT).getValue();
            double scale = ((weight + maxWeight) / maxWeight) - 1;
            Vec3d v = ((LivingEntity) (Object) this).getVelocity();
            ((LivingEntity) (Object) this).setVelocity(v.add(0, -scale*v.y*1.3, 0));
        }

    }

    @ModifyVariable(method = "travel(Lnet/minecraft/util/math/Vec3d;)V", index = 2, slice = @Slice(from = @At(value = "INVOKE"),
            to = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;hasStatusEffect(Lnet/minecraft/entity/effect/StatusEffect;)Z")),
            at = @At("INVOKE"))
    private double modifyFall(double d) {
        double weight = ((LivingEntity) (Object) this).getAttributeInstance(IguanaEntityAttributes.WEIGHT).getValue();
        double maxWeight = ((LivingEntity) (Object) this).getAttributeInstance(IguanaEntityAttributes.MAX_WEIGHT).getValue();
        double scale = ((weight + maxWeight) / maxWeight);
        if (((LivingEntity) (Object) this).isFallFlying()) {
            return d * scale;
        }
        return d;
    }

}
