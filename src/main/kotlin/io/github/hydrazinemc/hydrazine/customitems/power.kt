package io.github.hydrazinemc.hydrazine.customitems

import io.github.hydrazinemc.hydrazine.Hydrazine.Companion.plugin
import io.github.hydrazinemc.hydrazine.utils.Tasks
import io.github.hydrazinemc.hydrazine.utils.extensions.asMiniMessage
import net.kyori.adventure.text.Component
import org.bukkit.NamespacedKey
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerItemBreakEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable
import org.bukkit.persistence.PersistentDataType
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * Update this item's durability to match the current [power]/[maxPower]
 */
private fun ItemStack.updatePowerDurability() {
	if (!this.isPowerable) return // todo: throw something?
	this.editMeta {
		// In order to update the durability bar we need to set it to *not* be unbreakable
		it.isUnbreakable = false
		(it as Damageable).damage =
			(this.type.maxDurability - this.power!!.toFloat() / this.customItem!!.maxPower * this.type.maxDurability).roundToInt()
	}
}

/**
 * Whether this ItemStack is a powerable custom item
 */
val ItemStack.isPowerable: Boolean
	get() = (this.customItem?.maxPower ?: -1) > 0

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

/**
 * Cancels powerable items breaking, as their item durability is set by the [power]
 */
class PowerItemBreakCanceller : Listener {
	// Have to cancel damage on powerable items, otherwise ones at 0 power will break
	@EventHandler
	fun preventDamage(event: PlayerItemBreakEvent) {
		if (!event.brokenItem.isPowerable) return
		// https://bukkit.org/threads/playeritembreakevent-cancelling.282678/
		event.brokenItem.amount += 1 // If there's a custom item dupe it's probably because of this
		Tasks.syncDelay(1) { event.brokenItem.updatePowerDurability() }
	}
}
