package io.github.hydrazinemc.hydrazine.customitems

import io.github.hydrazinemc.hydrazine.Hydrazine
import org.bukkit.Material

object CustomItems {
	private var items = mutableMapOf<String, CustomItem>()

	/**
	 * Get a custom item by its id
	 */
	operator fun get(name: String): CustomItem? = items[name]


	/**
	 * Load custom items from the config file
	 */
	fun loadFromConfig() {
		val conf = Hydrazine.plugin.config
		conf.getConfigurationSection("customItems")!!.getKeys(false).forEach { id ->
			val itemPath = "customItems.$id"
			items[id] = CustomItem(
				id,
				conf.getString("$itemPath.name")!!,
				conf.getStringList("$itemPath.lore"),
				Material.valueOf(conf.getString("$itemPath.base")!!),
				conf.getInt("$itemPath.data"),
				conf.getInt("$itemPath.maxPower"),
			)
		}
	}
}
