package dex.iguanablanket;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;

public interface PlayerInvChangeCallback {
    Event<PlayerInvChangeCallback> EVENT = EventFactory.createArrayBacked(PlayerInvChangeCallback.class,
            (listeners) -> (player, slot, stack) -> {
                for (PlayerInvChangeCallback event : listeners) {
                    ActionResult result = event.change(player, slot, stack);
                    if (result != ActionResult.PASS) {
                        return result;
                    }
                }
                return ActionResult.PASS;
            });

    ActionResult change(PlayerEntity playerEntity, int slot, ItemStack stack);
}
