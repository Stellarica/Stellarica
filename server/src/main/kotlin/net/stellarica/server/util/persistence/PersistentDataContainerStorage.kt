package net.stellarica.server.util.persistence

import net.minecraft.resources.ResourceLocation
import net.stellarica.nbt.getCompoundTag
import net.stellarica.nbt.serialization.Nbt
import net.stellarica.nbt.serialization.decodeFromNbtElement
import net.stellarica.nbt.serialization.encodeToNbtElement
import net.stellarica.nbt.setCompoundTag
import org.bukkit.persistence.PersistentDataContainer

abstract class PersistentDataContainerStorage {

	inline operator fun <reified T> get(key: ResourceLocation): T? {
		if (!isValid()) throw IllegalStateException("Persistent storage is not valid!")
		val data = getPersistentDataContainer().getCompoundTag()
		return data.get(key.toString())?.let { Nbt.decodeFromNbtElement(it) }
	}

	 inline fun <reified T> set(key: ResourceLocation, value: T) {
		if (!isValid()) throw IllegalStateException("Persistent storage is not valid!")
		val data = getPersistentDataContainer().getCompoundTag()
		data.put(key.toString(), Nbt.encodeToNbtElement(value))
		getPersistentDataContainer().setCompoundTag(data)
	}

	abstract fun getPersistentDataContainer(): PersistentDataContainer
	abstract fun isValid(): Boolean
}

