package net.stellarica.server.material.feature

import net.minecraft.resources.ResourceLocation

abstract class OptionalMaterialFeature {
	abstract val material: ResourceLocation
	companion object {
		val features = mutableMapOf<ResourceLocation, OptionalMaterialFeature>()

		fun enablePresent() {
			// TODO
		}
	}

	abstract fun enable()
}
