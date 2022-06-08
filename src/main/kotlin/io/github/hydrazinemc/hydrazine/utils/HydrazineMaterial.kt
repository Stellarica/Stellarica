package io.github.hydrazinemc.hydrazine.utils
/*
import io.github.hydrazinemc.hydrazine.customblocks.CustomBlock
import io.github.hydrazinemc.hydrazine.customblocks.CustomBlocks
import io.github.hydrazinemc.hydrazine.customitems.CustomItem
import io.github.hydrazinemc.hydrazine.customitems.customItem
import io.github.hydrazinemc.hydrazine.utils.locations.BlockLocation
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.data.MultipleFacing
import org.bukkit.inventory.ItemStack


class HydrazineMaterial {
	constructor(block: Block) {
		CustomBlocks[block.blockData as? MultipleFacing] ?: {
			type = HydrazineMaterialType.BUKKIT
			id = block.type.toString()
		}
		type = HydrazineMaterialType.CUSTOM_BLOCK
	}

	constructor(item: ItemStack) {
		if (item.customItem != null) {
			type = HydrazineMaterialType.CUSTOM_ITEM
			id = item.customItem!!.id
		}
		else {
			type = HydrazineMaterialType.BUKKIT
			id = item.type.toString()
		}
	}

	constructor(item: CustomItem) {
		type = HydrazineMaterialType.CUSTOM_ITEM
		id = item.id
	}

	constructor(material: Material) {
		type = HydrazineMaterialType.BUKKIT
		id = material.toString()
	}
	constructor(blockLocation: BlockLocation) {
		HydrazineMaterial(blockLocation.bukkit)
	}

	constructor(block: CustomBlock) {
		type = HydrazineMaterialType.CUSTOM_BLOCK
		id = block.id
	}

	internal enum class HydrazineMaterialType {
		BUKKIT,
		CUSTOM_BLOCK,
		CUSTOM_ITEM,
	}

	lateinit private var type: HydrazineMaterialType
	private lateinit var id: String

	fun getItemStack(count: Int) {

	}
	fun getBlockData() {}
	fun getBukkitMaterial() {}

}
 */
