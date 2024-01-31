package net.stellarica.server.material.item

import net.stellarica.server.StellaricaServer.Companion.identifier
import net.stellarica.server.material.item.type.CustomItemType
import net.stellarica.server.material.item.type.ItemType
import net.stellarica.server.util.asMiniMessage
import net.stellarica.server.util.extension.toNamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable
import org.bukkit.persistence.PersistentDataType

class ItemPower private constructor(val stack: ItemStack, private val type: CustomItem) {
	val maxPower: Int
		get() = type.maxPower

	var power: Int
		get() = stack.itemMeta.persistentDataContainer.get(key, PersistentDataType.INTEGER) ?: 0
		set(value) {

			val newPower = value.coerceIn(0, maxPower)

			stack.lore((stack.lore() ?: mutableListOf()).also {
				it[it.lastIndex] = "<gray>Power: $newPower/${maxPower}".asMiniMessage
			})

			stack.editMeta {
				it.persistentDataContainer.set(key, PersistentDataType.INTEGER, newPower)
				// In order to update the durability bar we need to set it to *not* be unbreakable
				it.isUnbreakable = false
				(it as Damageable).damage = ((1 - (newPower.toFloat() / maxPower)) * stack.type.maxDurability).toInt()
			}
		}

	companion object {
		val ItemStack.power: ItemPower?
			get() {
				val type = ItemType.of(this) as? CustomItemType ?: return null
				return ItemPower(this, type.item)
			}

		private val key = identifier("item_power").toNamespacedKey()
	}
}

