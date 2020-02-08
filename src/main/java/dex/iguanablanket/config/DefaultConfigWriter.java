package dex.iguanablanket.config;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class DefaultConfigWriter {
    public void writeDefaultConfig(String fileName) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, false));
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
            "local maxStackWeight = 100 / (2*36)\n" +
            "\n" +
            "--this function must exist and eb named as such. It takes in the table of items, blocks, item tags, and block tags\n" +
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
            "    processBlocks(table[\"blocks\"]) --give all blocks default values\n" +
            "    processBlockTags(table[\"blocktags\"]) --changes the values absed on block tags\n" +
            "    processItemTags(table[\"itemtags\"]) --changes the values based on itemtags\n" +
            "    ensureNonstackablesRemain(table[\"items\"]) --makes sure nonstackable items remain that way\n" +
            "    exceptions() --any exception from block tags are handled here\n" +
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
            "function processBlockTags(bttable)\n" +
            "    for k, v in pairs(bttable[\"minecraft:planks\"]) do --k = block id; v = value; what's in the [] selects for that block tag\n" +
            "        configTable[\"blockweights\"][v] = 2\n" +
            "        configTable[\"blockstacksizes\"][v] = 32\n" +
            "        configTable[\"blockhardnessscale\"][v] = 2\n" +
            "        configTable[\"blockslowdownfactor\"][v] = 0\n" +
            "    end\n" +
            "    for k, v in pairs(bttable[\"minecraft:ice\"]) do\n" +
            "        configTable[\"blockweights\"][v] = maxStackWeight / 17\n" +
            "        configTable[\"blockstacksizes\"][v] = 17\n" +
            "        configTable[\"blockhardnessscale\"][v] = 0.5\n" +
            "        configTable[\"blockslowdownfactor\"][v] = 2\n" +
            "        configTable[\"enchantmentsakesslowdownignorant\"][v] = \"minecraft:frost_walker\"\n" +
            "    end\n" +
            "    for k, v in pairs(bttable[\"minecraft:anvil\"]) do\n" +
            "        configTable[\"blockweights\"][v] = 10\n" +
            "        configTable[\"blockstacksizes\"][v] = maxStackWeight / configTable[\"blockweights\"][v]\n" +
            "        configTable[\"blockhardnessscale\"][v] = 4\n" +
            "        configTable[\"blockslowdownfactor\"][v] = 0\n" +
            "    end\n" +
            "    for k, v in pairs(bttable[\"minecraft:banners\"]) do\n" +
            "        configTable[\"blockweights\"][v] = 3.0\n" +
            "        configTable[\"blockstacksizes\"][v] = maxStackWeight / configTable[\"blockweights\"][v]\n" +
            "        configTable[\"blockhardnessscale\"][v] = 1.2\n" +
            "        configTable[\"blockslowdownfactor\"][v] = 0\n" +
            "    end\n" +
            "    for k, v in pairs(bttable[\"minecraft:flower_pots\"]) do\n" +
            "        configTable[\"blockweights\"][v] = 0.5\n" +
            "        configTable[\"blockstacksizes\"][v] = maxStackWeight / configTable[\"blockweights\"][v]\n" +
            "        configTable[\"blockhardnessscale\"][v] = 0.9\n" +
            "        configTable[\"blockslowdownfactor\"][v] = 0\n" +
            "    end\n" +
            "    for k, v in pairs(bttable[\"minecraft:wooden_fences\"]) do\n" +
            "        configTable[\"blockweights\"][v] = 5\n" +
            "        configTable[\"blockstacksizes\"][v] = 32\n" +
            "        configTable[\"blockhardnessscale\"][v] = 2\n" +
            "        configTable[\"blockslowdownfactor\"][v] = 0\n" +
            "    end\n" +
            "    for k, v in pairs(bttable[\"minecraft:wooden_slabs\"]) do\n" +
            "        configTable[\"blockweights\"][v] = 1\n" +
            "        configTable[\"blockstacksizes\"][v] = 64\n" +
            "        configTable[\"blockhardnessscale\"][v] = 2\n" +
            "        configTable[\"blockslowdownfactor\"][v] = 0\n" +
            "    end\n" +
            "    for k, v in pairs(bttable[\"minecraft:small_flowers\"]) do\n" +
            "        configTable[\"blockweights\"][v] = 0.1\n" +
            "        configTable[\"blockstacksizes\"][v] = maxStackWeight / configTable[\"blockweights\"][v]\n" +
            "        configTable[\"blockhardnessscale\"][v] = 1\n" +
            "        configTable[\"blockslowdownfactor\"][v] = 0\n" +
            "    end\n" +
            "    for k, v in pairs(bttable[\"minecraft:logs\"]) do\n" +
            "        configTable[\"blockweights\"][v] = 8\n" +
            "        configTable[\"blockstacksizes\"][v] = 16\n" +
            "        configTable[\"blockhardnessscale\"][v] = 2.5\n" +
            "        configTable[\"blockslowdownfactor\"][v] = 0\n" +
            "    end\n" +
            "end\n" +
            "\n" +
            "function processItemTags(ittable)\n" +
            "    for k, v in pairs(ittable[\"fabric:swords\"]) do --same as in processBlockTags\n" +
            "        configTable[\"itemweights\"][v] = 3\n" +
            "        configTable[\"itemstacksizes\"][v] = 1\n" +
            "    end\n" +
            "    for k, v in pairs(ittable[\"fabric:pickaxes\"]) do\n" +
            "        configTable[\"itemweights\"][v] = 3.1\n" +
            "        configTable[\"itemstacksizes\"][v] = 1\n" +
            "    end\n" +
            "    for k, v in pairs(ittable[\"fabric:axes\"]) do\n" +
            "        configTable[\"itemweights\"][v] = 2.9\n" +
            "        configTable[\"itemstacksizes\"][v] = 1\n" +
            "    end\n" +
            "    for k, v in pairs(ittable[\"fabric:hoes\"]) do\n" +
            "        configTable[\"itemweights\"][v] = 3.3\n" +
            "        configTable[\"itemstacksizes\"][v] = 1\n" +
            "    end\n" +
            "    for k, v in pairs(ittable[\"fabric:shovels\"]) do\n" +
            "        configTable[\"itemweights\"][v] = 2.8\n" +
            "        configTable[\"itemstacksizes\"][v] = 1\n" +
            "    end\n" +
            "end\n" +
            "\n" +
            "function exceptions() --manually specifiying values per id\n" +
            "    configTable[\"blockslowdownfactor\"][\"minecraft:magma_block\"] = 3\n" +
            "    configTable[\"enchantmentsakesslowdownignorant\"][\"minecraft:magma_block\"] = \"minecraft:fire_protection\"\n" +
            "end\n" +
            "\n" +
            "function ensureNonstackablesRemain(itable)\n" +
            "    for k, v in pairs(itable) do\n" +
            "        if v == 1 then\n" +
            "            configTable[\"itemstacksizes\"][k] = 1\n" +
            "        end\n" +
            "    end\n" +
            "end";
}
