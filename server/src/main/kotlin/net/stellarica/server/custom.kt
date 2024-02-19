package net.stellarica.server

import net.stellarica.server.multiblock.MultiblockType
import net.stellarica.server.multiblock.type.MultiblockDef
import net.stellarica.server.util.Jank


val Multiblocks = Jank(MultiblockDef::class, MultiblockType::id)
