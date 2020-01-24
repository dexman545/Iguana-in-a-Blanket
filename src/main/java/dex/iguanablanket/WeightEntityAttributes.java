package dex.iguanablanket;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;

public class WeightEntityAttributes {
    public static final EntityAttribute MAX_WEIGHT = (new ClampedEntityAttribute(null, "generic.max-carry-weight", 100.0D, 0.0D, 1024.0D)).setName("MaxWeight").setTracked(true);
    public static final EntityAttribute WEIGHT = (new ClampedEntityAttribute(null, "generic.weight", 0.0D, 0.0D, 1024.0D)).setName("Weight").setTracked(true);

    public static float getMaxWeight(LivingEntity entity) {
        return (float) entity.getAttributeInstance(MAX_WEIGHT).getValue();
    }

    public static float getWeight(LivingEntity entity) {
        return (float) entity.getAttributeInstance(WEIGHT).getValue();
    }
}
