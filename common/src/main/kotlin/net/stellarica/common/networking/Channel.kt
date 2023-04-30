package net.stellarica.common.networking

import net.minecraft.resources.ResourceLocation

enum class Channel {
	LOGIN;

	val fabric by lazy {
		ResourceLocation("stellarica", name.lowercase())
	}
	val bukkit by lazy {
		"stellarica:${name.lowercase()}"
	}
}