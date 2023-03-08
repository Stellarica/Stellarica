package net.stellarica.common.utils

import net.minecraft.core.BlockPos
import net.minecraft.core.Vec3i
import net.minecraft.world.phys.Vec3
import kotlin.math.roundToInt

fun Vec3i.toVec3(): Vec3 {
	return Vec3(x.toDouble(), y.toDouble(), z.toDouble())
}

fun Vec3.toVec3i(): Vec3i {
	return Vec3i(x.roundToInt(), y.roundToInt(), z.roundToInt())
}

fun Vec3.toBlockPos(): BlockPos {
	return BlockPos(x.roundToInt(), y.roundToInt(), z.roundToInt())
}