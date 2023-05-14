package net.stellarica.server.util.persistence

import net.minecraft.resources.ResourceLocation
import kotlin.reflect.KClass

interface PersistentStorage {
	fun register(key: ResourceLocation, value: KClass<*>)
	operator fun get(key: ResourceLocation): Any?
	operator fun set(key: ResourceLocation, value: Any)

}