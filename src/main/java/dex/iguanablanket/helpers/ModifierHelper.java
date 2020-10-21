package dex.iguanablanket.helpers;

import dex.iguanablanket.IguanaBlanket;
import dex.iguanablanket.impl.IguanaEntityAttributes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

import java.util.Optional;

public class ModifierHelper {

    public static void changeMovementSpeed(LivingEntity entity, Data.AttributeModifier modifier, double value) {
        changeEntityAttribute(entity, modifier, value, EntityAttributes.GENERIC_MOVEMENT_SPEED);

    }

    public static void changeMaxWeight(LivingEntity entity, Data.AttributeModifier modifier, double value) {
        changeEntityAttribute(entity, modifier, value, IguanaEntityAttributes.MAX_WEIGHT);

    }

    private static void changeEntityAttribute(LivingEntity entity, Data.AttributeModifier modifier, double value, EntityAttribute attribute) {
        //Reset modifier
        if (entity.getAttributes().getCustomInstance(attribute).getModifier(modifier.getID()) != null) {
            entity.getAttributes().getCustomInstance(attribute).removeModifier(modifier.getID());
        }

        //Apply new modifier
        EntityAttributeModifier newAttributeModifier = new EntityAttributeModifier(modifier.getID(), modifier.toString(), value, EntityAttributeModifier.Operation.ADDITION);
        entity.getAttributes().getCustomInstance(attribute).addPersistentModifier(newAttributeModifier);

    }

    public static void updateMaxWeight(LivingEntity entity) {
        double maxWeight = entity.getAttributeInstance(IguanaEntityAttributes.MAX_WEIGHT).getBaseValue();
        int strengthAmplifier;
        int weaknessAmplifier;

        Optional<StatusEffectInstance> strength = Optional.ofNullable(entity.getStatusEffect(StatusEffects.STRENGTH));
        Optional<StatusEffectInstance> weakness = Optional.ofNullable(entity.getStatusEffect(StatusEffects.WEAKNESS));

        strengthAmplifier = strength.map(StatusEffectInstance::getAmplifier).orElse(-1) + 1;
        weaknessAmplifier = weakness.map(StatusEffectInstance::getAmplifier).orElse(-1) + 1;

        double deltaMaxWeight = IguanaBlanket.cfg.potionEffectWeightScaleFactor() * (strengthAmplifier - weaknessAmplifier) * maxWeight;

        changeMaxWeight(entity, Data.AttributeModifier.STRENGTH, deltaMaxWeight);
    }

}
