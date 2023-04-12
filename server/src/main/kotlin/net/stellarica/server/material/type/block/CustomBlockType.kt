package net.stellarica.server.material.type.block

import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.stellarica.server.material.custom.blocks.CustomBlock
import net.stellarica.server.material.type.item.ItemType
import org.bukkit.Material
import org.bukkit.block.data.BlockData
import org.bukkit.block.data.type.NoteBlock
import org.bukkit.craftbukkit.v1_19_R3.block.data.CraftBlockData

@JvmInline
value class CustomBlockType(val block: CustomBlock) : BlockType {
	override fun getBukkitBlockData(): BlockData {
		return (Material.NOTE_BLOCK.createBlockData() as NoteBlock).apply {
			this.note = block.note
			this.instrument = block.instrument
		}
	}

	override fun getVanillaBlockState(): net.minecraft.world.level.block.state.BlockState {
		return (getBukkitBlockData() as CraftBlockData).state
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