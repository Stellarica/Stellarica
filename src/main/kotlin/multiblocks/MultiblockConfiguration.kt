package io.github.petercrawley.minecraftstarshipplugin.multiblocks

import io.github.petercrawley.minecraftstarshipplugin.customMaterials.MSPMaterial

data class MultiblockConfiguration(
	val name: String,
	val blocks: List<List<List<MSPMaterial>>>,
	val signLocation: MultiblockSignLocation
)
