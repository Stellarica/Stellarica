package net.stellarica.server.util.persistence

import net.minecraft.resources.ResourceLocation
import net.stellarica.nbt.getCompoundTag
import net.stellarica.nbt.setCompoundTag
import org.bukkit.persistence.PersistentDataContainer
import kotlin.reflect.KClass

abstract class PersistentDataContainerStorage : PersistentStorage {

	val types = mutableMapOf<ResourceLocation, KClass<*>>()
	override fun register(key: ResourceLocation, value: KClass<*>) {
		types[key] = value
	}

	override fun get(key: ResourceLocation): Any? {
		val type = types[key] ?: throw IllegalArgumentException("No type registered for $key")
		val data = getPersistentDataContainer().getCompoundTag()
		data.getCompound(key.toString())

		return TODO()
	}

	override fun set(key: ResourceLocation, value: Any?) {
		val type = types[key] ?: throw IllegalArgumentException("No type registered for $key")
		if (!type.isInstance(value)) throw IllegalArgumentException("Value $value is not of type $type")
		val data = getPersistentDataContainer().getCompoundTag()

		data.put(key.toString(), TODO())

		getPersistentDataContainer().setCompoundTag(data)
	}

	protected abstract fun getPersistentDataContainer(): PersistentDataContainer
}

