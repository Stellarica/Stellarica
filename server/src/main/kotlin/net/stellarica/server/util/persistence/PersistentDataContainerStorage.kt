package net.stellarica.server.util.persistence

import kotlinx.serialization.serializer
import net.minecraft.resources.ResourceLocation
import net.stellarica.nbt.getCompoundTag
import net.stellarica.nbt.serialization.Nbt
import net.stellarica.nbt.serialization.decodeFromNbtElement
import net.stellarica.nbt.serialization.encodeToNbtElement
import net.stellarica.nbt.setCompoundTag
import net.stellarica.server.StellaricaServer.Companion.klogger
import org.bukkit.persistence.PersistentDataContainer
import kotlin.reflect.KClass

abstract class PersistentDataContainerStorage : PersistentStorage {

	val types = mutableMapOf<ResourceLocation, KClass<*>>()
	override fun register(key: ResourceLocation, value: KClass<*>) {
		try {
			serializer(value.java)
		} catch (e: Exception) {
			klogger.error { "Type $value is not serializable, and can't be registered for persistent storage!" }
			throw e
		}
		types[key] = value
	}

	override fun get(key: ResourceLocation): Any? {
		if (!isValid()) throw IllegalStateException("Persistent storage is not valid!")
		val type = types[key] ?: throw IllegalArgumentException("No type registered for $key")
		val data = getPersistentDataContainer().getCompoundTag()

		return Nbt.decodeFromNbtElement(serializer(type.java), data.getCompound(key.toString()))
	}

	override fun set(key: ResourceLocation, value: Any) {
		if (!isValid()) throw IllegalStateException("Persistent storage is not valid!")
		val type = types[key] ?: throw IllegalArgumentException("No type registered for $key")
		if (!type.isInstance(value)) throw IllegalArgumentException("Value $value is not of type $type")
		val data = getPersistentDataContainer().getCompoundTag()

		data.put(key.toString(), Nbt.encodeToNbtElement(serializer(type.java), value))

		getPersistentDataContainer().setCompoundTag(data)
	}

	protected abstract fun getPersistentDataContainer(): PersistentDataContainer
	protected abstract fun isValid(): Boolean
}

