package net.stellarica.server.crafts.starships

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.Vec3i
import net.minecraft.server.level.ServerLevel
import net.stellarica.common.utils.toVec3
import net.stellarica.server.StellaricaServer.Companion.pilotedCrafts
import net.stellarica.server.StellaricaServer.Companion.plugin
import net.stellarica.server.crafts.Craft
import net.stellarica.server.crafts.starships.control.ShipControlHotbar
import net.stellarica.server.crafts.starships.subsystems.Subsystem
import net.stellarica.server.crafts.starships.subsystems.armor.ArmorSubsystem
import net.stellarica.server.crafts.starships.subsystems.shields.ShieldSubsystem
import net.stellarica.server.crafts.starships.subsystems.weapons.WeaponSubsystem
import net.stellarica.server.utils.Tasks
import net.stellarica.server.utils.extensions.asMiniMessage
import net.stellarica.server.utils.extensions.toBlockPos
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockExplodeEvent
import kotlin.math.roundToInt

/**
 * Base Starship class
 */
class Starship(origin: BlockPos, direction: Direction, world: ServerLevel, owner: Player? = null) :
	Craft(origin, direction, world, owner), Listener {

	val subsystems: Set<Subsystem>
		get() = setOf(weapons, shields, armor)


	val weapons = WeaponSubsystem(this)
	val shields = ShieldSubsystem(this)
	val armor = ArmorSubsystem(this)

	var velocity: Vec3i = Vec3i.ZERO

	/**
	 * The player who is currently piloting this craft
	 */
	var pilot: Player? = null
		private set


	/**
	 * Activates the craft and registers it in [pilotedCrafts]
	 * @param pilot the pilot, who controls the ship
	 * @throws AlreadyPilotedException if the ship is already piloted
	 * @return whether the craft was successfully activated
	 */
	fun activateCraft(pilot: Player): Boolean {
		// Determine passengers, pilot
		if (this.pilot != null) return false
		if (this.detectedBlockCount == 0) {
			pilot.sendRichMessage("<red>Cannot pilot empty craft, detect it first!")
			return false
		}
		if (!contains(pilot.location.toBlockPos())) {
			pilot.sendRichMessage("<red>You must be inside the craft to pilot it!")
			return false
		}
		passengers.add(pilot)
		this.pilot = pilot
		Bukkit.getOnlinePlayers().filter { it.world == world }.forEach {
			if (contains(it.location.toBlockPos()) && it != pilot) {
				passengers.add(it)
				it.sendRichMessage("<gray>Now riding a craft piloted by ${pilot.name}!")
			}
		}


		plugin.server.pluginManager.registerEvents(this, plugin)
		ShipControlHotbar.openMenu(pilot)
		subsystems.forEach { it.onShipPiloted() }

		pilotedCrafts.add(this)
		messagePilot("<green>Piloted craft!")

		Tasks.syncRepeat(5, 5) {
			if (passengers.size <= 0) { // jank way to detect unpiloting
				this.cancel()
				return@syncRepeat
			}
			// there's gotta be a better way to check this
			if (velocity.x == 0 && velocity.y == 0 && velocity.z == 0) return@syncRepeat
			move(velocity)
			val percent = shields.shieldHealth / shields.maxShieldHealth.toFloat().coerceAtLeast(1f)
			val num = (percent * 20).roundToInt().coerceAtMost(20)

			pilot.sendActionBar(("<dark_gray>[<gray>" + "|".repeat(20 - num) + "<blue>|".repeat(num) + "<dark_gray>]").asMiniMessage)
		}

		return true
	}

	fun deactivateCraft(): Boolean {
		messagePilot("<green>Unpiloting craft")
		val pass = passengers.toMutableSet()
		subsystems.forEach { it.onShipUnpiloted() }
		// close the ship HUD. this is really dumb
		// todo: fix
		this.passengers = pass
		this.passengers.clear()
		// close the hotbar menu
		ShipControlHotbar.closeMenu(pilot!!)

		// todo: maybe find a better way than spamming this for everything we register to
		// reflection?
		BlockExplodeEvent.getHandlerList().unregister(this)

		pilot = null
		passengers.clear()
		pilotedCrafts.remove(this)
		return true
	}

	@EventHandler
	fun onBlockExplode(event: BlockExplodeEvent) {
		// todo: fix bad range check
		if (origin.distSqr(event.block.toBlockPos()) < 500 && contains(event.block.toBlockPos())) {
			if (shields.shieldHealth > 0) {
				event.isCancelled = true
				shields.damage(event.block.location, event.yield.roundToInt())
			} else {
				detectedBlocks.remove(event.block.toBlockPos())
			}
		}
	}
}
