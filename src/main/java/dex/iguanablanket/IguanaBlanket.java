package dex.iguanablanket;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.server.ServerTickCallback;
import net.minecraft.block.Block;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.DefaultedList;

import java.util.PrimitiveIterator;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

public class IguanaBlanket implements ModInitializer {

	@Override
	public void onInitialize() {
		ServerTickCallback.EVENT.register(t -> {
			for (ServerPlayerEntity player : t.getPlayerManager().getPlayerList()) {
				player.abilities.setWalkSpeed(0.001f);
				player.setMovementSpeed(0.001f);
				player.getBlockState().getBlock(); //gets block player is in

				//Item weight calc
				AtomicReference<Float> sum = new AtomicReference<>(0f);
				for (PrimitiveIterator.OfInt it = IntStream.range(0, player.inventory.getInvSize()).iterator(); it.hasNext(); ) {
					int slot = it.next();
					sum.updateAndGet(v -> (float) (v + ((ItemWeight) (Object) player.inventory.getInvStack(slot)).getWeight()));
					if (Block.getBlockFromItem(player.inventory.getInvStack(slot).getItem()) instanceof ShulkerBoxBlock) {
						//System.out.println(player.inventory.getInvStack(slot).getOrCreateTag());
						DefaultedList<ItemStack> defaultedList = DefaultedList.ofSize(27, ItemStack.EMPTY);
						Inventories.fromTag(player.inventory.getInvStack(slot).getOrCreateTag().getCompound("BlockEntityTag"), defaultedList);
						defaultedList.forEach(stack -> {
							sum.updateAndGet(v -> (float) (v + ((ItemWeight) (Object) stack).getWeight()));
						});
					}
				}
				System.out.println(sum.get());

			}
		});

	}
}
