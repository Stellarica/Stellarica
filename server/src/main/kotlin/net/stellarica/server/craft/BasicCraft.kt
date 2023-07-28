package net.stellarica.server.craft

import net.minecraft.core.Direction
import net.minecraft.core.Vec3i
import net.minecraft.world.level.block.Rotation
import net.stellarica.common.coordinate.BlockPosition
import net.stellarica.common.coordinate.RelativeBlockPosition
import net.stellarica.server.multiblock.MultiblockInstance
import org.bukkit.World

abstract class BasicCraft : Craft, MultiblockContainer {

	final override lateinit var origin: BlockPosition
		protected set
	final override lateinit var orientation: Direction
		protected set
	final override lateinit var world: World
		protected set

	// warning this is a list and not a set, make sure it doesn't contain duplicates
	// (hashset iter time is :agony:)
	protected val detectedBlocks = mutableListOf<BlockPosition>()

	override val blockCount: Int
		get() = detectedBlocks.size

	override fun contains(block: BlockPosition): Boolean {
		TODO()
	}

	override fun canRotate(rotation: Rotation): Boolean {
		TODO()
	}

	override fun rotate(rotation: Rotation) {
		TODO()
	}

	override fun canMove(offset: Vec3i): Boolean {
		TODO()
	}

	override fun move(offset: Vec3i) {
		TODO()
	}

	override fun canTeleport(pos: BlockPosition, world: World): Boolean {
		TODO()
	}

	override fun teleport(pos: BlockPosition, world: World) {
		TODO()
	}

	override fun getMultiblockAt(pos: RelativeBlockPosition) {
		TODO()
	}

	override fun addMultiblock(multiblock: MultiblockInstance) {
		TODO()
	}

	override fun removeMultiblock(multiblock: MultiblockInstance) {
		TODO()
	}
}