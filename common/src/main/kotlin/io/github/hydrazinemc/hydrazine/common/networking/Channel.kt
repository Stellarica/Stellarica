package io.github.hydrazinemc.hydrazine.common.networking

import net.minecraft.resources.ResourceLocation

enum class Channel {
	HANDSHAKE;

	val fabric by lazy {
		ResourceLocation("tomfoolery", name.lowercase())
	}
	val bukkit by lazy {
		"tomfoolery:${name.lowercase()}"
	}
}