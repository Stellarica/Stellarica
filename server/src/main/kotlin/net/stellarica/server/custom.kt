package net.stellarica.server

import net.stellarica.server.material.block.CustomBlock
import net.stellarica.server.material.item.CustomItem
import net.stellarica.server.multiblock.MultiblockType
import net.stellarica.server.multiblock.type.MultiblockDef
import net.stellarica.server.util.Jank
import net.stellarica.server.util.Registry


val Multiblocks = Jank(MultiblockDef::class, MultiblockType::id)
val CustomItems = Registry<CustomItem>()
val CustomBlocks = Registry<CustomBlock>()
