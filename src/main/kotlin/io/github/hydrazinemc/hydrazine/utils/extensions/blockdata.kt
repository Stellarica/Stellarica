package io.github.hydrazinemc.hydrazine.utils.extensions

import io.github.hydrazinemc.hydrazine.utils.RotationAmount
import io.github.hydrazinemc.hydrazine.utils.rotateAxis
import org.bukkit.block.data.BlockData
import org.bukkit.block.data.Directional
import org.bukkit.block.data.Orientable
import org.bukkit.block.data.Rotatable
import rotateBlockFace

fun BlockData.rotate(amount: RotationAmount) {
	if (amount == RotationAmount.NONE) return
	// Handle rotation of Directionals
	if (this is Directional) {
		this.facing = rotateBlockFace(this.facing, amount)
	}
	// Rotation of Orientables
	if (this is Orientable && amount != RotationAmount.REVERSE) {
		this.axis = rotateAxis(this.axis, amount)
	}
	// Rotation of Rotatables
	if (this is Rotatable) {
		this.rotation = rotateBlockFace(this.rotation, amount)
	}
}