package io.github.hydrazinemc.hydrazine.server.utils

import io.github.hydrazinemc.hydrazine.server.HydrazineServer.Companion.plugin
import org.bukkit.Material
import java.util.EnumSet

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
	lateinit var forcedUndetectable: EnumSet<Material>
		private set

	/**
	 * The blocks that by default cannot be detected.
	 * Each individual ship can override this.
	 *
	 * @see forcedUndetectable
	 */
	lateinit var defaultUndetectable: EnumSet<Material>
		private set

	/**
	 * Update values from the config file.
	 */
	fun loadFromConfig() {
		timeOperations = plugin.config.getBoolean("timeOperations", false)
		detectionLimit = plugin.config.getInt("detectionLimit", 500000)

		forcedUndetectable = EnumSet.copyOf(
			plugin.config.getStringList("forcedUndetectable").map { Material.getMaterial(it)!! }
		)

		defaultUndetectable = EnumSet.copyOf(
			plugin.config.getStringList("defaultUndetectable").map { Material.getMaterial(it!!) }
		)
	}
}
