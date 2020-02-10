package dex.iguanablanket.config;

import dex.iguanablanket.IguanaBlanket;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.registry.Registry;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class GenerateData {
    public void writeToFile(String str, FileWriter file) {
        BufferedWriter writer = null;
        if (file != null) {
            try {
                writer = new BufferedWriter(file);
                writer.append("\n" + str);
                writer.flush();
                //writer.close();
            } catch (IOException e) {
                IguanaBlanket.logger.catching(e);
            }
        }
    }


    public LuaTable genDefaultsTables() {
        String dest = FabricLoader.getInstance().getConfigDirectory().toString() + "/iguana-blanket/" + "iguanaIdDump.txt";
        FileWriter o = null;
        try {
            o = new FileWriter(dest, false);
            o.write("This is a dump of all ids iguana can handle. This file is overwritten on each world (re)load " +
                    "\nThis data has the dame structure as the data passed to the lua file");
        } catch (IOException e) {
            IguanaBlanket.logger.catching(e);
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
                IguanaBlanket.logger.catching(e);
            }
        }

        return MasterTable;
    }
}
