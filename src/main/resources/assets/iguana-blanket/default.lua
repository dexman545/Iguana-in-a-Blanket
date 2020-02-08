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
local maxStackWeight = 100 / 36

--this function must exist and eb named as such. It takes in the table of items, blocks, item tags, and block tags
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
    processBlockTags(table["blocktags"]) --changes the values absed on block tags
    processItemTags(table["itemtags"]) --changes the values based on itemtags
    ensureNonstackablesRemain(table["items"]) --makes sure nonstackable items remain that way
    exceptions() --any exception from block tags are handled here
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

function processBlockTags(bttable)
    for k, v in pairs(bttable["minecraft:planks"]) do --k = block id; v = value; what's in the [] selects for that block tag
        configTable["blockweights"][v] = 2
        configTable["blockstacksizes"][v] = 32
        configTable["blockhardnessscale"][v] = 2
        configTable["blockslowdownfactor"][v] = 0
    end
    for k, v in pairs(bttable["minecraft:ice"]) do
        configTable["blockweights"][v] = maxStackWeight / 17
        configTable["blockstacksizes"][v] = 17
        configTable["blockhardnessscale"][v] = 0.5
        configTable["blockslowdownfactor"][v] = 2
        configTable["enchantmentsakesslowdownignorant"][v] = "minecraft:frost_walker"
    end
    for k, v in pairs(bttable["minecraft:anvil"]) do
        configTable["blockweights"][v] = 10
        configTable["blockstacksizes"][v] = maxStackWeight / configTable["blockweights"][v]
        configTable["blockhardnessscale"][v] = 4
        configTable["blockslowdownfactor"][v] = 0
    end
    for k, v in pairs(bttable["minecraft:banners"]) do
        configTable["blockweights"][v] = 3.0
        configTable["blockstacksizes"][v] = maxStackWeight / configTable["blockweights"][v]
        configTable["blockhardnessscale"][v] = 1.2
        configTable["blockslowdownfactor"][v] = 0
    end
    for k, v in pairs(bttable["minecraft:flower_pots"]) do
        configTable["blockweights"][v] = 0.5
        configTable["blockstacksizes"][v] = maxStackWeight / configTable["blockweights"][v]
        configTable["blockhardnessscale"][v] = 0.9
        configTable["blockslowdownfactor"][v] = 0
    end
    for k, v in pairs(bttable["minecraft:wooden_fences"]) do
        configTable["blockweights"][v] = 5
        configTable["blockstacksizes"][v] = 32
        configTable["blockhardnessscale"][v] = 2
        configTable["blockslowdownfactor"][v] = 0
    end
    for k, v in pairs(bttable["minecraft:wooden_slabs"]) do
        configTable["blockweights"][v] = 1
        configTable["blockstacksizes"][v] = 64
        configTable["blockhardnessscale"][v] = 2
        configTable["blockslowdownfactor"][v] = 0
    end
    for k, v in pairs(bttable["minecraft:small_flowers"]) do
        configTable["blockweights"][v] = 0.1
        configTable["blockstacksizes"][v] = maxStackWeight / configTable["blockweights"][v]
        configTable["blockhardnessscale"][v] = 1
        configTable["blockslowdownfactor"][v] = 0
    end
    for k, v in pairs(bttable["minecraft:logs"]) do
        configTable["blockweights"][v] = 8
        configTable["blockstacksizes"][v] = 16
        configTable["blockhardnessscale"][v] = 2.5
        configTable["blockslowdownfactor"][v] = 0
    end
end

function processItemTags(ittable)
    for k, v in pairs(ittable["fabric:swords"]) do --same as in processBlockTags
        configTable["itemweights"][v] = 3
        configTable["itemstacksizes"][v] = 1
    end
    for k, v in pairs(ittable["fabric:pickaxes"]) do
        configTable["itemweights"][v] = 3.1
        configTable["itemstacksizes"][v] = 1
    end
    for k, v in pairs(ittable["fabric:axes"]) do
        configTable["itemweights"][v] = 2.9
        configTable["itemstacksizes"][v] = 1
    end
    for k, v in pairs(ittable["fabric:hoes"]) do
        configTable["itemweights"][v] = 3.3
        configTable["itemstacksizes"][v] = 1
    end
    for k, v in pairs(ittable["fabric:shovels"]) do
        configTable["itemweights"][v] = 2.8
        configTable["itemstacksizes"][v] = 1
    end
end

function exceptions() --manually specifiying values per id
    configTable["blockslowdownfactor"]["minecraft:magma_block"] = 3
    configTable["enchantmentsakesslowdownignorant"]["minecraft:magma_block"] = "minecraft:fire_protection"
end

function ensureNonstackablesRemain(itable)
    for k, v in pairs(itable) do
        if v == 1 then
            configTable["itemstacksizes"][k] = 1
        end
    end
end