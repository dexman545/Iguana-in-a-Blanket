package dex.iguanablanket;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;

public class ModifierHelper {
    public static void changeMovementSpeed(LivingEntity entity, Data.AttributeModifier modifier, double value) {
        //Reset modifier
        if (entity.getAttributes().get(EntityAttributes.MOVEMENT_SPEED).getModifier(modifier.getID()) != null) {
            entity.getAttributes().get(EntityAttributes.MOVEMENT_SPEED).removeModifier(modifier.getID());
        }

        //Apply new modifier
        EntityAttributeModifier newAttributeModifier = new EntityAttributeModifier(modifier.getID(), modifier.toString(), value, EntityAttributeModifier.Operation.ADDITION);
        entity.getAttributes().get(EntityAttributes.MOVEMENT_SPEED).addModifier(newAttributeModifier);

    }

}
