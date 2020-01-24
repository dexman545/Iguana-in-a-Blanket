package dex.iguanablanket;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.server.ServerTickCallback;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.PrimitiveIterator;
import java.util.stream.IntStream;

public class IguanaBlanket implements ModInitializer {

	@Override
	public void onInitialize() {
		ServerTickCallback.EVENT.register(t -> {
			for (ServerPlayerEntity player : t.getPlayerManager().getPlayerList()) {
				player.getBlockState().getBlock(); //gets block player is in

				//Item weight calc
				float sum = 0f;
				for (PrimitiveIterator.OfInt it = IntStream.range(0, player.inventory.getInvSize()).iterator(); it.hasNext(); ) {
					int slot = it.next();
					sum += ((ItemWeight) (Object) player.inventory.getInvStack(slot)).getWeight();
				}

				System.out.println(player.getAttributeInstance(WeightEntityAttributes.MAX_WEIGHT).getValue());



				float movementSpeed = 0.1f * ((100f - (sum > 100f ? 90f : sum)) / 100f);
				player.abilities.setWalkSpeed(movementSpeed);
				player.setMovementSpeed(movementSpeed);


			}
		});

	}
}
