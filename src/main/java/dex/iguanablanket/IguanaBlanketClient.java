package dex.iguanablanket;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.fabricmc.fabric.api.event.server.ServerTickCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import spinnery.util.InGameHudScreen;
import spinnery.widget.*;

public class IguanaBlanketClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		InGameHudScreen.addOnInitialize(() -> {
			// WInterfaceHolder
			WInterfaceHolder holder = InGameHudScreen.getHolder();
			// WInterfaceHolder



			// WInterface
			WInterface mainInterface = new WInterface(WPosition.of(WType.FREE, 8, 8, 0));

			holder.add(mainInterface);
			// WInterface



			// WStaticText
			WStaticText staticTextA = new WStaticText(WPosition.of(WType.ANCHORED, 0, 0, 0, mainInterface), mainInterface, new LiteralText("StaticText A"));
			// WStaticText



			mainInterface.add(staticTextA);
		});
		ClientTickCallback.EVENT.register(t -> {

		});
	}
}
