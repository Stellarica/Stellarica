package io.github.hydrazinemc.hydrazine.common.networking

import net.minecraft.resources.ResourceLocation

enum class Channel {
	HANDSHAKE;

	val fabric by lazy {
		ResourceLocation("hydrazine", name.lowercase())
	}
	val bukkit by lazy {
		"hydrazine:${name.lowercase()}"
	}
}