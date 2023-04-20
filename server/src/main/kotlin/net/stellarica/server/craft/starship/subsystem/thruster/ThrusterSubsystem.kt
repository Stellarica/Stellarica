package net.stellarica.server.craft.starship.subsystem.thruster;

import net.minecraft.world.phys.Vec3
import net.stellarica.common.util.OriginRelative
import net.stellarica.common.util.toVec3
import net.stellarica.server.craft.starship.Starship
import net.stellarica.server.craft.starship.subsystem.Subsystem
import net.stellarica.server.multiblock.data.ThrusterMultiblockData
import net.stellarica.server.util.extension.div
import net.stellarica.server.util.extension.plus
import net.stellarica.server.util.extension.times
import net.stellarica.server.util.extension.toVector
import kotlin.math.abs

class ThrusterSubsystem(ship: Starship): Subsystem(ship) {
	val thrusters = mutableSetOf<OriginRelative>()
	override fun onShipPiloted() {
		thrusters.clear()
		for (mb in ship.multiblocks) {
			if (ship.getMultiblock(mb)?.type in ThrusterType.values().map { it.multiblock }) {
				thrusters.add(mb)
			}
		}
	}

	fun stepThrusterWarmup(targetDir: Vec3) {
		for (thruster in thrusters.mapNotNull { ship.getMultiblock(it) }) {
			val type = ThrusterType.values().first { it.multiblock == thruster.type}
			val data = (thruster.data as ThrusterMultiblockData)

			val dir = thruster.direction.normal.toVec3().toVector()
			val angleBetween = targetDir.toVector().angle(dir)

			if (abs(angleBetween) <= Math.PI / 4) {
				// warming up
				data.warmupPercentage += type.warmupSpeed
			}
			else {
				// cooling down
				data.warmupPercentage = (data.warmupPercentage - type.warmupSpeed).coerceAtLeast(0)
			}
		}
	}

	private fun calculateTotalThrust(): Vec3 {
		var thrust = Vec3.ZERO
		for (thruster in thrusters.mapNotNull {ship.getMultiblock(it)}) {
			val type = ThrusterType.values().first { it.multiblock == thruster.type}
			val data = (thruster.data as ThrusterMultiblockData)
			thrust += thruster.direction.normal.toVec3() * (type.maxThrust * data.warmupPercentage).toDouble()
		}
		return thrust
	}

	fun calculateActualThrust(dir: Vec3): Vec3 {
		// though it would be nice to be able to just use the result of calculateTotalThrust as the actual thrust,
		// depending on how a ship is constructed it might not be able to travel at certain angles
		// to "fix" that, if the actual thrust is at least somewhat close to the target direction, it will
		// give thrust in the target direction, with a small penalty to thrust amount.
		val originalThrust = calculateTotalThrust()

		// painful type conversions aaaaaa
		val angleBetween = abs(originalThrust.toVector().angle(dir.toVector()))

		if (angleBetween < Math.PI / 8.0) {
			// no penalty
			return dir.normalize().scale(originalThrust.length())
		} else if(angleBetween < Math.PI / 6.0) {
			// half thrust
			return dir.normalize().scale(originalThrust.length() / 1.5)
		}

		// pointing rather far away from the actual thrust, but the actual thrust still applies
		return originalThrust
	}
}

