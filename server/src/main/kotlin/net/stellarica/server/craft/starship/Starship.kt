package net.stellarica.server.craft.starship

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.phys.Vec3
import net.stellarica.common.util.toVec3i
import net.stellarica.server.StellaricaServer.Companion.pilotedCrafts
import net.stellarica.server.StellaricaServer.Companion.plugin
import net.stellarica.server.craft.Craft
import net.stellarica.server.craft.starship.control.ShipControlHotbar
import net.stellarica.server.craft.starship.subsystem.Subsystem
import net.stellarica.server.craft.starship.subsystem.armor.ArmorSubsystem
import net.stellarica.server.craft.starship.subsystem.shield.ShieldSubsystem
import net.stellarica.server.craft.starship.subsystem.thruster.ThrusterSubsystem
import net.stellarica.server.craft.starship.subsystem.weapon.WeaponSubsystem
import net.stellarica.server.util.Tasks
import net.stellarica.server.util.extension.div
import net.stellarica.server.util.extension.toBlockPos
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockExplodeEvent
import kotlin.math.roundToInt

class Starship(origin: BlockPos, direction: Direction, world: ServerLevel, owner: Player? = null) :
	Craft(origin, direction, world, owner), Listener {

	val subsystems: Set<Subsystem>
		get() = setOf(weapons, shields, armor, thrusters)


	val weapons = WeaponSubsystem(this)
	val shields = ShieldSubsystem(this)
	val armor = ArmorSubsystem(this)
	var thrusters = ThrusterSubsystem(this)

	var heading: Vec3 = Vec3.ZERO

	var pilot: Player? = null
		private set


	/**
	 * Activates the craft and registers it in [pilotedCrafts]
	 * @param pilot the pilot, who controls the ship
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
			tickCraft()
		}

		return true
	}

	fun deactivateCraft(): Boolean {
		messagePilot("<green>Unpiloting craft")
		val pass = passengers.toMutableSet()
		subsystems.forEach { it.onShipUnpiloted() }
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

	private fun tickCraft() {
		subsystems.forEach { it.onShipTick() }
		val thrust = thrusters.calculateActualThrust(heading)
		val move = thrust / mass.toDouble()
		move(move.toVec3i())
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
