package net.stellarica.server.util.extension

import net.minecraft.resources.ResourceLocation
import net.minecraft.world.phys.Vec3
import net.stellarica.common.coordinate.BlockPosition
import net.stellarica.server.util.wrapper.ServerWorld
import org.bukkit.Location
import org.bukkit.NamespacedKey
import org.bukkit.block.Block

fun Location.toVec3() = Vec3(x, y, z)
fun Location.toBlockPosition() = BlockPosition(x.toInt(), y.toInt(), z.toInt())
fun BlockPosition.toLocation(world: ServerWorld?) = Location(world?.bukkit, x.toDouble(), y.toDouble(), z.toDouble())
fun Block.toBlockPosition() = BlockPosition(x, y, z)
fun Vec3.toLocation(world: ServerWorld?) = Location(world?.bukkit, x, y, z)
fun ResourceLocation.toNamespacedKey() = NamespacedKey(namespace, path)
fun NamespacedKey.toResourceLocation() = ResourceLocation(namespace, key)
