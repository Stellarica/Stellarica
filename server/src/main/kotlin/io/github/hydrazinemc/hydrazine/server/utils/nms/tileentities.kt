package io.github.hydrazinemc.hydrazine.server.utils.nms

import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import org.bukkit.Material
import org.bukkit.Tag
import sun.misc.Unsafe
import java.lang.reflect.Field
import java.util.EnumSet


// Taken and modified from APDevTeam's Movecraft 8, as noted in the readme

/**
 * Remove the tile entity at [position] in [world]
 * @return the entity that got removed
 */
fun removeBlockEntity(world: Level, position: BlockPos): BlockEntity? {
	return world.getChunkAt(position).blockEntities.remove(position)
}

/**
 * Set the tile entity at [position]
 */
fun setBlockEntity(world: Level, position: BlockPos, tile: BlockEntity) {
	val chunk = world.getChunkAt(position)
	try {
		val positionField = BlockEntity::class.java.getDeclaredField("o") // o is obfuscated worldPosition
		UnsafeUtils.setField(positionField, tile, position)
	} catch (e: NoSuchFieldException) {
		error { e.toString() }
	}
	tile.level = world
	tile.clearRemoved()
	if (world.captureBlockStates) {
		world.capturedTileEntities[position] = tile
		return
	}
	chunk.setBlockEntity(tile)
	chunk.blockEntities[position] = tile
}

/**
 * Unsafe nonsense, courtesy of Movecraft
 */
object UnsafeUtils {
	private var unsafe: Unsafe? = null

	init {
		var defered: Unsafe? = null
		try {
			val field: Field = Unsafe::class.java.getDeclaredField("theUnsafe")
			field.isAccessible = true
			defered = field[null] as Unsafe
		} catch (e: NoSuchFieldException) {
			error { e.toString() }
		} catch (e: IllegalAccessException) {
			error { e.toString() }
		}
		unsafe = defered
	}

	/**
	 * e
	 */
	fun setField(field: Field?, holder: Any?, value: Any?) {
		unsafe ?: return
		unsafe!!.putObject(holder, unsafe!!.objectFieldOffset(field), value)
	}
}

/**
 * This is terrible.
 * I feel bad about adding this.
 * End my suffering.
 * Save me.
 * Save us.
 * We're doomed.
 */ // todo: call an exorcist or something
val tileEntities: EnumSet<Material> = EnumSet.of(
	Material.CHEST,
	Material.TRAPPED_CHEST,
	Material.ENDER_CHEST,
	Material.SHULKER_BOX,
	Material.ITEM_FRAME,
	Material.HOPPER,
	Material.DROPPER,
	Material.DISPENSER,
	Material.FURNACE,
	Material.BREWING_STAND,
	Material.JUKEBOX,
	Material.BELL,
	Material.BEEHIVE,
	Material.BEE_NEST,
	Material.BARREL,
	Material.BEACON,
	Material.NOTE_BLOCK,
	Material.ENCHANTED_GOLDEN_APPLE,
	Material.CONDUIT,
	Material.SMOKER,
	Material.BLAST_FURNACE,
	Material.LECTERN,
	Material.SPAWNER,
	Material.CAMPFIRE,
	Material.SOUL_CAMPFIRE,
	Material.DAYLIGHT_DETECTOR
	// also mob heads?

).apply {
	this.addAll(Tag.SIGNS.values)
	this.addAll(Tag.BANNERS.values)
	this.addAll(Tag.SHULKER_BOXES.values)
	this.addAll(Tag.BEDS.values)
}
