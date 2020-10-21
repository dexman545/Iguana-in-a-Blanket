package dex.iguanablanket.impl;

import dex.iguanablanket.IguanaBlanket;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;

public class IguanaEntityAttributes {
    public static final EntityAttribute MAX_WEIGHT = (new ClampedEntityAttribute("generic.max-carry-weight", IguanaBlanket.cfg.defaultEntityMaxWeight() >= 1 ? IguanaBlanket.cfg.defaultEntityMaxWeight() : 1, 1.0D, 1024.0D)).setTracked(true);
    public static final EntityAttribute WEIGHT = (new ClampedEntityAttribute("generic.weight", 0.0D, 0.0D, 1024.0D)).setTracked(true);
    public static final EntityAttribute SUSCEPTIBILITY = (new ClampedEntityAttribute("generic.susceptibility", IguanaBlanket.cfg.defaultEntitySusceptibility(), 0.0D, 1024.0D)).setTracked(true);

    public static float getMaxWeight(LivingEntity entity) {
        return (float) entity.getAttributeInstance(MAX_WEIGHT).getValue();
    }

    public static float getWeight(LivingEntity entity) {
        return (float) entity.getAttributeInstance(WEIGHT).getValue();
    }
}
