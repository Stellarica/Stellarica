package net.stellarica.server.material.custom.item

import net.minecraft.resources.ResourceLocation
import net.stellarica.server.material.custom.item.type.CustomItemDef
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaGetter

@Suppress("Unused") // iea
object CustomItems {
	val all = CustomItemDef::class.sealedSubclasses.map { def ->
		def.memberProperties.mapNotNull { prop ->
			// cursed, cursed, cursed! there are better reflection-y ways to do this
			// but this works:tm: for now
			prop.javaGetter!!.invoke(def.objectInstance!!) as? CustomItem
		}
	}.flatten()

	private val idMap = all.associateBy { it.id }
	fun byId(id: ResourceLocation): CustomItem? = idMap[id]
}