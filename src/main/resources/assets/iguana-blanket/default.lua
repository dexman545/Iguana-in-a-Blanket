--This file is overwritten on startup! Load a copy of this via iguana.cfg if you want to make changes to it!

local configTable = { --All config files must return a table in this formt, with these names (configTable can be any name, but the values inside must be named correctly)
    blockweights = {}, --map of block id -> weight
    itemweights = {}, --map of item id -> weight
    blockstacksizes = {}, --map of block id -> stack size
    itemstacksizes = {}, --map of item id -> stack size
    blockhardnessscale = {}, --map of block id -> hardnes scale
    blockslowdownfactor = {}, --map of block id -> slowdown factor; postive decreases the speed, negative increases it
    enchantmentsakesslowdownignorant = {} --map of block id -> enchant id. If the entity has this enchantment on armour, not slowed down by the block
}

--A variable to set the max weight of a stack, can be used or changed or ignored
local maxStackWeight = 200 / (0.25*36)

--this function must exist and be named as such. It takes in the table of items, blocks, item tags, and block tags
--it must return configTable or equivalent
--what happens inside is up to you
function generateTable(table)
    for k, v in pairs(table["blocktags"]) do --example loop
        for k2, v2 in pairs(v) do
            --print(k, v2)
        end
    end

    --the order of operations changes the final values.
    processItems(table["items"]) --give all items default values
    processBlocks(table["blocks"]) --give all blocks default values
    makeSingleStackBlocksHeavy(table["blocks"])
    processBlockTags(table["blocktags"]) --changes the values absed on block tags
    processItemTags(table["itemtags"]) --changes the values based on itemtags
    ensureNonstackablesRemain(table["items"]) --makes sure nonstackable items remain that way
    exceptions() --any exception from block tags are handled here
    armour() -- armour weights
    return configTable
end

function processItems(itable)
    for k, v in pairs(itable) do --k = item id; v = value
        configTable["itemstacksizes"][k] = 32
        configTable["itemweights"][k] = maxStackWeight / 32
    end
end

function processBlocks(btable)
    for k, v in pairs(btable) do --k = block id; v = value
        configTable["blockstacksizes"][k] = 32
        configTable["blockweights"][k] = maxStackWeight / 32
    end
end

local waterToIceRatio = 1.091
local iceDensity = 881
local iceWeight = 2 --iceDensity * 9.81
local waterDensity = waterToIceRatio * iceDensity
local normalization = iceWeight / iceDensity


function processBlockTags(bttable) --wood stuff uses oak density
    for k, v in pairs(bttable["minecraft:ice"]) do
        configTable["blockweights"][v] = iceWeight
        configTable["blockstacksizes"][v] = maxStackWeight / configTable["blockweights"][v]
        configTable["blockhardnessscale"][v] = 0.5
        configTable["blockslowdownfactor"][v] = 2
        configTable["enchantmentsakesslowdownignorant"][v] = "minecraft:frost_walker"
    end
    for k, v in pairs(bttable["minecraft:flower_pots"]) do
        configTable["blockweights"][v] = (400/8) * normalization
        configTable["blockstacksizes"][v] = maxStackWeight / configTable["blockweights"][v]
    end
    for k, v in pairs(bttable["minecraft:crops"]) do
        configTable["blockweights"][v] = 160 * normalization
        configTable["blockstacksizes"][v] = maxStackWeight / configTable["blockweights"][v]
    end
    for k, v in pairs(bttable["minecraft:bamboo_plantable_on"]) do
        configTable["blockweights"][v] = 1041 * normalization
        configTable["blockstacksizes"][v] = maxStackWeight / configTable["blockweights"][v]
    end
    for k, v in pairs(bttable["minecraft:sand"]) do
        configTable["blockweights"][v] = 105 * normalization
        configTable["blockstacksizes"][v] = maxStackWeight / configTable["blockweights"][v]
    end
    for k, v in pairs(bttable["minecraft:leaves"]) do
        configTable["blockweights"][v] = (192.2) * normalization
        configTable["blockstacksizes"][v] = maxStackWeight / configTable["blockweights"][v]
    end
    for k, v in pairs(bttable["minecraft:wool"]) do
        configTable["blockweights"][v] = 1314 * normalization
        configTable["blockstacksizes"][v] = maxStackWeight / configTable["blockweights"][v]
    end
    for k, v in pairs(bttable["minecraft:logs"]) do
        configTable["blockweights"][v] = 590 * normalization
        configTable["blockstacksizes"][v] = maxStackWeight / configTable["blockweights"][v]
    end
    for k, v in pairs(bttable["minecraft:anvil"]) do
        configTable["blockweights"][v] = (2883*3.14) * normalization
        configTable["blockstacksizes"][v] = maxStackWeight / configTable["blockweights"][v]
    end
    for k, v in pairs(bttable["minecraft:stone_bricks"]) do
        configTable["blockweights"][v] = 2400 * normalization
        configTable["blockstacksizes"][v] = maxStackWeight / configTable["blockweights"][v]
    end
    for k, v in pairs(bttable["minecraft:walls"]) do
        configTable["blockweights"][v] = (2400/.25) * normalization
        configTable["blockstacksizes"][v] = maxStackWeight / configTable["blockweights"][v]
    end
    for k, v in pairs(bttable["minecraft:planks"]) do
        configTable["blockweights"][v] = (590/4) * normalization
        configTable["blockstacksizes"][v] = maxStackWeight / configTable["blockweights"][v]
    end
    for k, v in pairs(bttable["minecraft:slabs"]) do
        configTable["blockweights"][v] = (2400/2) * normalization
        configTable["blockstacksizes"][v] = maxStackWeight / configTable["blockweights"][v]
    end
    for k, v in pairs(bttable["minecraft:wooden_slabs"]) do
        configTable["blockweights"][v] = (590/8) * normalization
        configTable["blockstacksizes"][v] = maxStackWeight / configTable["blockweights"][v]
    end
    for k, v in pairs(bttable["minecraft:stairs"]) do
        configTable["blockweights"][v] = (2400/1.5) * normalization
        configTable["blockstacksizes"][v] = maxStackWeight / configTable["blockweights"][v]
    end
    for k, v in pairs(bttable["minecraft:wooden_stairs"]) do
        configTable["blockweights"][v] = (590/3) * normalization
        configTable["blockstacksizes"][v] = maxStackWeight / configTable["blockweights"][v]
    end
    for k, v in pairs(bttable["minecraft:doors"]) do
        configTable["blockweights"][v] = (2883/4) * normalization
        configTable["blockstacksizes"][v] = maxStackWeight / configTable["blockweights"][v]
    end
    for k, v in pairs(bttable["minecraft:wooden_doors"]) do
        configTable["blockweights"][v] = (590/16) * normalization
        configTable["blockstacksizes"][v] = maxStackWeight / configTable["blockweights"][v]
    end
    for k, v in pairs(bttable["minecraft:trapdoors"]) do
        configTable["blockweights"][v] = (2883/8) * normalization
        configTable["blockstacksizes"][v] = maxStackWeight / configTable["blockweights"][v]
    end
    for k, v in pairs(bttable["minecraft:wooden_trapdoors"]) do
        configTable["blockweights"][v] = (590/32) * normalization
        configTable["blockstacksizes"][v] = maxStackWeight / configTable["blockweights"][v]
    end
    for k, v in pairs(bttable["minecraft:buttons"]) do
        configTable["blockweights"][v] = (2400*.012) * normalization
        configTable["blockstacksizes"][v] = maxStackWeight / configTable["blockweights"][v]
    end
    for k, v in pairs(bttable["minecraft:wooden_buttons"]) do
        configTable["blockweights"][v] = (.012*590/4) * normalization
        configTable["blockstacksizes"][v] = maxStackWeight / configTable["blockweights"][v]
    end
    for k, v in pairs(bttable["minecraft:beds"]) do
        configTable["blockweights"][v] = 3*((590/4)+(1314)) * normalization
        configTable["blockstacksizes"][v] = maxStackWeight / configTable["blockweights"][v]
    end
    for k, v in pairs(bttable["minecraft:shulker_boxes"]) do
        configTable["blockweights"][v] = 10 * normalization
        configTable["blockstacksizes"][v] = maxStackWeight / configTable["blockweights"][v]
    end
    for k, v in pairs(bttable["minecraft:flowers"]) do
        configTable["blockweights"][v] = 80*(.1) * normalization
        configTable["blockstacksizes"][v] = maxStackWeight / configTable["blockweights"][v]
    end
    for k, v in pairs(bttable["minecraft:tall_flowers"]) do
        configTable["blockweights"][v] = 160*(.1) * normalization
        configTable["blockstacksizes"][v] = maxStackWeight / configTable["blockweights"][v]
    end
    for k, v in pairs(bttable["minecraft:fences"]) do
        configTable["blockweights"][v] = (590/4)*.08 * normalization
        configTable["blockstacksizes"][v] = maxStackWeight / configTable["blockweights"][v]
    end
    for k, v in pairs(bttable["minecraft:saplings"]) do
        configTable["blockweights"][v] = 160*(.1) * normalization
        configTable["blockstacksizes"][v] = maxStackWeight / configTable["blockweights"][v]
    end
    for k, v in pairs(bttable["minecraft:banners"]) do
        configTable["blockweights"][v] = (1314/8) * normalization
        configTable["blockstacksizes"][v] = maxStackWeight / configTable["blockweights"][v]
    end
    for k, v in pairs(bttable["minecraft:rails"]) do
        configTable["blockweights"][v] = (2883/16) * normalization
        configTable["blockstacksizes"][v] = maxStackWeight / configTable["blockweights"][v]
    end
    for k, v in pairs(bttable["minecraft:carpets"]) do
        configTable["blockweights"][v] = (1314/16) * normalization
        configTable["blockstacksizes"][v] = maxStackWeight / configTable["blockweights"][v]
    end
    for k, v in pairs(bttable["minecraft:spruce_logs"]) do
        configTable["blockweights"][v] = 480 * normalization
        configTable["blockstacksizes"][v] = maxStackWeight / configTable["blockweights"][v]
    end
    for k, v in pairs(bttable["minecraft:jungle_logs"]) do
        configTable["blockweights"][v] = 560 * normalization
        configTable["blockstacksizes"][v] = maxStackWeight / configTable["blockweights"][v]
    end
    for k, v in pairs(bttable["minecraft:acacia_logs"]) do
        configTable["blockweights"][v] = 770 * normalization
        configTable["blockstacksizes"][v] = maxStackWeight / configTable["blockweights"][v]
    end
    for k, v in pairs(bttable["minecraft:birch_logs"]) do
        configTable["blockweights"][v] = 670 * normalization
        configTable["blockstacksizes"][v] = maxStackWeight / configTable["blockweights"][v]
    end
    for k, v in pairs(bttable["minecraft:dark_oak_logs"]) do
        configTable["blockweights"][v] = 740 * normalization
        configTable["blockstacksizes"][v] = maxStackWeight / configTable["blockweights"][v]
    end
    for k, v in pairs(bttable["minecraft:wooden_pressure_plates"]) do
        configTable["blockweights"][v] = 590/9 * normalization
        configTable["blockstacksizes"][v] = maxStackWeight / configTable["blockweights"][v]
    end
    for k, v in pairs(bttable["minecraft:impermeable"]) do
        configTable["blockweights"][v] = 1922 * normalization
        configTable["blockstacksizes"][v] = maxStackWeight / configTable["blockweights"][v]
    end
end

function processItemTags(ittable)
    for k, v in pairs(ittable["fabric:swords"]) do --same as in processBlockTags
        configTable["itemweights"][v] = 3 * normalization * 100
        configTable["itemstacksizes"][v] = 1
    end
    for k, v in pairs(ittable["fabric:pickaxes"]) do
        configTable["itemweights"][v] = 3.1 * normalization * 100
        configTable["itemstacksizes"][v] = 1
    end
    for k, v in pairs(ittable["fabric:axes"]) do
        configTable["itemweights"][v] = 2.9 * normalization * 100
        configTable["itemstacksizes"][v] = 1
    end
    for k, v in pairs(ittable["fabric:hoes"]) do
        configTable["itemweights"][v] = 3.3 * normalization * 100
        configTable["itemstacksizes"][v] = 1
    end
    for k, v in pairs(ittable["fabric:shovels"]) do
        configTable["itemweights"][v] = 2.8 * normalization * 100
        configTable["itemstacksizes"][v] = 1
    end
    for k, v in pairs(ittable["minecraft:coals"]) do
        configTable["itemweights"][v] = 721 * normalization
        configTable["itemstacksizes"][v] = maxStackWeight / configTable["itemweights"][v]
    end
    for k, v in pairs(ittable["minecraft:boats"]) do
        configTable["itemweights"][v] = 590*5/9 * normalization
        configTable["itemstacksizes"][v] = maxStackWeight / configTable["itemweights"][v]
    end
    for k, v in pairs(ittable["minecraft:lectern_books"]) do
        configTable["itemweights"][v] = 80.1 * normalization
        configTable["itemstacksizes"][v] = maxStackWeight / configTable["itemweights"][v]
    end
    for k, v in pairs(ittable["minecraft:arrows"]) do
        configTable["itemweights"][v] = 590/8 * normalization
        configTable["itemstacksizes"][v] = maxStackWeight / configTable["itemweights"][v]
    end
    for k, v in pairs(ittable["minecraft:fishes"]) do
        configTable["itemweights"][v] = 400/9 * normalization
        configTable["itemstacksizes"][v] = maxStackWeight / configTable["itemweights"][v]
    end
    for k, v in pairs(ittable["minecraft:music_discs"]) do
        configTable["itemweights"][v] = .15 * normalization
        configTable["itemstacksizes"][v] = maxStackWeight / configTable["itemweights"][v]
    end
end

function exceptions() --manually specifiying values per id
    configTable["blockslowdownfactor"]["minecraft:magma_block"] = 3
    configTable["enchantmentsakesslowdownignorant"]["minecraft:magma_block"] = "minecraft:fire_protection"
    configTable["itemweights"]["minecraft:charcoal"] = 270/9 * normalization
    configTable["itemweights"]["minecraft:diamond"] = 3514 * (1/9) * normalization
    configTable["itemweights"]["minecraft:gold_ingot"] = 19300 * (1/9) * normalization
    configTable["itemweights"]["minecraft:iron_ingot"] = 2883 * (1/9) * normalization
    configTable["itemweights"]["minecraft:netherite_ingot"] = (4*(19300 + (19050/3))) * (1/9) * normalization
    configTable["itemweights"]["minecraft:cobblestone"] = 2400 * (.6) * normalization
    configTable["itemweights"]["minecraft:gravel"] = 1800 * normalization
    configTable["itemweights"]["minecraft:leather"] = 860 * (1/9) * normalization
    configTable["blockweights"]["minecraft:hay_block"] = 384 * normalization
    configTable["blockstacksizes"]["minecraft:hay_block"] = maxStackWeight / configTable["blockweights"]["minecraft:hay_block"]
    configTable["blockweights"]["minecraft:stone"] = 2400 * normalization
    configTable["blockstacksizes"]["minecraft:stone"] = maxStackWeight / configTable["blockweights"]["minecraft:stone"]
    configTable["blockweights"]["minecraft:iron_block"] = 2883 * normalization
    configTable["blockstacksizes"]["minecraft:iron_block"] = maxStackWeight / configTable["blockweights"]["minecraft:iron_block"]
    configTable["blockweights"]["minecraft:gold_block"] = 19300 * normalization
    configTable["blockstacksizes"]["minecraft:gold_block"] = maxStackWeight / configTable["blockweights"]["minecraft:gold_block"]
    configTable["blockweights"]["minecraft:diamond_block"] = 3514 * normalization
    configTable["blockstacksizes"]["minecraft:diamond_block"] = maxStackWeight / configTable["blockweights"]["minecraft:diamond_block"]
    configTable["blockweights"]["minecraft:netherite_block"] = (4*(19300 + (19050/3))) * normalization
    configTable["blockstacksizes"]["minecraft:netherite_block"] = maxStackWeight / configTable["blockweights"]["minecraft:netherite_block"]
end

function makeSingleStackBlocksHeavy(btable)
    for k, v in pairs(btable) do
        if v == 1 then
            configTable["blockweights"][k] = 9
        end
    end
end

function ensureNonstackablesRemain(itable)
    for k, v in pairs(itable) do
        if v == 1 then
            configTable["itemstacksizes"][k] = 1
        elseif v > 64 then
            configTable["itemstacksizes"][k] = 64
        end
    end
end

function armour()
    configTable["itemweights"]["minecraft:golden_helmet"] = 19300 * (5/9) * normalization
    configTable["itemweights"]["minecraft:golden_chestplate"] = 19300 * (8/9) * normalization
    configTable["itemweights"]["minecraft:golden_leggings"] = 19300 * (7/9) * normalization
    configTable["itemweights"]["minecraft:golden_boots"] = 19300 * (4/9) * normalization
    configTable["itemweights"]["minecraft:chainmail_helmet"] = (2883/100) * (5/9) * normalization
    configTable["itemweights"]["minecraft:chainmail_chestplate"] = (2883/100) * (8/9) * normalization
    configTable["itemweights"]["minecraft:chainmail_leggings"] = (2883/100) * (7/9) * normalization
    configTable["itemweights"]["minecraft:chainmail_boots"] = (2883/100) * (4/9) * normalization
    configTable["itemweights"]["minecraft:iron_helmet"] = 2883 * (5/9) * normalization
    configTable["itemweights"]["minecraft:iron_chestplate"] = 2883 * (8/9) * normalization
    configTable["itemweights"]["minecraft:iron_leggings"] = 2883 * (7/9) * normalization
    configTable["itemweights"]["minecraft:iron_boots"] = 2883 * (4/9) * normalization
    configTable["itemweights"]["minecraft:leather_helmet"] = 860 * (5/9) * normalization
    configTable["itemweights"]["minecraft:leather_chestplate"] = 860 * (8/9) * normalization
    configTable["itemweights"]["minecraft:leather_leggings"] = 860 * (7/9) * normalization
    configTable["itemweights"]["minecraft:leather_boots"] = 860 * (4/9) * normalization
    configTable["itemweights"]["minecraft:diamond_helmet"] = 3514 * (5/9) * normalization
    configTable["itemweights"]["minecraft:diamond_chestplate"] = 3514 * (8/9) * normalization
    configTable["itemweights"]["minecraft:diamond_leggings"] = 3514 * (7/9) * normalization
    configTable["itemweights"]["minecraft:diamond_boots"] = 3514 * (4/9) * normalization
    configTable["itemweights"]["minecraft:netherite_helmet"] = (4*(19300 + (19050/3))) * (5/9) * normalization --ancient debris I'm saying has same density of uranium, hence the 19050
    configTable["itemweights"]["minecraft:netherite_chestplate"] = (4*(19300 + (19050/3))) * (8/9) * normalization
    configTable["itemweights"]["minecraft:netherite_leggings"] = (4*(19300 + (19050/3))) * (7/9) * normalization
    configTable["itemweights"]["minecraft:netherite_boots"] = (4*(19300 + (19050/3))) * (4/9) * normalization
end

