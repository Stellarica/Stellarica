package net.stellarica.server.customblocks

import net.stellarica.server.StellaricaServer.Companion.plugin
import net.stellarica.server.customblocks.CustomBlocks.blocks
import net.stellarica.server.customitems.CustomItem
import net.stellarica.server.customitems.CustomItems.itemStackFromId
import org.bukkit.block.BlockFace
import org.bukkit.block.data.MultipleFacing
import org.bukkit.inventory.ItemStack

/**
 * Loads CustomBlocks from the config and keeps track of registered [blocks]
 */
object CustomBlocks {
	/**
	 * The custom blocks (id: block)
	 */
	val blocks = mutableMapOf<String, CustomBlock>()

	/**
	 * Get a custom block by its id
	 */
	operator fun get(name: String): CustomBlock? = blocks[name]

	/**
	 * Get a custom block by its representative item
	 */
	operator fun get(item: CustomItem): CustomBlock? {
		return blocks.values.firstOrNull { it.customItem == item }
	}

	/**
	 * Get a custom block by its block data
	 */
	operator fun get(data: MultipleFacing?): CustomBlock? {
		for (block in blocks.values) {
			if (block.blockData.faces == data?.faces) {
				return block
			}
		}
		return null
	}


	/**
	 * Load custom blocks from the config file
	 */
	fun loadFromConfig() {
		// todo: really need to do error handling for this, or just stop using bukkit config
		blocks.clear()
		val conf = plugin.config
		conf.getConfigurationSection("customBlocks")?.getKeys(false)?.forEach { id ->
			val blockPath = "customBlocks.$id"
			val data = mapOf(
				BlockFace.NORTH to conf.getBoolean("$blockPath.faces.north"),
				BlockFace.EAST to conf.getBoolean("$blockPath.faces.east"),
				BlockFace.SOUTH to conf.getBoolean("$blockPath.faces.south"),
				BlockFace.WEST to conf.getBoolean("$blockPath.faces.west"),
				BlockFace.UP to conf.getBoolean("$blockPath.faces.up"),
				BlockFace.DOWN to conf.getBoolean("$blockPath.faces.down")
			)

			blocks[id] = CustomBlock(
				id,
				conf.getString("$blockPath.item"),
				data,
				ItemStack(
					itemStackFromId(
						conf.getString("$blockPath.drops.item")!!,
						conf.getInt("$blockPath.drops.count")
					)!!
				)
			)
		}
	}
}
