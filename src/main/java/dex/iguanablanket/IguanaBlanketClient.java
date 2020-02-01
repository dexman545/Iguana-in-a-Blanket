package dex.iguanablanket;

import dex.iguanablanket.mixin.EntityMixin;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.fabricmc.fabric.api.event.server.ServerTickCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
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

	Identifier[] textures = new Identifier[5];

	WDynamicImage encumbrance = new WDynamicImage(WPosition.of(WType.ANCHORED, 0, 0, 0, mainInterface),
			WSize.of(15, 15), mainInterface);


	private void genTextures() {
		for (int i = 0; i < 5; i++) {
			textures[i] = (new Identifier(modid, "textures/encumbrance_000" + i +".png"));
		}

		/*
		Image translation:
		轻 light 0
		侐 immobile 4
		大 heavy 3
		难 difficult 2
		羽 feather 1
		*/

		encumbrance.setTextures(textures);

	}

	@Override
	public void onInitializeClient() {

		genTextures();

		InGameHudScreen.addOnInitialize(() -> {
			WInterfaceHolder holder = InGameHudScreen.getHolder();
			holder.add(mainInterface);

			mainInterface.add(encumbrance);
		});

		ClientTickCallback.EVENT.register(t -> {
			ClientPlayerEntity player = t.player;

			double weight = 0;
			double maxWeight = 0;
			if (player != null) {
				weight = player.getAttributeInstance(IguanaEntityAttributes.WEIGHT).getValue();
				maxWeight = player.getAttributeInstance(IguanaEntityAttributes.MAX_WEIGHT).getValue();

				if (weight >= maxWeight) {
					((EntityMixin)(Entity)player).callSetPose(EntityPose.SWIMMING);
				}
			}

			int value = (int) Math.floor(4 * (Math.min(weight, maxWeight) / maxWeight));

			encumbrance.setCurrentImage(value);
			encumbrance.setHidden(!IguanaBlanket.cfg.displayEncumbranceIcon());


		});
	}
}
