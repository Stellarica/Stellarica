package net.stellarica.server.utils

import net.stellarica.server.StellaricaServer
import net.stellarica.server.utils.extensions.id
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.inventory.ShapelessRecipe

/**
 * Register a shaped recipe for [itemStack]
 * @param matrix a list of item (or custom item) ids that represent the crafting grid
 * @see registerShapelessRecipe
 */
private fun registerShapedRecipe(itemStack: ItemStack, matrix: List<String?>) {
	StellaricaServer.klogger.debug { "Registering recipe for $itemStack" }
	val key = NamespacedKey(StellaricaServer.plugin, "recipe_${itemStack.id}")
	if (Bukkit.getRecipe(key) != null) {
		StellaricaServer.klogger.warn { "A recipe is already registered with key ${key.key}!" }
		StellaricaServer.klogger.warn { "Cannot register bukkit shapeless recipe for ${itemStack.id}" }
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
	StellaricaServer.klogger.info { "Registered recipe $matrix for ${itemStack.id}" }
}

/**
 * Register a shapeless recipe for [itemStack]
 * @param ingredients the crafting ingredients as a set of id strings
 * @see registerShapedRecipe
 */
private fun registerShapelessRecipe(itemStack: ItemStack, ingredients: List<String>) {
	val key = NamespacedKey(StellaricaServer.plugin, "recipe_${itemStack.id}")
	if (Bukkit.getRecipe(key) != null) {
		StellaricaServer.klogger.warn { "A recipe is already registered with key ${key.key}!" }
		StellaricaServer.klogger.warn { "Cannot register bukkit shapeless recipe for ${itemStack.id}" }
		return
	}
	val recipe = ShapelessRecipe(key, itemStack)
	ingredients.forEach {
		recipe.addIngredient(itemStackFromId(it)!!)
	}
	Bukkit.addRecipe(recipe)
	StellaricaServer.klogger.info { "Registered recipe $ingredients for ${itemStack.id}" }
}