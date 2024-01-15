package net.stellarica.server.util

import net.minecraft.resources.ResourceLocation
import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaGetter

class Jank<T>(private val cl: KClass<*>, private val idg: T.() -> ResourceLocation) :
	Collection<T> by cl.sealedSubclasses.map({ def: KClass<out Any> ->
		def.memberProperties.mapNotNull { prop ->
			// cursed, cursed, cursed! there are better reflection-y ways to do this
			// but this works:tm: for now
			@Suppress("UNCHECKED_CAST")
			prop.javaGetter!!.invoke(def.objectInstance!!) as? T
		}
	}).flatten() {
	operator fun get(id: ResourceLocation): T? = idMap[id]

	private val idMap = this.associateBy { it.idg() }
}
