package net.stellarica.server.material.custom.feature.blasters

import net.stellarica.server.material.custom.item.power
import net.stellarica.server.material.type.item.CustomItemType
import net.stellarica.server.material.type.item.ItemType
import net.stellarica.server.util.extension.sendRichActionBar
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent

object BlasterListener : Listener {
	@EventHandler
	fun onPlayerClick(event: PlayerInteractEvent) {
		if (event.action != Action.LEFT_CLICK_BLOCK && event.action != Action.LEFT_CLICK_AIR) return
		val held = event.player.inventory.itemInMainHand
		val heldType = (ItemType.of(held) as? CustomItemType)?.item ?: return
		val blasterType = BlasterType.values().firstOrNull { it.item == heldType } ?: return
		event.isCancelled = true

		if (held.power!! < blasterType.powerCost) {
			event.player.sendRichActionBar("<red>Out of Power!")
			return
		}

		if (event.player.getCooldown(held.type) > 0) return

		held.power = held.power!! - blasterType.powerCost
		event.player.setCooldown(held.type, blasterType.cooldown)

		blasterType.projectile.shoot(event.player, event.player.eyeLocation.clone().subtract(0.0, 0.3, 0.0))
	}
}