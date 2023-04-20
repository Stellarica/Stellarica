package net.stellarica.server.util.extension

import net.minecraft.core.Vec3i

operator fun Vec3i.plus(other: Vec3i) = this.offset(other)
operator fun Vec3i.minus(other: Vec3i) = this.subtract(other)
operator fun Vec3i.times(other: Int) = this.multiply(other)
operator fun Vec3i.div(other: Int) = this.multiply(1 / other)
