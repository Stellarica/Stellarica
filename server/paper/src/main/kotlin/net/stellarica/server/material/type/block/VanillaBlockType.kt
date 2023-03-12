package net.stellarica.server.material.type.block

import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.Block
import net.stellarica.server.material.type.item.ItemType
import org.bukkit.Material
import org.bukkit.block.data.BlockData


@JvmInline
value class VanillaBlockType(val block: Block) : BlockType {
	override fun getBukkitBlockData(): BlockData {
		return getBukkitBlock().createBlockData()
	}

	override fun getVanillaBlockState(): net.minecraft.world.level.block.state.BlockState {
		return block.defaultBlockState()
	}

	override fun getBukkitBlock(): Material {
		return getVanillaBlockState().bukkitMaterial
	}

	override fun getVanillaBlock(): Block {
		return block
	}

	override fun getId(): ResourceLocation {
		@Suppress("DEPRECATION")
		return block.builtInRegistryHolder().key().location()
	}

	override fun getItem(): ItemType {
		return ItemType.of(block.asItem())
	}
}