package net.stellarica.server.material.item

import net.kyori.adventure.text.Component
import net.stellarica.server.StellaricaServer.Companion.plugin
import net.stellarica.server.material.item.type.CustomItemType
import net.stellarica.server.material.item.type.ItemType
import net.stellarica.server.util.asMiniMessage
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable
import org.bukkit.persistence.PersistentDataType
import kotlin.math.max
import kotlin.math.min


/**
 * Whether this ItemStack is a powerable custom item
 */
val ItemStack.isPowerable: Boolean
	get() = (ItemType.of(this) as? CustomItemType)?.item?.isPowerable ?: false

/**
 * The maximum power this item can hold
 * @see CustomItem.maxPower
 */
val ItemStack.maxPower: Int?
	get() = (ItemType.of(this) as? CustomItemType)?.item?.maxPower

/**
 * The amount of power this item is currently holding.
 * Clamped between 0 and this item's [maxPower]
 * Backed by this ItemStack's PersistentDataContainer
 */
var ItemStack.power: Int?
	get() {
		if (!this.isPowerable) return null
		return this.itemMeta.persistentDataContainer.get(
				NamespacedKey(plugin, "item-power"),
				PersistentDataType.INTEGER
		) ?: 0
	}
	set(value) {
		if (!this.isPowerable) return
		value ?: return

		val newPower = max(min(value, this.maxPower!!), 0)

		val lore: MutableList<Component> = this.lore() ?: mutableListOf()
		val text = "<gray>Power: $newPower/${this.maxPower}"
		lore[lore.lastIndex] = text.asMiniMessage
		this.lore(lore)

		this.editMeta {
			it.persistentDataContainer.set(
					NamespacedKey(plugin, "item-power"),
					PersistentDataType.INTEGER,
					newPower
			)
			// In order to update the durability bar we need to set it to *not* be unbreakable
			it.isUnbreakable = false
			(it as Damageable).damage = ((1 - (newPower.toFloat() / this.maxPower!!)) * this.type.maxDurability).toInt()
		}
	}

