package dex.iguanablanket;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ActionResult;

public interface StatusEffectChangeCallback {
    Event<StatusEffectChangeCallback> EVENT = EventFactory.createArrayBacked(StatusEffectChangeCallback.class,
            (listeners) -> (entity) -> {
                for (StatusEffectChangeCallback event : listeners) {
                    ActionResult result = event.effectChange(entity);
                    if (result != ActionResult.PASS) {
                        return result;
                    }
                }
                return ActionResult.PASS;
            });

    ActionResult effectChange(LivingEntity entity);
}
