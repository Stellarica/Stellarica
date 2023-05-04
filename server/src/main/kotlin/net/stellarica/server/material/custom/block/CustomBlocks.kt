package net.stellarica.server.material.custom.block

import net.minecraft.resources.ResourceLocation
import net.stellarica.server.material.custom.block.type.CustomBlockDef
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaGetter

object CustomBlocks {
	val all = CustomBlockDef::class.sealedSubclasses.map { def ->
		def.memberProperties.mapNotNull { prop ->
			// cursed, cursed, cursed! there are better reflection-y ways to do this
			// but this works:tm: for now
			prop.javaGetter!!.invoke(def.objectInstance!!) as? CustomBlock
		}
	}.flatten()

	private val idMap = all.associateBy { it.id }
	fun byId(id: ResourceLocation): CustomBlock? = idMap[id]
}