package dex.iguanablanket;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.server.ServerTickCallback;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.PrimitiveIterator;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

public class IguanaBlanket implements ModInitializer {

	@Override
	public void onInitialize() {
		ServerTickCallback.EVENT.register(t -> {
			for (ServerPlayerEntity player : t.getPlayerManager().getPlayerList()) {
				player.getBlockState().getBlock(); //gets block player is in

				//Item weight calc
				AtomicReference<Float> sum = new AtomicReference<>(0f);
				for (PrimitiveIterator.OfInt it = IntStream.range(0, player.inventory.getInvSize()).iterator(); it.hasNext(); ) {
					int slot = it.next();
					sum.updateAndGet(v -> (float) (v + ((ItemWeight) (Object) player.inventory.getInvStack(slot)).getWeight()));
					/*if (Block.getBlockFromItem(player.inventory.getInvStack(slot).getItem()) instanceof ShulkerBoxBlock) {
						DefaultedList<ItemStack> defaultedList = DefaultedList.ofSize(27, ItemStack.EMPTY);
						Inventories.fromTag(player.inventory.getInvStack(slot).getOrCreateTag().getCompound("BlockEntityTag"), defaultedList);
						defaultedList.forEach(stack -> {
							sum.updateAndGet(v -> (float) (v + ((ItemWeight) (Object) stack).getWeight()));
						});
					}*/
				}

				player.abilities.setWalkSpeed(0.1f * ((100f - (sum.get() > 100f ? 90f : sum.get())) / 100f));
				player.setMovementSpeed(0.1f * ((100f - (sum.get() > 100f ? 90f : sum.get())) / 100f));


			}
		});

	}
}
