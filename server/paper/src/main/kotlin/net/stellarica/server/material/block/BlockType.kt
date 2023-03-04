package net.stellarica.server.material.block

import net.minecraft.resources.ResourceLocation
import net.stellarica.server.customblocks.CustomBlock
import org.bukkit.craftbukkit.v1_19_R2.block.data.CraftBlockData

interface BlockType {
	fun getBukkitBlockData(): org.bukkit.block.data.BlockData
	fun getVanillaBlockState(): net.minecraft.world.level.block.state.BlockState
	fun getBukkitBlock(): org.bukkit.Material
	fun getVanillaBlock(): net.minecraft.world.level.block.Block
	fun getId(): ResourceLocation
	fun getStringId(): String

	val isCustom: Boolean
		get() = this is CustomBlockType

	companion object {
		fun of(block: CustomBlock): CustomBlockType {
			return CustomBlockType(block)
		}

		fun of(block: net.minecraft.world.level.block.state.BlockState): BlockType {
			return of(block.block)
		}

		fun of(block: org.bukkit.block.BlockState): BlockType {
			return of((block.blockData as CraftBlockData).state)
		}

		fun of(block: net.minecraft.world.level.block.Block): VanillaBlockType {
			return VanillaBlockType(block)
		}

		fun of(block: org.bukkit.Material): VanillaBlockType {
			return of((block.createBlockData() as CraftBlockData).state.block) as VanillaBlockType
		}
	}
}