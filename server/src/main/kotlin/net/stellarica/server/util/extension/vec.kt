package net.stellarica.server.util.extension

import net.minecraft.world.phys.Vec3

operator fun Vec3.plus(other: Vec3) = this.add(other)
operator fun Vec3.minus(other: Vec3) = this.subtract(other)
operator fun Vec3.times(other: Double) = this.scale(other)
operator fun Vec3.div(other: Double) = this.scale(1 / other)
