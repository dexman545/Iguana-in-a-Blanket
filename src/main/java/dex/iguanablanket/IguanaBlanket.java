package dex.iguanablanket;

import dex.iguanablanket.mixin.EntityMixin;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.server.ServerStartCallback;
import net.fabricmc.fabric.api.event.server.ServerTickCallback;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ElytraItem;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.registry.Registry;
import org.aeonbits.owner.ConfigFactory;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

import java.io.*;
import java.util.PrimitiveIterator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class IguanaBlanket implements ModInitializer {
	static IguanaConfig cfg;

	@Override
	public void onInitialize() {
		try {
			(new DefaultConfigWriter()).writeDefaultConfig(FabricLoader.getInstance().getConfigDirectory().toString() + "/default.lua");
		} catch (IOException e) {
			e.printStackTrace();
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
			e.printStackTrace();
		}

		ServerTickCallback.EVENT.register(t -> {

			for (ServerPlayerEntity player : t.getPlayerManager().getPlayerList()) {
				//Item weight calc
				float currentWeight = 0f;
				if (!player.isCreative() && !player.isSpectator()) {
					for (PrimitiveIterator.OfInt it = IntStream.range(0, player.inventory.getInvSize()).iterator(); it.hasNext(); ) {
						int slot = it.next();
						currentWeight += ((ItemWeight) (Object) player.inventory.getInvStack(slot)).getWeight();
					}
				}

				ModifierHelper.updateMaxWeight(player);

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
								v.setDamage(v.getMaxDamage() - 1);
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


		ServerStartCallback.EVENT.register(minecraftServer -> {
			LuaConfigCompilation.threadedmain(FabricLoader.getInstance().getConfigDirectory().toString() + "/" + cfg.luaConfig(), genDefaultsTables());
		});

		ServerReloadCallback.EVENT.register(t -> {
			LuaConfigCompilation.threadedmain(FabricLoader.getInstance().getConfigDirectory().toString() + "/" + cfg.luaConfig(), genDefaultsTables());
		});

	}

	public void writeToFile(String str, FileWriter file) {
		BufferedWriter writer = null;
		if (file != null) {
			try {
				writer = new BufferedWriter(file);
				writer.append("\n" + str);
				writer.flush();
				//writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}


	public LuaTable genDefaultsTables() {
		String dest = FabricLoader.getInstance().getConfigDirectory().toString() + "/" + "iguanaIdDump.txt";
		FileWriter o = null;
		try {
			o = new FileWriter(dest, false);
			o.write("");
		} catch (IOException e) {
			e.printStackTrace();
		}

		LuaTable BlockTable = LuaValue.tableOf();
		writeToFile("Blocks",o);
		//blocks
		FileWriter finalO4 = o;
		Registry.BLOCK.forEach(t -> {
			writeToFile("\t" + Registry.BLOCK.getId(t).toString(), finalO4);
			BlockTable.set(LuaValue.valueOf(Registry.BLOCK.getId(t).toString()), LuaValue.valueOf(t.asItem().getMaxCount()));
		});

		LuaTable BlockTagTable = LuaValue.tableOf();
		writeToFile("BlockTags",o);
		FileWriter finalO2 = o;
		FileWriter finalO5 = o;
		BlockTags.getContainer().getKeys().forEach(identifier -> {
			writeToFile("\t" + identifier.toString(), finalO5);
			LuaTable x = LuaTable.tableOf();
			AtomicInteger i = new AtomicInteger();
			TagRegistry.block(identifier).values().forEach(block -> {
				x.set(i.get(), LuaValue.valueOf(Registry.BLOCK.getId(block).toString()));
				i.addAndGet(1);
				writeToFile("\t\t" + Registry.BLOCK.getId(block).toString(), finalO2);
			});
			BlockTagTable.set(LuaValue.valueOf(identifier.toString()), x);
		});

		//items
		LuaTable ItemTable = LuaValue.tableOf();
		writeToFile("Items",o);
		FileWriter finalO3 = o;
		Registry.ITEM.forEach(t -> {
			writeToFile("\t" + Registry.ITEM.getId(t).toString(), finalO3);
			ItemTable.set(LuaValue.valueOf(Registry.ITEM.getId(t).toString()), LuaValue.valueOf(t.asItem().getMaxCount()));
		});

		LuaTable ItemTagTable = LuaValue.tableOf();
		writeToFile("ItemTags",o);
		FileWriter finalO1 = o;
		ItemTags.getContainer().getKeys().forEach(identifier -> {
			LuaTable x = LuaTable.tableOf();
			writeToFile("\t" + identifier.toString(), finalO1);
			AtomicInteger i = new AtomicInteger();
			TagRegistry.item(identifier).values().forEach(item -> {
				writeToFile("\t\t" + Registry.ITEM.getId(item).toString(), finalO1);
				x.set(i.get(), LuaValue.valueOf(Registry.ITEM.getId(item).toString()));
				i.addAndGet(1);
			});
			ItemTagTable.set(LuaValue.valueOf(identifier.toString()), x);
		});

		writeToFile("Enchantments",o);
		FileWriter finalO = o;
		Registry.ENCHANTMENT.forEach(enchantment -> {
			writeToFile("\t" + Registry.ENCHANTMENT.getId(enchantment).toString(), finalO);
		});

		LuaTable MasterTable = LuaValue.tableOf();
		MasterTable.set("blocks", BlockTable);
		MasterTable.set("items", ItemTable);
		MasterTable.set("blocktags", BlockTagTable);
		MasterTable.set("itemtags", ItemTagTable);

		if (o != null) {
			try {
				o.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return MasterTable;
	}

}
