package io.github.hydrazinemc.hydrazine.utils.nms

import io.github.hydrazinemc.hydrazine.utils.rotation.RotationAmount
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket.RelativeArgument.X_ROT
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket.RelativeArgument.Y_ROT
import net.minecraft.server.network.ServerGamePacketListenerImpl
import net.minecraft.world.phys.Vec3
import org.bukkit.Location
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer
import org.bukkit.entity.Player
import java.lang.reflect.Field

/**
 * Used to teleport the player when teleporting multiple times per second while
 * allowing them to look around.
 *
 * Taken (and heavily modified) from Horizon's End's IonCore, under MIT as noted in the readme
 * Based on https://www.spigotmc.org/threads/teleport-player-smoothly.317416/page-2#post-4186516
 */
object TeleportUtils {
	// no idea what any of this is lol
	private val OFFSET_DIRECTION = setOf(X_ROT, Y_ROT)

	private var justTeleportedField: Field = getField("justTeleported")
	private var awaitingPositionFromClientField: Field = getField("B") // B = awaitingPositionFromClient
	private var lastPosXField: Field = getField("lastPosX")
	private var lastPosYField: Field = getField("lastPosY")
	private var lastPosZField: Field = getField("lastPosZ")
	private var awaitingTeleportField: Field = getField("C") // C = awaitingTeleport
	private var awaitingTeleportTimeField: Field = getField("D") // D = awaitingTeleportTimeField
	private var aboveGroundVehicleTickCountField: Field = getField("H") // H = aboveGroundVehicleTickCountField

	private fun getField(name: String): Field =
		ServerGamePacketListenerImpl::class.java.getDeclaredField(name).apply { isAccessible = true }

	private fun move(player: Player, loc: Location, theta: Float = 0.0f) {
		// I honestly have no idea what most of this does and am afraid to touch it
		val handle = (player as CraftPlayer).handle

		if (handle.containerMenu !== handle.inventoryMenu) handle.closeContainer()

		handle.absMoveTo(loc.x, loc.y, loc.z, handle.yRot + theta, handle.xRot)

		val connection = handle.connection

		var teleportAwait: Int

		justTeleportedField.set(connection, true)
		awaitingPositionFromClientField.set(connection, Vec3(loc.x, loc.y, loc.z))
		lastPosXField.set(connection, loc.x)
		lastPosYField.set(connection, loc.y)
		lastPosZField.set(connection, loc.z)

		teleportAwait = awaitingTeleportField.getInt(connection) + 1

		if (teleportAwait == 2147483647) teleportAwait = 0

		awaitingTeleportField.set(connection, teleportAwait)
		awaitingTeleportTimeField.set(connection, aboveGroundVehicleTickCountField.get(connection))

		val packet =
			ClientboundPlayerPositionPacket(loc.x, loc.y, loc.z, theta, 0f, OFFSET_DIRECTION, teleportAwait, false)
		connection.send(packet)
	}

	/**
	 * Teleport the [player] to [loc]
	 */
	fun teleport(player: Player, loc: Location) = move(player, loc, 0.0f)

	/**
	 * Teleport the [player] to [loc], and rotate them by [theta] degrees
	 */
	fun teleportRotate(player: Player, loc: Location, theta: Float) = move(player, loc, theta)

	/**
	 * Teleport the [player] to [loc] and rotate them by [rotation]
	 */
	fun teleportRotate(player: Player, loc: Location, rotation: RotationAmount) =
		move(player, loc, rotation.asDegrees)
}
