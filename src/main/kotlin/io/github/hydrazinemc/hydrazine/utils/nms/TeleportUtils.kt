package io.github.hydrazinemc.hydrazine.utils.nms

import io.github.hydrazinemc.hydrazine.utils.rotation.RotationAmount
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket.RelativeArgument.X
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket.RelativeArgument.X_ROT
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket.RelativeArgument.Y
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket.RelativeArgument.Y_ROT
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket.RelativeArgument.Z
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
 * Taken from Horizon's End's IonCore, under MIT as noted in the readme
 * Based on https://www.spigotmc.org/threads/teleport-player-smoothly.317416/page-2#post-4186516
 */
object TeleportUtils {
	// no idea what any of this is lol
	private val OFFSET_DIRECTION = setOf(X_ROT, Y_ROT)
	private val OFFSET_ALL = setOf(X_ROT, Y_ROT, X, Y, Z)

	private var justTeleportedField: Field = getField("justTeleported")
	private var awaitingPositionFromClientField: Field = getField("y") // y = awaitingPositionFromClient
	private var lastPosXField: Field = getField("lastPosX")
	private var lastPosYField: Field = getField("lastPosY")
	private var lastPosZField: Field = getField("lastPosZ")
	private var awaitingTeleportField: Field = getField("z") // z = awaitingTeleport
	private var awaitingTeleportTimeField: Field = getField("A") // A = awaitingTeleportTimeField
	private var aboveGroundVehicleTickCountField: Field = getField("E") // E = aboveGroundVehicleTickCountField

	private fun getField(name: String): Field =
		ServerGamePacketListenerImpl::class.java.getDeclaredField(name).apply { isAccessible = true }

	private fun move(player: Player, loc: Location, theta: Float = 0.0f) {
		// I honestly have no idea what most of this does and am afraid to touch it
		val x = loc.x
		val y = loc.y
		val z = loc.z

		val handle = (player as CraftPlayer).handle

		if (handle.containerMenu !== handle.inventoryMenu) handle.closeContainer()

		handle.absMoveTo(x, y, z, handle.yRot + theta, handle.xRot)

		val connection = handle.connection

		var teleportAwait: Int

		justTeleportedField.set(connection, true)
		awaitingPositionFromClientField.set(connection, Vec3(x, y, z))
		lastPosXField.set(connection, x)
		lastPosYField.set(connection, y)
		lastPosZField.set(connection, z)

		teleportAwait = awaitingTeleportField.getInt(connection) + 1

		if (teleportAwait == 2147483647) teleportAwait = 0

		awaitingTeleportField.set(connection, teleportAwait)
		awaitingTeleportTimeField.set(connection, aboveGroundVehicleTickCountField.get(connection))

		val packet = ClientboundPlayerPositionPacket(x, y, z, theta, 0f, OFFSET_DIRECTION, teleportAwait, false)
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
