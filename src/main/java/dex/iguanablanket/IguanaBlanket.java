package dex.iguanablanket;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.server.ServerTickCallback;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;

import java.util.PrimitiveIterator;
import java.util.stream.IntStream;

public class IguanaBlanket implements ModInitializer {

	@Override
	public void onInitialize() {

		Storage store = new Storage();

		ServerTickCallback.EVENT.register(t -> {
			//disabled as the PlayerInventoryChange callback event fails to update player for unknown reason
			/*store.test.forEach((s, playerEntityDoubleTuple) -> {
				ModifierHelper.changeMovementSpeed(playerEntityDoubleTuple.x, Data.AttributeModifier.ENCUMBRANCE_SLOWDOWN, -playerEntityDoubleTuple.y);
				playerEntityDoubleTuple.x.sendAbilitiesUpdate();
				playerEntityDoubleTuple.x.updateNeeded = true;
			});
			store.test.clear();*/

			for (ServerPlayerEntity player : t.getPlayerManager().getPlayerList()) {
				//Item weight calc
				float currentWeight = 0f;
				for (PrimitiveIterator.OfInt it = IntStream.range(0, player.inventory.getInvSize()).iterator(); it.hasNext(); ) {
					int slot = it.next();
					currentWeight += ((ItemWeight) (Object) player.inventory.getInvStack(slot)).getWeight();
				}

				//System.out.println(player.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).getValue());
				player.getAttributeInstance(IguanaEntityAttributes.WEIGHT).setBaseValue(currentWeight);

				double defaultMovementSpeed = player.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).getBaseValue();
				double maxWeight = player.getAttributeInstance(IguanaEntityAttributes.MAX_WEIGHT).getValue();
				double deltaMovementSpeed = defaultMovementSpeed - (defaultMovementSpeed * ((maxWeight - Math.min(maxWeight, currentWeight)) / maxWeight));


				ModifierHelper.changeMovementSpeed(player, Data.AttributeModifier.ENCUMBRANCE_SLOWDOWN, -deltaMovementSpeed);

			}
		});

		EntityHealthChangeCallback.EVENT.register(((entity, health) -> {
			float maxHealth = entity.getMaximumHealth();
			double defaultMovementSpeed = entity.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).getBaseValue();
			double deltaMovementSpeed = (defaultMovementSpeed * ((maxHealth - Math.min(maxHealth, health)) / maxHealth));
			if ((entity instanceof PlayerEntity)) {
				//System.out.println(deltaMovementSpeed);
			}

			ModifierHelper.changeMovementSpeed(entity, Data.AttributeModifier.HEALTH_SLOWDOWN, -deltaMovementSpeed);

			return ActionResult.PASS;

		}));

		PlayerInvChangeCallback.EVENT.register((player, slot, stack) -> {
			/*float currentWeight = 0f;
			for (PrimitiveIterator.OfInt it = IntStream.range(0, player.inventory.getInvSize()).iterator(); it.hasNext(); ) {
				int nSlot = it.next();

				//fix weight not updating when it should
				if (nSlot == slot) {
					if (player.inventory.getInvStack(nSlot).getItem().equals(ItemStack.EMPTY.getItem())) {
						currentWeight += ((ItemWeight) (Object) stack).getWeight();
					}
				}

				currentWeight += ((ItemWeight) (Object) player.inventory.getInvStack(nSlot)).getWeight();

			}

			player.getAttributeInstance(IguanaEntityAttributes.WEIGHT).setBaseValue(currentWeight);

			double defaultMovementSpeed = player.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).getBaseValue();
			double maxWeight = player.getAttributeInstance(IguanaEntityAttributes.MAX_WEIGHT).getValue();
			double deltaMovementSpeed = defaultMovementSpeed - (defaultMovementSpeed * ((maxWeight - Math.min(maxWeight, currentWeight)) / maxWeight));
			//System.out.println(deltaMovementSpeed);

			store.test.put(player.getUuidAsString(), new Storage.Tuple(player, deltaMovementSpeed));
			//ModifierHelper.changeMovementSpeed(player, Data.AttributeModifier.ENCUMBRANCE_SLOWDOWN, -deltaMovementSpeed);

			return ActionResult.PASS;*/
			return ActionResult.PASS;
		});

	}
}
