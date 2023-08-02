package net.stellarica.server

import net.stellarica.server.material.custom.block.CustomBlock
import net.stellarica.server.material.custom.block.type.CustomBlockDef
import net.stellarica.server.material.custom.item.CustomItem
import net.stellarica.server.material.custom.item.type.CustomItemDef
import net.stellarica.server.multiblock.MultiblockType
import net.stellarica.server.multiblock.type.MultiblockDef
import net.stellarica.server.util.Jank

@Suppress("Unused")
val CustomItems = Jank(CustomItemDef::class, CustomItem::id)
val CustomBlocks = Jank(CustomBlockDef::class, CustomBlock::id)
val Multiblocks = Jank(MultiblockDef::class, MultiblockType::id)