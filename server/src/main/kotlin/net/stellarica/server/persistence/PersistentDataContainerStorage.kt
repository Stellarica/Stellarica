package net.stellarica.server.persistence

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

	 inline operator fun <reified T> set(key: ResourceLocation, value: T) {
		 if (!isValid()) throw IllegalStateException("Persistent storage is not valid!")
		 val pdc = getPersistentDataContainer()
		 val data = pdc.getCompoundTag()
		 data.put(key.toString(), Nbt.encodeToNbtElement(value))
		 pdc.setCompoundTag(data)
	}

	abstract fun getPersistentDataContainer(): PersistentDataContainer
	abstract fun isValid(): Boolean
}

