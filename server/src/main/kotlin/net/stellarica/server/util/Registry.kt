package net.stellarica.server.util

import net.minecraft.resources.ResourceLocation

// For now, this is basically just a map, but it *might* have extra functionality in the future
// Doesn't hurt, I gues
class Registry<T>: Map<ResourceLocation, T> {
	val map = mutableMapOf<ResourceLocation, T>()


	// for now, useless.
	// effectively just delegates to the map
	override val entries: Set<Map.Entry<ResourceLocation, T>>
		get() = map.entries
	override val keys: Set<ResourceLocation>
		get() = map.keys
	override val size: Int
		get() = map.size
	override val values: Collection<T>
		get() = map.values

	override fun isEmpty() = map.isEmpty()
	override fun get(key: ResourceLocation) = map[key]
	override fun containsValue(value: T) = map.containsValue(value)
	override fun containsKey(key: ResourceLocation) = map.containsKey(key)

	fun register(id: ResourceLocation, value: T) {
		map[id] = value
	}
}
