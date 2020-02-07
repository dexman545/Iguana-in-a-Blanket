package dex.iguanablanket;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class DefaultConfigWriter {
    public void writeDefaultConfig(String fileName) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true));
        writer.write(str);
        writer.close();
    }

    String str = "--This file is overwritten on startup! Load a copy of this via iguana.cfg if you want to make changes to it!\n" +
            "\n" +
            "local configTable = {\n" +
            "    blockweights = {},\n" +
            "    itemweights = {},\n" +
            "    blockstacksizes = {},\n" +
            "    itemstacksizes = {},\n" +
            "    blockhardnessscale = {},\n" +
            "    blockslowdownfactor = {}\n" +
            "}\n" +
            "\n" +
            "local maxStackWeight = 15\n" +
            "\n" +
            "function generateTable(table)\n" +
            "    for k, v in pairs(table[\"blocktags\"]) do\n" +
            "        for k2, v2 in pairs(v) do\n" +
            "            --print(k, v2)\n" +
            "        end\n" +
            "    end\n" +
            "    processItems(table[\"items\"])\n" +
            "    processBlocks(table[\"blocks\"])\n" +
            "    processBlockTags(table[\"blocktags\"])\n" +
            "    return configTable\n" +
            "end\n" +
            "\n" +
            "function processItems(itable)\n" +
            "    for k, v in pairs(itable) do\n" +
            "        configTable[\"itemstacksizes\"][k] = 32\n" +
            "        configTable[\"itemweights\"][k] = 1.11\n" +
            "    end\n" +
            "end\n" +
            "\n" +
            "function processBlocks(btable)\n" +
            "    for k, v in pairs(btable) do\n" +
            "        configTable[\"blockstacksizes\"][k] = 32\n" +
            "        configTable[\"blockweights\"][k] = 2.22\n" +
            "    end\n" +
            "end\n" +
            "\n" +
            "function processBlockTags(bttable)\n" +
            "    for k, v in pairs(bttable[\"minecraft:planks\"]) do\n" +
            "        configTable[\"blockweights\"][v] = 2\n" +
            "        configTable[\"blockstacksizes\"][v] = 32\n" +
            "        configTable[\"blockhardnessscale\"][v] = 2\n" +
            "        configTable[\"blockslowdownfactor\"][v] = 0\n" +
            "    end\n" +
            "    for k, v in pairs(bttable[\"minecraft:ice\"]) do\n" +
            "        configTable[\"blockweights\"][v] = 5.2\n" +
            "        configTable[\"blockstacksizes\"][v] = 17\n" +
            "        configTable[\"blockhardnessscale\"][v] = 0.5\n" +
            "        configTable[\"blockslowdownfactor\"][v] = 2\n" +
            "    end\n" +
            "    for k, v in pairs(bttable[\"minecraft:anvil\"]) do\n" +
            "        configTable[\"blockweights\"][v] = 10\n" +
            "        configTable[\"blockstacksizes\"][v] = maxStackWeight / configTable[\"blockweights\"][v]\n" +
            "        configTable[\"blockhardnessscale\"][v] = 4\n" +
            "        configTable[\"blockslowdownfactor\"][v] = 0\n" +
            "    end\n" +
            "end";
}
