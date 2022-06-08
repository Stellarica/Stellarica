package io.github.hydrazinemc.hydrazine.customitems

import io.github.hydrazinemc.hydrazine.Hydrazine
import io.github.hydrazinemc.hydrazine.Hydrazine.Companion.klogger
import io.github.hydrazinemc.hydrazine.Hydrazine.Companion.plugin
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.inventory.ShapelessRecipe

object CustomItems {
	private val items = mutableMapOf<String, CustomItem>()

	/**
	 * Get a custom item by its id
	 */
	operator fun get(name: String?): CustomItem? = items[name]


	/**
	 * Load custom items from the config file
	 */
	fun loadFromConfig() {
		items.clear()
		val conf = plugin.config
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
			if (conf.getConfigurationSection("$itemPath.shapedRecipe") != null) {
				val matrix = mutableListOf<String>()
				for (i in 1..3) {
					matrix.addAll(conf.getStringList("$itemPath.shapedRecipe.r$i"))
				}
				registerShapedRecipe(
					items[id]!!.getItem(conf.getInt("$itemPath.shapedRecipe.amount")),
					matrix
				)
			}
			if (conf.getConfigurationSection("$itemPath.shapelessRecipe") != null) {
				registerShapelessRecipe(
					items[id]!!.getItem(conf.getInt("$itemPath.shapelessRecipe.amount")),
					conf.getStringList("$itemPath.shapelessRecipe.items")
				)
			}
		}
	}


	/**
	 * Register a shaped recipe for [itemStack]
	 * @param matrix a list of item (or custom item) ids that represent the crafting grid
	 * @see registerShapelessRecipe
	 */
	private fun registerShapedRecipe(itemStack: ItemStack, matrix: List<String?>) {
		klogger.debug { "Registering recipe for $itemStack" }
		val key = NamespacedKey(Hydrazine.plugin, "recipe_${itemStack.id}")
		if (Bukkit.getRecipe(key) != null) {
			klogger.warn {"A recipe is already registered with key ${key.key}!"}
			klogger.warn {"Cannot register bukkit shapeless recipe for ${itemStack.id}"}
			return
		}
		val recipe = ShapedRecipe(key, itemStack).shape("abc", "def", "ghi")
		var shape = ""
		val str = "abcdefghi"
		for (i in 0..8) {
			if (matrix[i] == null) {
				shape += " "
				continue
			}
			shape += str[i]
			recipe.setIngredient(str[i], itemStackFromId(matrix[i]!!)!!)
		}
		recipe.shape(*shape.chunked(3).toTypedArray())
		Bukkit.addRecipe(recipe)
		klogger.info{"Registered recipe $matrix for ${itemStack.id}"}
	}

	/**
	 * Register a shapeless recipe for [itemStack]
	 * @param ingredients the crafting ingredients as a set of id strings
	 * @see registerShapedRecipe
	 */
	private fun registerShapelessRecipe(itemStack: ItemStack, ingredients: List<String>) {
		val key = NamespacedKey(plugin, "recipe_${itemStack.id}")
		if (Bukkit.getRecipe(key) != null) {
			klogger.warn {"A recipe is already registered with key ${key.key}!"}
			klogger.warn {"Cannot register bukkit shapeless recipe for ${itemStack.id}"}
			return
		}
		val recipe = ShapelessRecipe(key, itemStack)
		ingredients.forEach {
			recipe.addIngredient(itemStackFromId(it)!!)
		}
		Bukkit.addRecipe(recipe)
		klogger.info{"Registered recipe $ingredients for ${itemStack.id}"}
	}

	/**
	 * @return the ItemStack of the custom item or material with [id], with [count] items
	 * @see [ItemStack.id]
	 */
	fun itemStackFromId(id: String, count: Int = 1): ItemStack? {
		return CustomItems[id]?.getItem(count) ?: ItemStack(
			Material.getMaterial(id.uppercase()) ?: return null,
			count
		)
	}
}
