package io.github.hydrazinemc.hydrazine.utils

import io.github.hydrazinemc.hydrazine.Hydrazine.Companion.plugin
import org.bukkit.Material

/**
 * Stores the values that can be configured in config.yml
 */
object ConfigurableValues {

	/**
	 * Whether to output the time certain operations take. e.g. ship detection
	 */
	var timeOperations: Boolean = false
		private set

	/**
	 * The maximum size a starship can detect
	 */
	var detectionLimit: Int = 500000
		private set

	/**
	 * The blocks that can never be detected as part of a starship
	 *
	 * @see defaultUndetectable
	 */
	var forcedUndetectable = setOf<Material>()
		private set

	/**
	 * The blocks that by default cannot be detected.
	 * Each individual ship can override this.
	 *
	 * @see forcedUndetectable
	 */
	var defaultUndetectable = setOf<Material>()
		private set

	/**
	 * Update values from the config file.
	 */
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