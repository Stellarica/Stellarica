package net.stellarica.server.multiblock.data

import kotlinx.serialization.Serializable
import net.stellarica.common.util.OriginRelative
import net.stellarica.server.multiblock.MultiblockInstance
import net.stellarica.server.transfer.FuelPacket
import net.stellarica.server.transfer.PipeHandler
import kotlin.math.min

@Serializable
class FuelableMultiblockData : MultiblockData, FuelContainer {
	override var content: Int = 0
	override var capacity = 0

	companion object {
		fun MultiblockInstance.transferFuelFrom(fuelPoint: OriginRelative, max: Int): Int {
			val fuelPos = fuelPoint.getBlockPos(this.origin, this.direction)
			val node = PipeHandler[this.world][fuelPos] ?: return 0
			val data = (this.data as FuelContainer)

			var transferred = 0
			while (node.content.isNotEmpty() && data.content < data.capacity && transferred < max) {
				// attempt to transfer in as much as possible
				val c = node.content.first() as FuelPacket
				if (data.content + c.content > data.capacity) continue

				node.content.remove(c)
				data.content += c.content
				transferred += c.content
			}
			return transferred
		}

		fun MultiblockInstance.transferFuelTo(fuelPoint: OriginRelative, max: Int): Int {
			val fuelPos = fuelPoint.getBlockPos(this.origin, this.direction)
			val node = PipeHandler[this.world][fuelPos] ?: return 0
			val data = (this.data as FuelContainer)

			val amount = min(max, data.content)
			if (amount == 0) return 0

			data.content -= amount
			node.content.add(FuelPacket(amount))
			return amount
		}
	}
}