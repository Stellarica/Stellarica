package io.github.hydrazinemc.hydrazine.utils

import io.github.hydrazinemc.hydrazine.Hydrazine.Companion.plugin
import org.bukkit.Material

object ConfigurableValues {
	var timeOperations: Boolean = false
		private set

	var detectionLimit: Int = 500000
		private set
	var forcedUndetectable = setOf<Material>()
		private set

	var defaultUndetectable = setOf<Material>()
		private set

	fun loadFromConfig() {
		timeOperations = plugin.config.getBoolean("timeOperations", false)
		detectionLimit = plugin.config.getInt("detectionLimit", 500000)
		val newForcedUndetectable = mutableSetOf<Material>()
		plugin.config.getStringList("forcedUndetectable").forEach {
			newForcedUndetectable.add(Material.getMaterial(it)!!)
		}
		forcedUndetectable = newForcedUndetectable

		val newDefaultUndetectable = mutableSetOf<Material>()
		plugin.config.getStringList("defaultUndetectable").forEach {
			newDefaultUndetectable.add(Material.getMaterial(it)!!)
		}
		defaultUndetectable = newDefaultUndetectable
	}
}