package dex.iguanablanket;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dex.iguanablanket.config.LuaConfigCompilation;
import dex.iguanablanket.impl.IguanaEntityAttributes;
import dex.iguanablanket.mixin.EntityMixin;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding;
import net.fabricmc.fabric.api.client.keybinding.KeyBindingRegistry;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.packet.PlayerActionC2SPacket;
import net.minecraft.server.network.packet.PlayerInputC2SPacket;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.apache.commons.lang3.mutable.MutableFloat;
import spinnery.client.InGameHudScreen;
import spinnery.common.BaseContainer;
import spinnery.widget.*;
import spinnery.widget.api.Position;
import spinnery.widget.api.Size;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Objects;

public class IguanaBlanketClient implements ClientModInitializer {
	static String modid = "iguana-blanket";
	public static float power = 0;

	// WInterface
	WInterface mainInterface = new WInterface();

	Identifier[] textures = new Identifier[5];

	WDynamicImage encumbrance = new WDynamicImage();

	WHorizontalBar toss = new WHorizontalBar();


	private void genTextures() {
		for (int i = 0; i < 5; i++) {
			textures[i] = (new Identifier(modid, "textures/encumbrance_000" + i +".png"));
		}

		/*
		Image translation:
		轻 light 0
		羽 feather 1
		难 difficult 2
		大 heavy 3
		侐 immobile 4
		*/

		encumbrance.setTextures(textures);

	}

	@Override
	public void onInitializeClient() {
		toss.setHidden(true);

		genTextures();

		InGameHudScreen.addOnInitialize(() -> {
			WInterface x = InGameHudScreen.getInterface();
			encumbrance.setSize(Size.of(15, 15));
			encumbrance.setPosition(Position.of(0,0,0));
			x.add(encumbrance);

			toss.setSize(Size.of(50, 10));
			toss.setPosition(Position.of(0,30,0));
			toss.setLimit(new MutableFloat(IguanaBlanket.cfg.maxThrowFactor()));
			x.add(toss);

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

			if (MinecraftClient.getInstance() != null) {
				if ((MinecraftClient.getInstance().options.keyDrop.isPressed() || MinecraftClient.getInstance().options.keyDrop.wasPressed()) && !player.getMainHandStack().equals(ItemStack.EMPTY)) {
					power++;
					toss.setHidden(false);
					toss.setProgress(new MutableFloat(Math.max(1, Math.min(power / 4, IguanaBlanket.cfg.maxThrowFactor()))));

				} else if (power != 0) {
					power = power / 4;
					power = Math.min(power, IguanaBlanket.cfg.maxThrowFactor());
					power = Math.max(power, 1);
					PacketByteBuf data = new PacketByteBuf(Unpooled.buffer());
					data.writeFloat(power);
					ClientSidePacketRegistry.INSTANCE.sendToServer(IguanaBlanket.IGUANA_ITEM_POWERED_THROW, data);
					power = 0f;
					toss.setHidden(true);
				}
			}




		});

		//Listen for config packet from server
		ClientSidePacketRegistry.INSTANCE.register(IguanaBlanket.IGUANA_CONFIG_PACKET_ID_WEIGHTS,
				((packetContext, packetByteBuf) -> {
					CompoundTag x = packetByteBuf.readCompoundTag();
					Gson gson = new Gson();
					Type m = new TypeToken<HashMap<String, Float>>() {}.getType();
					HashMap<String, Float> k = gson.fromJson(Objects.requireNonNull(x).asString(), m);

					packetContext.getTaskQueue().execute(() -> {
						try {
							//clear client data
							LuaConfigCompilation.weights.clear();

							LuaConfigCompilation.weights.putAll(k);

						} catch (Exception e) {
							IguanaBlanket.logger.catching(e);
						}

					});
				}));

		ClientSidePacketRegistry.INSTANCE.register(IguanaBlanket.IGUANA_CONFIG_PACKET_ID_STACKS,
				((packetContext, packetByteBuf) -> {
					CompoundTag x = packetByteBuf.readCompoundTag();
					Gson gson = new Gson();
					Type m = new TypeToken<HashMap<String, Integer>>() {}.getType();
					HashMap<String, Integer> k = gson.fromJson(Objects.requireNonNull(x).asString(), m);

					packetContext.getTaskQueue().execute(() -> {
						try {
							//clear client data
							LuaConfigCompilation.stacksizes.clear();

							LuaConfigCompilation.stacksizes.putAll(k);

						} catch (Exception e) {
							IguanaBlanket.logger.catching(e);
						}

					});
				}));

	}
}
