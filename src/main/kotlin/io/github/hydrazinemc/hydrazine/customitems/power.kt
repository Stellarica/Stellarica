package io.github.hydrazinemc.hydrazine.customitems

import io.github.hydrazinemc.hydrazine.Hydrazine.Companion.plugin
import io.github.hydrazinemc.hydrazine.utils.extensions.asMiniMessage
import net.kyori.adventure.text.Component
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable
import org.bukkit.persistence.PersistentDataType
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * Update this item's durability to match the current [power]/[maxPower]
 * @return whether it was successful
 */
fun ItemStack.updatePowerDurability(): Boolean {
	if (!this.isPowerable || this.itemMeta !is Damageable) return false;
	this.editMeta {
		// In order to update the durability bar we need to set it to *not* be unbreakable
		it.isUnbreakable = false
		(it as Damageable).damage =
			(this.type.maxDurability - this.power!!.toFloat() /
					this.customItem!!.maxPower * this.type.maxDurability).roundToInt()
	}
	return true
}

/**
 * Whether this ItemStack is a powerable custom item
 */
val ItemStack.isPowerable: Boolean
	get() = this.customItem?.isPowerable ?: false

/**
 * The maximum power this item can hold
 * @see CustomItem.maxPower
 */
val ItemStack.maxPower: Int?
	get() = this.customItem?.maxPower


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
		if (!this.isPowerable) throw return
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
		}
		this.updatePowerDurability()
	}

