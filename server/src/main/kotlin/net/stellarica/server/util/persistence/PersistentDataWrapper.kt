package net.stellarica.server.util.persistence

import net.minecraft.nbt.CompoundTag
import org.bukkit.craftbukkit.v1_19_R3.persistence.CraftPersistentDataContainer
import org.bukkit.persistence.PersistentDataContainer

object PersistentDataWrapper {
	operator fun get(pdc: PersistentDataContainer): CompoundTag {
		return (pdc as CraftPersistentDataContainer).toTagCompound()
	}
	operator fun set(pdc: PersistentDataContainer, value: CompoundTag) {
		(pdc as CraftPersistentDataContainer).clear()
		pdc.putAll(value)
	}
}