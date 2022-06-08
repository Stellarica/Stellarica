package io.github.hydrazinemc.hydrazine.multiblocks

data class MultiblockLayout(val name: String) {
	val blocks = mutableMapOf<MultiblockOriginRelative, MSPMaterial>()
}
