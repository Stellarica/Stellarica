package net.stellarica.server.utils.extensions

import net.minecraft.core.BlockPos
import net.minecraft.core.Vec3i
import net.minecraft.world.phys.Vec3
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.util.Vector

fun Location.toBlockPos(): BlockPos {
	return BlockPos(x.toInt(), y.toInt(), z.toInt())
}

fun Location.toVec3(): Vec3 {
	return Vec3(x, y, z)
}

fun Location.toVec3i(): Vec3i {
	return Vec3i(x, y, z)
}

fun BlockPos.toLocation(world: World?): Location {
	return Location(world, x.toDouble(), y.toDouble(), z.toDouble())
}

fun Block.toBlockPos(): BlockPos {
	return BlockPos(x, y, z)
}

fun Vec3i.toVec3(): Vec3 {
	return Vec3(x.toDouble(), y.toDouble(), z.toDouble())
}

fun Vec3.toVec3i(): Vec3i {
	return Vec3i(x, y, z)
}

fun Vec3.toBlockPos(): BlockPos {
	return BlockPos(x, y, z)
}

fun Vec3.toLocation(world: World?): Location {
	return Location(world, x, y, z)
}

fun Vec3.toVector(): Vector {
	return Vector(x, y, z)
}