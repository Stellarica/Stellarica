package net.stellarica.common.util

import net.minecraft.core.BlockPos
import net.minecraft.core.Vec3i
import net.minecraft.world.phys.Vec3
import net.stellarica.common.coordinate.BlockPosition
import kotlin.math.roundToInt

fun Vec3i.toVec3(): Vec3 = Vec3(x.toDouble(), y.toDouble(), z.toDouble())
fun Vec3.toVec3i(): Vec3i = Vec3i(x.roundToInt(), y.roundToInt(), z.roundToInt())
fun Vec3.toBlockPosition(): BlockPosition = BlockPosition(x.roundToInt(), y.roundToInt(), z.roundToInt())
fun BlockPosition.toVec3(): Vec3 = Vec3(x.toDouble(), y.toDouble(), z.toDouble())
fun BlockPosition.toVec3i(): Vec3i = Vec3i(x, y, z)
fun BlockPosition.toBlockPos(): BlockPos = BlockPos(x, y, z)