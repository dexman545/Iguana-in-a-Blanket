package dex.iguanablanket.config;

import java.io.*;

public class DefaultConfigWriter {
    public void writeDefaultConfig(String fileName) throws IOException {
        File file = new File(fileName);
        file.getParentFile().mkdirs();
        //file.createNewFile();
        BufferedWriter writer = new BufferedWriter(new FileWriter(file, false));

        writer.write(str);
        writer.close();
    }

    String str = "--This file is overwritten on startup! Load a copy of this via iguana.cfg if you want to make changes to it!\n" +
            "\n" +
            "local configTable = { --All config files must return a table in this formt, with these names (configTable can be any name, but the values inside must be named correctly)\n" +
            "    blockweights = {}, --map of block id -> weight\n" +
            "    itemweights = {}, --map of item id -> weight\n" +
            "    blockstacksizes = {}, --map of block id -> stack size\n" +
            "    itemstacksizes = {}, --map of item id -> stack size\n" +
            "    blockhardnessscale = {}, --map of block id -> hardnes scale\n" +
            "    blockslowdownfactor = {}, --map of block id -> slowdown factor; postive decreases the speed, negative increases it\n" +
            "    enchantmentsakesslowdownignorant = {} --map of block id -> enchant id. If the entity has this enchantment on armour, not slowed down by the block\n" +
            "}\n" +
            "\n" +
            "--A variable to set the max weight of a stack, can be used or changed or ignored\n" +
            "local maxStackWeight = 200 / (0.25*36)\n" +
            "\n" +
            "--this function must exist and be named as such. It takes in the table of items, blocks, item tags, and block tags\n" +
            "--it must return configTable or equivalent\n" +
            "--what happens inside is up to you\n" +
            "function generateTable(table)\n" +
            "    for k, v in pairs(table[\"blocktags\"]) do --example loop\n" +
            "        for k2, v2 in pairs(v) do\n" +
            "            --print(k, v2)\n" +
            "        end\n" +
            "    end\n" +
            "\n" +
            "    --the order of operations changes the final values.\n" +
            "    processItems(table[\"items\"]) --give all items default values\n" +
            "    processBlocks(table[\"blocks\"]) --give all blocks default values \n" +
            "    makeSingleStackBlocksHeavy(table[\"blocks\"])\n" +
            "    processBlockTags(table[\"blocktags\"]) --changes the values absed on block tags\n" +
            "    processItemTags(table[\"itemtags\"]) --changes the values based on itemtags\n" +
            "    ensureNonstackablesRemain(table[\"items\"]) --makes sure nonstackable items remain that way\n" +
            "    exceptions() --any exception from block tags are handled here\n" +
            "    armour() -- armour weights\n" +
            "    return configTable\n" +
            "end\n" +
            "\n" +
            "function processItems(itable)\n" +
            "    for k, v in pairs(itable) do --k = item id; v = value\n" +
            "        configTable[\"itemstacksizes\"][k] = 32\n" +
            "        configTable[\"itemweights\"][k] = maxStackWeight / 32\n" +
            "    end\n" +
            "end\n" +
            "\n" +
            "function processBlocks(btable)\n" +
            "    for k, v in pairs(btable) do --k = block id; v = value\n" +
            "        configTable[\"blockstacksizes\"][k] = 32\n" +
            "        configTable[\"blockweights\"][k] = maxStackWeight / 32\n" +
            "    end\n" +
            "end\n" +
            "\n" +
            "local waterToIceRatio = 1.091\n" +
            "local iceDensity = 881\n" +
            "local iceWeight = 2 --iceDensity * 9.81\n" +
            "local waterDensity = waterToIceRatio * iceDensity\n" +
            "local normalization = iceWeight / iceDensity\n" +
            "\n" +
            "\n" +
            "function processBlockTags(bttable) --wood stuff uses oak density\n" +
            "    for k, v in pairs(bttable[\"minecraft:ice\"]) do\n" +
            "        configTable[\"blockweights\"][v] = iceWeight\n" +
            "        configTable[\"blockstacksizes\"][v] = maxStackWeight / configTable[\"blockweights\"][v]\n" +
            "        configTable[\"blockhardnessscale\"][v] = 0.5\n" +
            "        configTable[\"blockslowdownfactor\"][v] = 2\n" +
            "        configTable[\"enchantmentsakesslowdownignorant\"][v] = \"minecraft:frost_walker\"\n" +
            "    end\n" +
            "    for k, v in pairs(bttable[\"minecraft:flower_pots\"]) do\n" +
            "        configTable[\"blockweights\"][v] = (400/8) * normalization\n" +
            "        configTable[\"blockstacksizes\"][v] = maxStackWeight / configTable[\"blockweights\"][v]\n" +
            "    end\n" +
            "    for k, v in pairs(bttable[\"minecraft:crops\"]) do\n" +
            "        configTable[\"blockweights\"][v] = 160 * normalization\n" +
            "        configTable[\"blockstacksizes\"][v] = maxStackWeight / configTable[\"blockweights\"][v]\n" +
            "    end\n" +
            "    for k, v in pairs(bttable[\"minecraft:bamboo_plantable_on\"]) do\n" +
            "        configTable[\"blockweights\"][v] = 1041 * normalization\n" +
            "        configTable[\"blockstacksizes\"][v] = maxStackWeight / configTable[\"blockweights\"][v]\n" +
            "    end\n" +
            "    for k, v in pairs(bttable[\"minecraft:sand\"]) do\n" +
            "        configTable[\"blockweights\"][v] = 105 * normalization\n" +
            "        configTable[\"blockstacksizes\"][v] = maxStackWeight / configTable[\"blockweights\"][v]\n" +
            "    end\n" +
            "    for k, v in pairs(bttable[\"minecraft:leaves\"]) do\n" +
            "        configTable[\"blockweights\"][v] = (192.2) * normalization\n" +
            "        configTable[\"blockstacksizes\"][v] = maxStackWeight / configTable[\"blockweights\"][v]\n" +
            "    end\n" +
            "    for k, v in pairs(bttable[\"minecraft:wool\"]) do\n" +
            "        configTable[\"blockweights\"][v] = 1314 * normalization\n" +
            "        configTable[\"blockstacksizes\"][v] = maxStackWeight / configTable[\"blockweights\"][v]\n" +
            "    end\n" +
            "    for k, v in pairs(bttable[\"minecraft:logs\"]) do\n" +
            "        configTable[\"blockweights\"][v] = 590 * normalization\n" +
            "        configTable[\"blockstacksizes\"][v] = maxStackWeight / configTable[\"blockweights\"][v]\n" +
            "    end\n" +
            "    for k, v in pairs(bttable[\"minecraft:anvil\"]) do\n" +
            "        configTable[\"blockweights\"][v] = (2883*3.14) * normalization\n" +
            "        configTable[\"blockstacksizes\"][v] = maxStackWeight / configTable[\"blockweights\"][v]\n" +
            "    end\n" +
            "    for k, v in pairs(bttable[\"minecraft:stone_bricks\"]) do\n" +
            "        configTable[\"blockweights\"][v] = 2400 * normalization\n" +
            "        configTable[\"blockstacksizes\"][v] = maxStackWeight / configTable[\"blockweights\"][v]\n" +
            "    end\n" +
            "    for k, v in pairs(bttable[\"minecraft:walls\"]) do\n" +
            "        configTable[\"blockweights\"][v] = (2400/.25) * normalization\n" +
            "        configTable[\"blockstacksizes\"][v] = maxStackWeight / configTable[\"blockweights\"][v]\n" +
            "    end\n" +
            "    for k, v in pairs(bttable[\"minecraft:planks\"]) do\n" +
            "        configTable[\"blockweights\"][v] = (590/4) * normalization\n" +
            "        configTable[\"blockstacksizes\"][v] = maxStackWeight / configTable[\"blockweights\"][v]\n" +
            "    end\n" +
            "    for k, v in pairs(bttable[\"minecraft:slabs\"]) do\n" +
            "        configTable[\"blockweights\"][v] = (2400/2) * normalization\n" +
            "        configTable[\"blockstacksizes\"][v] = maxStackWeight / configTable[\"blockweights\"][v]\n" +
            "    end\n" +
            "    for k, v in pairs(bttable[\"minecraft:wooden_slabs\"]) do\n" +
            "        configTable[\"blockweights\"][v] = (590/8) * normalization\n" +
            "        configTable[\"blockstacksizes\"][v] = maxStackWeight / configTable[\"blockweights\"][v]\n" +
            "    end\n" +
            "    for k, v in pairs(bttable[\"minecraft:stairs\"]) do\n" +
            "        configTable[\"blockweights\"][v] = (2400/1.5) * normalization\n" +
            "        configTable[\"blockstacksizes\"][v] = maxStackWeight / configTable[\"blockweights\"][v]\n" +
            "    end\n" +
            "    for k, v in pairs(bttable[\"minecraft:wooden_stairs\"]) do\n" +
            "        configTable[\"blockweights\"][v] = (590/3) * normalization\n" +
            "        configTable[\"blockstacksizes\"][v] = maxStackWeight / configTable[\"blockweights\"][v]\n" +
            "    end\n" +
            "    for k, v in pairs(bttable[\"minecraft:doors\"]) do\n" +
            "        configTable[\"blockweights\"][v] = (2883/4) * normalization\n" +
            "        configTable[\"blockstacksizes\"][v] = maxStackWeight / configTable[\"blockweights\"][v]\n" +
            "    end\n" +
            "    for k, v in pairs(bttable[\"minecraft:wooden_doors\"]) do\n" +
            "        configTable[\"blockweights\"][v] = (590/16) * normalization\n" +
            "        configTable[\"blockstacksizes\"][v] = maxStackWeight / configTable[\"blockweights\"][v]\n" +
            "    end\n" +
            "    for k, v in pairs(bttable[\"minecraft:trapdoors\"]) do\n" +
            "        configTable[\"blockweights\"][v] = (2883/8) * normalization\n" +
            "        configTable[\"blockstacksizes\"][v] = maxStackWeight / configTable[\"blockweights\"][v]\n" +
            "    end\n" +
            "    for k, v in pairs(bttable[\"minecraft:wooden_trapdoors\"]) do\n" +
            "        configTable[\"blockweights\"][v] = (590/32) * normalization\n" +
            "        configTable[\"blockstacksizes\"][v] = maxStackWeight / configTable[\"blockweights\"][v]\n" +
            "    end\n" +
            "    for k, v in pairs(bttable[\"minecraft:buttons\"]) do\n" +
            "        configTable[\"blockweights\"][v] = (2400*.012) * normalization\n" +
            "        configTable[\"blockstacksizes\"][v] = maxStackWeight / configTable[\"blockweights\"][v]\n" +
            "    end\n" +
            "    for k, v in pairs(bttable[\"minecraft:wooden_buttons\"]) do\n" +
            "        configTable[\"blockweights\"][v] = (.012*590/4) * normalization\n" +
            "        configTable[\"blockstacksizes\"][v] = maxStackWeight / configTable[\"blockweights\"][v]\n" +
            "    end\n" +
            "    for k, v in pairs(bttable[\"minecraft:beds\"]) do\n" +
            "        configTable[\"blockweights\"][v] = 3*((590/4)+(1314)) * normalization\n" +
            "        configTable[\"blockstacksizes\"][v] = maxStackWeight / configTable[\"blockweights\"][v]\n" +
            "    end\n" +
            "    for k, v in pairs(bttable[\"minecraft:shulker_boxes\"]) do\n" +
            "        configTable[\"blockweights\"][v] = 10 * normalization\n" +
            "        configTable[\"blockstacksizes\"][v] = maxStackWeight / configTable[\"blockweights\"][v]\n" +
            "    end\n" +
            "    for k, v in pairs(bttable[\"minecraft:flowers\"]) do\n" +
            "        configTable[\"blockweights\"][v] = 80*(.1) * normalization\n" +
            "        configTable[\"blockstacksizes\"][v] = maxStackWeight / configTable[\"blockweights\"][v]\n" +
            "    end\n" +
            "    for k, v in pairs(bttable[\"minecraft:tall_flowers\"]) do\n" +
            "        configTable[\"blockweights\"][v] = 160*(.1) * normalization\n" +
            "        configTable[\"blockstacksizes\"][v] = maxStackWeight / configTable[\"blockweights\"][v]\n" +
            "    end\n" +
            "    for k, v in pairs(bttable[\"minecraft:fences\"]) do\n" +
            "        configTable[\"blockweights\"][v] = (590/4)*.08 * normalization\n" +
            "        configTable[\"blockstacksizes\"][v] = maxStackWeight / configTable[\"blockweights\"][v]\n" +
            "    end\n" +
            "    for k, v in pairs(bttable[\"minecraft:saplings\"]) do\n" +
            "        configTable[\"blockweights\"][v] = 160*(.1) * normalization\n" +
            "        configTable[\"blockstacksizes\"][v] = maxStackWeight / configTable[\"blockweights\"][v]\n" +
            "    end\n" +
            "        for k, v in pairs(bttable[\"minecraft:banners\"]) do\n" +
            "        configTable[\"blockweights\"][v] = (1314/8) * normalization\n" +
            "        configTable[\"blockstacksizes\"][v] = maxStackWeight / configTable[\"blockweights\"][v]\n" +
            "    end\n" +
            "    for k, v in pairs(bttable[\"minecraft:rails\"]) do\n" +
            "        configTable[\"blockweights\"][v] = (2883/16) * normalization\n" +
            "        configTable[\"blockstacksizes\"][v] = maxStackWeight / configTable[\"blockweights\"][v]\n" +
            "    end\n" +
            "    for k, v in pairs(bttable[\"minecraft:carpets\"]) do\n" +
            "        configTable[\"blockweights\"][v] = (1314/16) * normalization\n" +
            "        configTable[\"blockstacksizes\"][v] = maxStackWeight / configTable[\"blockweights\"][v]\n" +
            "    end\n" +
            "    for k, v in pairs(bttable[\"minecraft:spruce_logs\"]) do\n" +
            "        configTable[\"blockweights\"][v] = 480 * normalization\n" +
            "        configTable[\"blockstacksizes\"][v] = maxStackWeight / configTable[\"blockweights\"][v]\n" +
            "    end\n" +
            "    for k, v in pairs(bttable[\"minecraft:jungle_logs\"]) do\n" +
            "        configTable[\"blockweights\"][v] = 560 * normalization\n" +
            "        configTable[\"blockstacksizes\"][v] = maxStackWeight / configTable[\"blockweights\"][v]\n" +
            "    end\n" +
            "    for k, v in pairs(bttable[\"minecraft:acacia_logs\"]) do\n" +
            "        configTable[\"blockweights\"][v] = 770 * normalization\n" +
            "        configTable[\"blockstacksizes\"][v] = maxStackWeight / configTable[\"blockweights\"][v]\n" +
            "    end\n" +
            "    for k, v in pairs(bttable[\"minecraft:birch_logs\"]) do\n" +
            "        configTable[\"blockweights\"][v] = 670 * normalization\n" +
            "        configTable[\"blockstacksizes\"][v] = maxStackWeight / configTable[\"blockweights\"][v]\n" +
            "    end\n" +
            "    for k, v in pairs(bttable[\"minecraft:dark_oak_logs\"]) do\n" +
            "        configTable[\"blockweights\"][v] = 740 * normalization\n" +
            "        configTable[\"blockstacksizes\"][v] = maxStackWeight / configTable[\"blockweights\"][v]\n" +
            "    end\n" +
            "    for k, v in pairs(bttable[\"minecraft:wooden_pressure_plates\"]) do\n" +
            "        configTable[\"blockweights\"][v] = 590/9 * normalization\n" +
            "        configTable[\"blockstacksizes\"][v] = maxStackWeight / configTable[\"blockweights\"][v]\n" +
            "    end\n" +
            "    for k, v in pairs(bttable[\"minecraft:impermeable\"]) do\n" +
            "        configTable[\"blockweights\"][v] = 1922 * normalization\n" +
            "        configTable[\"blockstacksizes\"][v] = maxStackWeight / configTable[\"blockweights\"][v]\n" +
            "    end\n" +
            "end\n" +
            "\n" +
            "function processItemTags(ittable)\n" +
            "    for k, v in pairs(ittable[\"fabric:swords\"]) do --same as in processBlockTags\n" +
            "        configTable[\"itemweights\"][v] = 3 * normalization * 100\n" +
            "        configTable[\"itemstacksizes\"][v] = 1\n" +
            "    end\n" +
            "    for k, v in pairs(ittable[\"fabric:pickaxes\"]) do\n" +
            "        configTable[\"itemweights\"][v] = 3.1 * normalization * 100\n" +
            "        configTable[\"itemstacksizes\"][v] = 1\n" +
            "    end\n" +
            "    for k, v in pairs(ittable[\"fabric:axes\"]) do\n" +
            "        configTable[\"itemweights\"][v] = 2.9 * normalization * 100\n" +
            "        configTable[\"itemstacksizes\"][v] = 1\n" +
            "    end\n" +
            "    for k, v in pairs(ittable[\"fabric:hoes\"]) do\n" +
            "        configTable[\"itemweights\"][v] = 3.3 * normalization * 100\n" +
            "        configTable[\"itemstacksizes\"][v] = 1\n" +
            "    end\n" +
            "    for k, v in pairs(ittable[\"fabric:shovels\"]) do\n" +
            "        configTable[\"itemweights\"][v] = 2.8 * normalization * 100\n" +
            "        configTable[\"itemstacksizes\"][v] = 1\n" +
            "    end\n" +
            "    for k, v in pairs(ittable[\"minecraft:coals\"]) do\n" +
            "        configTable[\"itemweights\"][v] = 721 * normalization\n" +
            "        configTable[\"itemstacksizes\"][v] = maxStackWeight / configTable[\"itemweights\"][v]\n" +
            "    end\n" +
            "    for k, v in pairs(ittable[\"minecraft:boats\"]) do\n" +
            "        configTable[\"itemweights\"][v] = 590*5/9 * normalization\n" +
            "        configTable[\"itemstacksizes\"][v] = maxStackWeight / configTable[\"itemweights\"][v]\n" +
            "    end\n" +
            "    for k, v in pairs(ittable[\"minecraft:lectern_books\"]) do\n" +
            "        configTable[\"itemweights\"][v] = 80.1 * normalization\n" +
            "        configTable[\"itemstacksizes\"][v] = maxStackWeight / configTable[\"itemweights\"][v]\n" +
            "    end\n" +
            "    for k, v in pairs(ittable[\"minecraft:arrows\"]) do\n" +
            "        configTable[\"itemweights\"][v] = 590/8 * normalization\n" +
            "        configTable[\"itemstacksizes\"][v] = maxStackWeight / configTable[\"itemweights\"][v]\n" +
            "    end\n" +
            "    for k, v in pairs(ittable[\"minecraft:fishes\"]) do\n" +
            "        configTable[\"itemweights\"][v] = 400/9 * normalization\n" +
            "        configTable[\"itemstacksizes\"][v] = maxStackWeight / configTable[\"itemweights\"][v]\n" +
            "    end\n" +
            "    for k, v in pairs(ittable[\"minecraft:music_discs\"]) do\n" +
            "        configTable[\"itemweights\"][v] = .15 * normalization\n" +
            "        configTable[\"itemstacksizes\"][v] = maxStackWeight / configTable[\"itemweights\"][v]\n" +
            "    end\n" +
            "end\n" +
            "\n" +
            "function exceptions() --manually specifiying values per id\n" +
            "    configTable[\"blockslowdownfactor\"][\"minecraft:magma_block\"] = 3\n" +
            "    configTable[\"enchantmentsakesslowdownignorant\"][\"minecraft:magma_block\"] = \"minecraft:fire_protection\"\n" +
            "    configTable[\"itemweights\"][\"minecraft:charcoal\"] = 270/9 * normalization\n" +
            "    configTable[\"itemweights\"][\"minecraft:diamond\"] = 3514 * (1/9) * normalization\n" +
            "    configTable[\"itemweights\"][\"minecraft:gold_ingot\"] = 19300 * (1/9) * normalization\n" +
            "    configTable[\"itemweights\"][\"minecraft:iron_ingot\"] = 2883 * (1/9) * normalization\n" +
            "    configTable[\"itemweights\"][\"minecraft:netherite_ingot\"] = (4*(19300 + (19050/3))) * (1/9) * normalization\n" +
            "    configTable[\"itemweights\"][\"minecraft:cobblestone\"] = 2400 * (.6) * normalization\n" +
            "    configTable[\"itemweights\"][\"minecraft:gravel\"] = 1800 * normalization\n" +
            "    configTable[\"itemweights\"][\"minecraft:leather\"] = 860 * (1/9) * normalization\n" +
            "    configTable[\"blockweights\"][\"minecraft:hay_block\"] = 384 * normalization\n" +
            "    configTable[\"blockstacksizes\"][\"minecraft:hay_block\"] = maxStackWeight / configTable[\"blockweights\"][\"minecraft:hay_block\"]\n" +
            "    configTable[\"blockweights\"][\"minecraft:stone\"] = 2400 * normalization\n" +
            "    configTable[\"blockstacksizes\"][\"minecraft:stone\"] = maxStackWeight / configTable[\"blockweights\"][\"minecraft:stone\"]\n" +
            "    configTable[\"blockweights\"][\"minecraft:iron_block\"] = 2883 * normalization\n" +
            "    configTable[\"blockstacksizes\"][\"minecraft:iron_block\"] = maxStackWeight / configTable[\"blockweights\"][\"minecraft:iron_block\"]\n" +
            "    configTable[\"blockweights\"][\"minecraft:gold_block\"] = 19300 * normalization\n" +
            "    configTable[\"blockstacksizes\"][\"minecraft:gold_block\"] = maxStackWeight / configTable[\"blockweights\"][\"minecraft:gold_block\"]\n" +
            "    configTable[\"blockweights\"][\"minecraft:diamond_block\"] = 3514 * normalization\n" +
            "    configTable[\"blockstacksizes\"][\"minecraft:diamond_block\"] = maxStackWeight / configTable[\"blockweights\"][\"minecraft:diamond_block\"]\n" +
            "    configTable[\"blockweights\"][\"minecraft:netherite_block\"] = (4*(19300 + (19050/3))) * normalization\n" +
            "    configTable[\"blockstacksizes\"][\"minecraft:netherite_block\"] = maxStackWeight / configTable[\"blockweights\"][\"minecraft:netherite_block\"]\n" +
            "end\n" +
            "\n" +
            "function makeSingleStackBlocksHeavy(btable)\n" +
            "    for k, v in pairs(btable) do\n" +
            "        if v == 1 then\n" +
            "            configTable[\"blockweights\"][k] = 9\n" +
            "        end\n" +
            "    end\n" +
            "end\n" +
            "\n" +
            "function ensureNonstackablesRemain(itable)\n" +
            "    for k, v in pairs(itable) do\n" +
            "        if v == 1 then\n" +
            "            configTable[\"itemstacksizes\"][k] = 1\n" +
            "        elseif v > 64 then\n" +
            "            configTable[\"itemstacksizes\"][k] = 64\n" +
            "        end\n" +
            "    end\n" +
            "end\n" +
            "\n" +
            "function armour()\n" +
            "    configTable[\"itemweights\"][\"minecraft:golden_helmet\"] = 19300 * (5/9) * normalization\n" +
            "    configTable[\"itemweights\"][\"minecraft:golden_chestplate\"] = 19300 * (8/9) * normalization\n" +
            "    configTable[\"itemweights\"][\"minecraft:golden_leggings\"] = 19300 * (7/9) * normalization\n" +
            "    configTable[\"itemweights\"][\"minecraft:golden_boots\"] = 19300 * (4/9) * normalization\n" +
            "    configTable[\"itemweights\"][\"minecraft:chainmail_helmet\"] = (2883/100) * (5/9) * normalization\n" +
            "    configTable[\"itemweights\"][\"minecraft:chainmail_chestplate\"] = (2883/100) * (8/9) * normalization\n" +
            "    configTable[\"itemweights\"][\"minecraft:chainmail_leggings\"] = (2883/100) * (7/9) * normalization\n" +
            "    configTable[\"itemweights\"][\"minecraft:chainmail_boots\"] = (2883/100) * (4/9) * normalization\n" +
            "    configTable[\"itemweights\"][\"minecraft:iron_helmet\"] = 2883 * (5/9) * normalization\n" +
            "    configTable[\"itemweights\"][\"minecraft:iron_chestplate\"] = 2883 * (8/9) * normalization\n" +
            "    configTable[\"itemweights\"][\"minecraft:iron_leggings\"] = 2883 * (7/9) * normalization\n" +
            "    configTable[\"itemweights\"][\"minecraft:iron_boots\"] = 2883 * (4/9) * normalization\n" +
            "    configTable[\"itemweights\"][\"minecraft:leather_helmet\"] = 860 * (5/9) * normalization\n" +
            "    configTable[\"itemweights\"][\"minecraft:leather_chestplate\"] = 860 * (8/9) * normalization\n" +
            "    configTable[\"itemweights\"][\"minecraft:leather_leggings\"] = 860 * (7/9) * normalization\n" +
            "    configTable[\"itemweights\"][\"minecraft:leather_boots\"] = 860 * (4/9) * normalization\n" +
            "    configTable[\"itemweights\"][\"minecraft:diamond_helmet\"] = 3514 * (5/9) * normalization\n" +
            "    configTable[\"itemweights\"][\"minecraft:diamond_chestplate\"] = 3514 * (8/9) * normalization\n" +
            "    configTable[\"itemweights\"][\"minecraft:diamond_leggings\"] = 3514 * (7/9) * normalization\n" +
            "    configTable[\"itemweights\"][\"minecraft:diamond_boots\"] = 3514 * (4/9) * normalization\n" +
            "    configTable[\"itemweights\"][\"minecraft:netherite_helmet\"] = (4*(19300 + (19050/3))) * (5/9) * normalization --ancient debris I'm saying has same density of uranium, hence the 19050\n" +
            "    configTable[\"itemweights\"][\"minecraft:netherite_chestplate\"] = (4*(19300 + (19050/3))) * (8/9) * normalization\n" +
            "    configTable[\"itemweights\"][\"minecraft:netherite_leggings\"] = (4*(19300 + (19050/3))) * (7/9) * normalization\n" +
            "    configTable[\"itemweights\"][\"minecraft:netherite_boots\"] = (4*(19300 + (19050/3))) * (4/9) * normalization\n" +
            "end\n" +
            "\n";
}
