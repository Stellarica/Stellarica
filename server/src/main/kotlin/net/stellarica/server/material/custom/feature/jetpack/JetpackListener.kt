package net.stellarica.server.material.custom.feature.jetpack

import net.stellarica.server.material.custom.item.power
import net.stellarica.server.material.custom.item.type.MiscCustomItems
import net.stellarica.server.material.type.item.ItemType
import net.stellarica.server.util.extension.sendRichActionBar
import org.bukkit.Particle
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.inventory.ItemStack

object JetpackListener : Listener {
	private const val powerPerTick = 5
	private val type = ItemType.of(MiscCustomItems.JETPACK)

	@EventHandler
	fun onPlayerMove(event: PlayerMoveEvent) {
		@Suppress("DEPRECATION")
		if (!event.player.isSneaking || event.player.isOnGround) return
		val pack = getPack(event.player) ?: return
		if (pack.power!! < powerPerTick) {
			event.player.sendRichActionBar("<red>Jetpack out of Power!")
			return
		}
		pack.power = pack.power!! - powerPerTick
		event.player.velocity = event.player.velocity.apply { this.y = 0.3 }

		event.player.world.spawnParticle(Particle.FLAME, event.player.location, 2, 0.0, 0.0, 0.0, 0.0)
		event.player.world.spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, event.player.location, 3, 0.1, 0.0, 0.1, 0.02)
		event.player.playSound(event.player.location, org.bukkit.Sound.BLOCK_FIRE_AMBIENT, 1f, 1f)
	}

	private fun getPack(player: Player): ItemStack? {
		val plate = player.inventory.chestplate ?: return null
		return if (ItemType.of(plate) == type) plate
		else null
	}
}