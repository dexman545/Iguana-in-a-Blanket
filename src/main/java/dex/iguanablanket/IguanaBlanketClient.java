package dex.iguanablanket;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.fabricmc.fabric.api.event.server.ServerTickCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.network.ServerPlayerEntity;

public class IguanaBlanketClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		ClientTickCallback.EVENT.register(t -> {
			t.options.fov = MinecraftClient.getInstance().options.fov;
		});
	}
}
