package net.stellarica.server.material.block

import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.Block
import net.stellarica.server.customblocks.CustomBlock
import org.bukkit.Material
import org.bukkit.block.data.BlockData

@JvmInline
value class CustomBlockType(val type: CustomBlock) : BlockType {
	override fun getBukkitBlockData(): BlockData {
		TODO("Not yet implemented")
	}

	override fun getVanillaBlockState(): net.minecraft.world.level.block.state.BlockState {
		TODO("Not yet implemented")
	}

	override fun getBukkitBlock(): Material {
		TODO("Not yet implemented")
	}

	override fun getVanillaBlock(): Block {
		TODO("Not yet implemented")
	}

	override fun getId(): ResourceLocation {
		TODO("Not yet implemented")
	}

	override fun getStringId(): String {
		TODO("Not yet implemented")
	}

}