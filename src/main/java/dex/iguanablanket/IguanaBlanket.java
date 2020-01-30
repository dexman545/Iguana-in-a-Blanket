package dex.iguanablanket;

import dex.iguanablanket.mixin.EntityMixin;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.server.ServerTickCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ElytraItem;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import org.aeonbits.owner.ConfigFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.PrimitiveIterator;
import java.util.stream.IntStream;

public class IguanaBlanket implements ModInitializer {
	static IguanaConfig cfg;

	@Override
	public void onInitialize() {
		//configuration
		String config = FabricLoader.getInstance().getConfigDirectory().toString() + "/iguana.cfg";
		ConfigFactory.setProperty("configDir", config);
		cfg = ConfigFactory.create(IguanaConfig.class);

		//generate config file; removes incorrect values from existing one as well
		try {
			cfg.store(new FileOutputStream(config), "Iguana in a Blanket Configuration File" +
					"\nNote: Default options only effect new entities. Options will reload after ~5 seconds from save.");
		} catch (IOException e) {
			e.printStackTrace();
		}

		ServerTickCallback.EVENT.register(t -> {

			for (ServerPlayerEntity player : t.getPlayerManager().getPlayerList()) {
				//Item weight calc
				float currentWeight = 0f;
				for (PrimitiveIterator.OfInt it = IntStream.range(0, player.inventory.getInvSize()).iterator(); it.hasNext(); ) {
					int slot = it.next();
					currentWeight += ((ItemWeight) (Object) player.inventory.getInvStack(slot)).getWeight();
				}

				player.getAttributeInstance(IguanaEntityAttributes.WEIGHT).setBaseValue(currentWeight);

				double defaultMovementSpeed = player.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).getBaseValue();
				double maxWeight = player.getAttributeInstance(IguanaEntityAttributes.MAX_WEIGHT).getValue();
				double deltaMovementSpeed = defaultMovementSpeed - (defaultMovementSpeed * ((maxWeight - Math.min(maxWeight, currentWeight)) / maxWeight));

				ModifierHelper.changeMovementSpeed(player, Data.AttributeModifier.ENCUMBRANCE_SLOWDOWN, -deltaMovementSpeed);

				//player collapse and elytra break
				if (currentWeight >= maxWeight) {
					player.setSwimming(!cfg.playerOverburdenedDoesPushups());
					((EntityMixin)(Entity)player).callSetPose(EntityPose.SWIMMING);
					if (player.isFallFlying() && !player.onGround) {
						player.getArmorItems().forEach(v -> {
							if (v.getItem() instanceof ElytraItem) {
								v.setDamage(431);
							}
						});
					}
				}

			}
		});

		EntityHealthChangeCallback.EVENT.register(((entity, health) -> {
			float maxHealth = entity.getMaximumHealth();
			double susceptibility = entity.getAttributeInstance(IguanaEntityAttributes.SUSCEPTIBILITY).getValue();
			double defaultMovementSpeed = entity.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).getBaseValue();
			double deltaMovementSpeed = susceptibility * (defaultMovementSpeed * ((maxHealth - Math.min(maxHealth, health)) / maxHealth));

			ModifierHelper.changeMovementSpeed(entity, Data.AttributeModifier.HEALTH_SLOWDOWN, -deltaMovementSpeed);

			return ActionResult.PASS;

		}));

	}
}
