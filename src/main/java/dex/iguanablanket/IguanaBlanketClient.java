package dex.iguanablanket;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.fabricmc.fabric.api.event.server.ServerTickCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import spinnery.util.InGameHudScreen;
import spinnery.widget.*;

import java.util.ArrayList;

public class IguanaBlanketClient implements ClientModInitializer {
	static String modid = "iguana-blanket";

	// WInterface
	WInterface mainInterface = new WInterface(WPosition.of(WType.FREE, 8, 8, 0));


	Identifier[] textures = new Identifier[11];

	WDynamicImage encumbrance = new WDynamicImage(WPosition.of(WType.ANCHORED, 0, 0, 0, mainInterface),
			WSize.of(25, 25), mainInterface);

	private void genTextures() {
		for (int i = 1; i <= 9; i++) {
			textures[i-1] = (new Identifier(modid, "textures/" + i +".png"));
		}

		encumbrance.setTextures(textures);

	}

	@Override
	public void onInitializeClient() {

		genTextures();


		InGameHudScreen.addOnInitialize(() -> {
			// WInterfaceHolder
			WInterfaceHolder holder = InGameHudScreen.getHolder();
			// WInterfaceHolder


			holder.add(mainInterface);

			// WStaticText
			//WStaticText staticTextA = new WStaticText(WPosition.of(WType.ANCHORED, 0, 0, 0, mainInterface), mainInterface, new LiteralText("StaticText A"));
			// WStaticText

			//mainInterface.add(staticTextA);
			mainInterface.add(encumbrance);
		});

		ClientTickCallback.EVENT.register(t -> {
			ClientPlayerEntity player = t.player;

			double weight = 0;
			double maxWeight = 0;
			if (player != null) {
				weight = player.getAttributeInstance(IguanaEntityAttributes.WEIGHT).getValue();
				maxWeight = player.getAttributeInstance(IguanaEntityAttributes.MAX_WEIGHT).getValue();
			}

			int value = (int) Math.round(10 * (Math.min(weight, maxWeight) / maxWeight));

			encumbrance.setCurrentImage(Math.min(value, 8));


		});
	}
}
