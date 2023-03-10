package net.stellarica.server.material.type.block

import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.stellarica.server.material.custom.block.CustomBlock
import net.stellarica.server.material.type.item.ItemType
import org.bukkit.Material
import org.bukkit.block.data.BlockData

@JvmInline
value class CustomBlockType(val block: CustomBlock) : BlockType {
	override fun getBukkitBlockData(): BlockData {
		TODO("Not yet implemented")
	}

	override fun getVanillaBlockState(): net.minecraft.world.level.block.state.BlockState {
		TODO("Not yet implemented")
	}

	override fun getBukkitBlock(): Material {
		return Material.NOTE_BLOCK
	}

	override fun getVanillaBlock(): Block {
		return Blocks.NOTE_BLOCK
	}

	override fun getId(): ResourceLocation {
		return block.id
	}

	override fun getItem(): ItemType? {
		return block.item?.let { ItemType.of(it) }
	}
}