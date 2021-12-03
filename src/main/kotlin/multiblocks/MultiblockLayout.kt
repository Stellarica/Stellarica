package io.github.petercrawley.minecraftstarshipplugin.multiblocks

import io.github.petercrawley.minecraftstarshipplugin.customMaterials.MSPMaterial

data class MultiblockLayout(val name: String) {
	val blocks = mutableMapOf<MultiblockOriginRelative, MSPMaterial>()
}