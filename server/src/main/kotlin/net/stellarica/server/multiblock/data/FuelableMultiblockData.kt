package net.stellarica.server.multiblock.data

import kotlinx.serialization.Serializable
import net.stellarica.common.util.OriginRelative
import net.stellarica.server.multiblock.MultiblockInstance
import net.stellarica.server.transfer.PipeHandler
import kotlin.math.abs
import kotlin.math.min

@Serializable
class FuelableMultiblockData : MultiblockData, FuelContainer {
	override var fuel: Int = 0
	override var capacity = 0
	companion object {
		fun transferFuelWith(instance: MultiblockInstance, fuelPoint: OriginRelative) {
			val fuelPos = fuelPoint.getBlockPos(instance.origin, instance.direction)
			val node = PipeHandler[instance.world][fuelPos] ?: return

			val data = (instance.data as FuelContainer)
			val nodeMissing = node.capacity - node.content
			val mbMissing = data.capacity - data.fuel

			// this could DEFINITELY be improved
			val nodePercent = node.content / node.capacity.toFloat()
			val mbPercent = data.fuel / data.capacity.toFloat()

			val diff = mbPercent - nodePercent
			if (abs(diff) < 0.1) return

			if (diff < 0) {
				// transfer to multiblock
				val amount = min(mbMissing, node.content)
				node.content -= amount
				data.fuel += amount
			} else {
				// transfer to pipe
				val amount = min(nodeMissing, data.fuel)
				data.fuel -= amount
				node.content += amount
			}
		}
	}
}