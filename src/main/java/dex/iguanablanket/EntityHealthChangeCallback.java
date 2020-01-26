package dex.iguanablanket;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ActionResult;

public interface EntityHealthChangeCallback {
    Event<EntityHealthChangeCallback> EVENT = EventFactory.createArrayBacked(EntityHealthChangeCallback.class,
            (listeners) -> (entity, health) -> {
                for (EntityHealthChangeCallback event : listeners) {
                    ActionResult result = event.health(entity, health);
                    if (result != ActionResult.PASS) {
                        return result;
                    }
                }
                return ActionResult.PASS;
            });

    ActionResult health(LivingEntity entity, float health);
}
