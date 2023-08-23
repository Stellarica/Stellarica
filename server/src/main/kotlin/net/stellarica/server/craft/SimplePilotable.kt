package net.stellarica.server.craft

import io.papermc.paper.entity.TeleportFlag
import net.minecraft.world.level.block.Rotation
import net.minecraft.world.phys.Vec3
import net.stellarica.common.util.asDegrees
import net.stellarica.common.util.rotateCoordinates
import net.stellarica.common.util.toVec3
import net.stellarica.server.util.extension.minus
import net.stellarica.server.util.extension.plus
import net.stellarica.server.util.extension.toBlockPosition
import net.stellarica.server.util.extension.toLocation
import net.stellarica.server.util.extension.toVec3
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerTeleportEvent

class SimplePilotable : Pilotable {
	override val passengers = mutableSetOf<LivingEntity>()
	override fun addPassenger(passenger: LivingEntity) {

	}

	override fun removePassenger(passenger: LivingEntity) {

	}

	override var pilot: Player? = null
		private set

	override fun pilot(pilot: Player) {
		if (CraftPilotEvent.callCancellable(Pair(this, pilot))) {
			this.pilot = pilot
		}
	}

	override fun unpilot() {
		CraftUnpilotEvent.call(this)
		this.pilot = null
	}

	override fun movePassengers(craft: Craft, data: CraftTransformation) {
		for (passenger in passengers) {
			// TODO: FIX
			// this is not a good solution because if there is any rotation, the player will not be translated by the offset
			// The result is that any ship movement that attempts to rotate and move in the same action will break.
			// For now there aren't any actions like that, but if there are in the future, this will need to be fixed.
			//
			// Rotating the whole ship around the adjusted origin will not work,
			// as rotating the ship 4 times does not bring it back to the original position
			//
			// However, without this dumb fix players do not rotate to the proper relative location
			val destination =
				if (data.rotation != Rotation.NONE) rotateCoordinates(
					passenger.location.toVec3(),
					craft.origin.toVec3().add(Vec3(0.5, 0.0, 0.5)), data.rotation
				)
				else {
					val o = passenger.location.toVec3().minus(passenger.location.toBlockLocation().toVec3())
					(data.offset(passenger.location.toBlockPosition()).toVec3() + o)
				}.toLocation(craft.world.world)


			destination.world = data.world.world
			destination.pitch = passenger.location.pitch
			destination.yaw = (passenger.location.yaw + data.rotation.asDegrees).toFloat()

			@Suppress("UnstableApiUsage")
			passenger.teleport(
				destination,
				PlayerTeleportEvent.TeleportCause.PLUGIN,
				TeleportFlag.EntityState.RETAIN_OPEN_INVENTORY,
				*TeleportFlag.Relative.entries.toTypedArray()
			)
		}
	}
}