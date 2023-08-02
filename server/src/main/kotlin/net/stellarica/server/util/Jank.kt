package net.stellarica.server.util

import net.minecraft.resources.ResourceLocation
import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaGetter

class Jank<T>(private val cl: KClass<*>, private val idg: T.() -> ResourceLocation): Collection<T> {
	operator fun get(id: ResourceLocation): T? = idMap[id]

	private val all: List<T> by lazy {
		cl.sealedSubclasses.map { def ->
			def.memberProperties.mapNotNull { prop ->
				// cursed, cursed, cursed! there are better reflection-y ways to do this
				// but this works:tm: for now
				@Suppress("UNCHECKED_CAST")
				prop.javaGetter!!.invoke(def.objectInstance!!) as? T
			}
		}.flatten()
	}

	private val idMap = all.associateBy { it.idg() }
	override val size: Int
		get() = all.size
	override fun isEmpty(): Boolean = all.isEmpty()
	override fun iterator(): Iterator<T> = all.iterator()
	override fun containsAll(elements: Collection<T>): Boolean = all.containsAll(elements)
	override fun contains(element: T): Boolean = all.contains(element)
}