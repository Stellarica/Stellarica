package net.stellarica.server.multiblock.type

import net.minecraft.resources.ResourceLocation
import net.stellarica.server.multiblock.MultiblockType
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaGetter

object Multiblocks {
	val all = MultiblockDef::class.sealedSubclasses.map { def ->
		def.memberProperties.mapNotNull { prop ->
			// cursed, cursed, cursed! there are better reflection-y ways to do this
			// but this works:tm: for now
			prop.javaGetter!!.invoke(def.objectInstance!!) as? MultiblockType
		}
	}.flatten()

	private val idMap = all.associateBy { it.id }

	fun byId(id: ResourceLocation): MultiblockType? = idMap[id]
}
