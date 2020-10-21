package dex.iguanablanket;

import dex.iguanablanket.config.DefaultConfigWriter;
import dex.iguanablanket.config.GenerateData;
import dex.iguanablanket.config.IguanaConfig;
import dex.iguanablanket.config.LuaConfigCompilation;
import dex.iguanablanket.helpers.Data;
import dex.iguanablanket.helpers.ModifierHelper;
import dex.iguanablanket.helpers.SyncHelper;
import dex.iguanablanket.impl.*;
import dex.iguanablanket.mixin.EntityMixin;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.server.ServerStartCallback;
import net.fabricmc.fabric.api.event.server.ServerTickCallback;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.fabricmc.fabric.api.server.PlayerStream;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import org.aeonbits.owner.ConfigFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.PrimitiveIterator;
import java.util.UUID;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class IguanaBlanket implements ModInitializer {
	public static IguanaConfig cfg;
	public static final Identifier IGUANA_CONFIG_PACKET_ID_WEIGHTS = new Identifier("iguana-blanket", "config_weights");
	public static final Identifier IGUANA_CONFIG_PACKET_ID_STACKS = new Identifier("iguana-blanket", "config_stacks");
	public static final Identifier IGUANA_ITEM_POWERED_THROW = new Identifier("iguana-blanket", "item_throw_power");

	public static final Logger logger = LogManager.getLogger(IguanaBlanketClient.modid);

	public static HashMap<UUID, Float> playerDropPower = new HashMap<>();

	static {
		//Config Moved Here to Avoid Null Pointers

		try {
			(new DefaultConfigWriter()).writeDefaultConfig(FabricLoader.getInstance().getConfigDirectory().toString() + "/iguana-blanket/" + "default.lua");
		} catch (IOException e) {
			logger.catching(e);
		}

		//configuration
		String config = FabricLoader.getInstance().getConfigDirectory().toString() + "/iguana.cfg";
		ConfigFactory.setProperty("configDir", config);
		cfg = ConfigFactory.create(IguanaConfig.class);

		//generate config file; removes incorrect values from existing one as well
		try {
			cfg.store(new FileOutputStream(config), "Iguana in a Blanket Configuration File" +
					"\nNote: Default options only effect new entities. Options will reload after ~5 seconds from save.");
		} catch (IOException e) {
			logger.catching(e);
		}
	}

	@Override
	public void onInitialize() {
		ServerTickCallback.EVENT.register(t -> {

			for (ServerPlayerEntity player : t.getPlayerManager().getPlayerList()) {
				//Item weight calc
				float currentWeight = 0f;
				if (!player.isCreative() && !player.isSpectator()) {
					for (PrimitiveIterator.OfInt it = IntStream.range(0, player.inventory.size()).iterator(); it.hasNext(); ) {
						int slot = it.next();
						currentWeight += ((ItemWeight) (Object) player.inventory.getStack(slot)).getWeight();
					}
				}

				ModifierHelper.updateMaxWeight(player);

				player.getAttributeInstance(IguanaEntityAttributes.WEIGHT).setBaseValue(currentWeight);

				double defaultMovementSpeed = player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)
						.getBaseValue();
				double maxWeight = player.getAttributeInstance(IguanaEntityAttributes.MAX_WEIGHT).getValue();
				double deltaMovementSpeed = defaultMovementSpeed
						- (defaultMovementSpeed * ((maxWeight - Math.min(maxWeight, currentWeight)) / maxWeight));

				ModifierHelper.changeMovementSpeed(player, Data.AttributeModifier.ENCUMBRANCE_SLOWDOWN,
						-deltaMovementSpeed);

				// player collapse and elytra break
				if (currentWeight >= maxWeight) {
					player.setSwimming(!cfg.playerOverburdenedDoesPushups());
					((EntityMixin) (Entity) player).callSetPose(EntityPose.SWIMMING);
					if (player.isFallFlying() && !player.isOnGround()) {
						player.getArmorItems().forEach(v -> {
							if (v.getItem() instanceof ElytraItem) {
								v.setDamage(v.getMaxDamage() - 1);
							}
						});
					}
				}

			}
		});

		EntityHealthChangeCallback.EVENT.register(((entity, health) -> {
			float maxHealth = entity.getMaxHealth();
			double susceptibility = entity.getAttributeInstance(IguanaEntityAttributes.SUSCEPTIBILITY).getValue();
			double defaultMovementSpeed = entity.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)
					.getBaseValue();
			double deltaMovementSpeed = susceptibility * (defaultMovementSpeed * ((maxHealth - Math.min(maxHealth, health)) / maxHealth));

			ModifierHelper.changeMovementSpeed(entity, Data.AttributeModifier.HEALTH_SLOWDOWN, -deltaMovementSpeed);

			return ActionResult.PASS;

		}));

		GenerateData gen = new GenerateData();

		ServerStartCallback.EVENT.register(minecraftServer -> {
			LuaConfigCompilation.threadedmain(FabricLoader.getInstance().getConfigDirectory().toString() + "/iguana-blanket/" + cfg.luaConfig(), gen.genDefaultsTables());
		});

		PlayerJoinCallback.EVENT.register(playerEntity -> {
			SyncHelper.syncData(playerEntity, LuaConfigCompilation.syncedData);

			return ActionResult.PASS;
		});

		ServerReloadCallback.EVENT.register(t -> {
			HashMap<String, HashMap> syncData = LuaConfigCompilation.threadedmain(FabricLoader.getInstance().getConfigDirectory().toString() + "/iguana-blanket/" + cfg.luaConfig(), gen.genDefaultsTables());
			Stream<ServerPlayerEntity> players = PlayerStream.all(t);

			//send the data
			players.forEach(serverPlayerEntity -> {
				SyncHelper.syncData(serverPlayerEntity, syncData);
			});
		});

		ServerSidePacketRegistry.INSTANCE.register(IGUANA_ITEM_POWERED_THROW, (context, data) -> {
			float power = data.readFloat();
			context.getTaskQueue().execute(() -> {
				if (context.getPlayer().isAlive()&& power > 0 && !context.getPlayer().getMainHandStack().equals(ItemStack.EMPTY)) {
					playerDropPower.put(context.getPlayer().getUuid(), Math.min(power, cfg.maxThrowFactor()));
					context.getPlayer().dropSelectedItem(false);
				}
			});
		});

		//Register Attributes (New in 1.16?)
		Registry.register(Registry.ATTRIBUTE, "generic.max-carry-weight", IguanaEntityAttributes.MAX_WEIGHT);
		Registry.register(Registry.ATTRIBUTE, "generic.weight", IguanaEntityAttributes.WEIGHT);
		Registry.register(Registry.ATTRIBUTE, "generic.susceptibility", IguanaEntityAttributes.SUSCEPTIBILITY);
	}
}
