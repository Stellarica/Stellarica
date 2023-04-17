package net.stellarica.server.multiblock.type

import net.minecraft.resources.ResourceLocation
import net.stellarica.server.multiblock.MultiblockType

object Multiblocks {
	val all = MultiblockDef::class.sealedSubclasses.map { def ->
		def.java.declaredFields.mapNotNull { field ->
			field.get(def.objectInstance) as? MultiblockType
		}
	}.flatten()

	private val idMap = all.associateBy { it.id }

	fun byId(id: ResourceLocation): MultiblockType? = idMap[id]
}
