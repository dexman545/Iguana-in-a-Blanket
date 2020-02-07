package dex.iguanablanket;

import com.google.common.collect.HashMultimap;
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
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.aeonbits.owner.ConfigFactory;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.PrimitiveIterator;
import java.util.concurrent.atomic.AtomicInteger;
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
			LuaConfigLoader.threadedmain(new String[] {"return 'foo'"}, genTables2());

		});

		ServerReloadCallback.EVENT.register(t -> {
			//LuaConfigLoader.threadedmain(new String[] {"return 'foo'"}, genTables2());
			LuaConfigCompilation.threadedmain(new String[] {FabricLoader.getInstance().getConfigDirectory().toString() + "/test.lua"}, genTables2());
			LuaConfigCompilation x = new LuaConfigCompilation();
			//x.getConfig(FabricLoader.getInstance().getConfigDirectory().toString() + "/test.lua", genTables2());
		});

	}

	public void whenWriteStringUsingBufferedWritter_thenCorrect(String str, String fileName) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true));
		//writer.write(str);
		writer.append(str);
		writer.close();
	}

	public HashMap<String, Object> genTables() {
		//init maps
		HashMap<String, Object> defaultMap = new HashMap<>();
		HashMap<Identifier, Integer> defaultBlockMap = new HashMap<>();
		HashMap<Identifier, Integer> defaultItemMap = new HashMap<>();
		HashMultimap<Identifier, Identifier> defaultBlockTagsMap = HashMultimap.create();
		HashMultimap<Identifier, Identifier> defaultItemTagsMap = HashMultimap.create();

		//blocks
		Registry.BLOCK.forEach(t -> {
			defaultBlockMap.put(Registry.BLOCK.getId(t), t.asItem().getMaxCount());
		});

		BlockTags.getContainer().getKeys().forEach(identifier -> {
			TagRegistry.block(identifier).values().forEach(block -> {
				defaultBlockTagsMap.put(identifier, Registry.BLOCK.getId(block));
			});
		});

		//items
		Registry.ITEM.forEach(t -> {
			defaultItemMap.put(Registry.ITEM.getId(t), t.getMaxCount());
		});

		ItemTags.getContainer().getKeys().forEach(identifier -> {
			TagRegistry.item(identifier).values().forEach(item -> {
				defaultItemTagsMap.put(identifier, Registry.ITEM.getId(item));
			});
		});

		defaultMap.put("blocks", defaultBlockMap);
		defaultMap.put("items", defaultItemMap);
		defaultMap.put("blocktags", defaultBlockTagsMap);
		defaultMap.put("itemtags", defaultItemTagsMap);

		return defaultMap;
	}

	public LuaTable genTables2() {

		LuaTable BlockTable = LuaValue.tableOf();
		//blocks
		Registry.BLOCK.forEach(t -> {
			BlockTable.set(LuaValue.valueOf(Registry.BLOCK.getId(t).toString()), LuaValue.valueOf(t.asItem().getMaxCount()));
		});

		LuaTable BlockTagTable = LuaValue.tableOf();
		BlockTags.getContainer().getKeys().forEach(identifier -> {
			LuaTable x = LuaTable.tableOf();
			AtomicInteger i = new AtomicInteger();
			TagRegistry.block(identifier).values().forEach(block -> {
				x.set(i.get(), LuaValue.valueOf(Registry.BLOCK.getId(block).toString()));
				i.addAndGet(1);
				//BlockTagTable.set(LuaValue.valueOf(identifier.toString()), LuaValue.valueOf(Registry.BLOCK.getId(block).toString()));
			});
			BlockTagTable.set(LuaValue.valueOf(identifier.toString()), x);
		});

		//items
		LuaTable ItemTable = LuaValue.tableOf();
		Registry.ITEM.forEach(t -> {
			ItemTable.set(LuaValue.valueOf(Registry.ITEM.getId(t).toString()), LuaValue.valueOf(t.asItem().getMaxCount()));
		});

		LuaTable ItemTagTable = LuaValue.tableOf();
		ItemTags.getContainer().getKeys().forEach(identifier -> {
			LuaTable x = LuaTable.tableOf();
			AtomicInteger i = new AtomicInteger();
			TagRegistry.item(identifier).values().forEach(item -> {
				x.set(i.get(), LuaValue.valueOf(Registry.ITEM.getId(item).toString()));
				i.addAndGet(1);
				//ItemTagTable.set(LuaValue.valueOf(identifier.toString()), LuaValue.valueOf(Registry.ITEM.getId(item).toString()));
			});
			ItemTagTable.set(LuaValue.valueOf(identifier.toString()), x);
		});

		LuaTable MasterTable = LuaValue.tableOf();
		MasterTable.set("blocks", BlockTable);
		MasterTable.set("items", ItemTable);
		MasterTable.set("blocktags", BlockTagTable);
		MasterTable.set("itemtags", ItemTagTable);

		return MasterTable;
	}

}
