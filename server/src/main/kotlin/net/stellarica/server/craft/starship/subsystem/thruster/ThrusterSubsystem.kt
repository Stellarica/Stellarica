package net.stellarica.server.craft.starship.subsystem.thruster;

import net.minecraft.core.Direction
import net.minecraft.core.Vec3i
import net.stellarica.common.util.OriginRelative
import net.stellarica.server.craft.starship.Starship
import net.stellarica.server.craft.starship.subsystem.Subsystem
import net.stellarica.server.multiblock.data.ThrusterMultiblockData
import net.stellarica.server.util.extension.div
import net.stellarica.server.util.extension.plus
import net.stellarica.server.util.extension.times
import kotlin.math.roundToInt

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

	fun stepThrusterWarmup(targetDir: Vec3i) {
		for (thruster in thrusters.mapNotNull { ship.getMultiblock(it) }) {
			val type = ThrusterType.values().first { it.multiblock == thruster.type}
			val data = (thruster.data as ThrusterMultiblockData)
			if (TODO()) {
				// warming up
				data.warmupPercentage += type.warmupSpeed
			}
			else {
				// cooling down
				data.warmupPercentage = (data.warmupPercentage - type.warmupSpeed).coerceAtLeast(0)
			}
		}
	}

	fun calculateTotalThrust(): Vec3i {
		var thrust = Vec3i.ZERO
		for (thruster in thrusters.mapNotNull {ship.getMultiblock(it)}) {
			val type = ThrusterType.values().first { it.multiblock == thruster.type}
			val data = (thruster.data as ThrusterMultiblockData)
			thrust += thruster.direction.normal * (type.maxThrust * data.warmupPercentage)
		}
		return thrust / ship.mass.roundToInt()
	}
}

