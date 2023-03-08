package net.stellarica.server.material.type.block

import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.Block
import org.bukkit.Material
import org.bukkit.block.data.BlockData


@JvmInline
value class VanillaBlockType(val type: Block) : BlockType {
	override fun getBukkitBlockData(): BlockData {
		return getBukkitBlock().createBlockData()
	}

	override fun getVanillaBlockState(): net.minecraft.world.level.block.state.BlockState {
		return type.defaultBlockState()
	}

	override fun getBukkitBlock(): Material {
		return Material.getMaterial(getStringId())!!
	}

	override fun getVanillaBlock(): Block {
		return type
	}

	override fun getId(): ResourceLocation {
		@Suppress("DEPRECATION")
		return type.builtInRegistryHolder().key().location()
	}
}