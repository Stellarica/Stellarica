package net.stellarica.server.utils

import net.stellarica.server.StellaricaServer
import net.stellarica.server.material.type.item.ItemType
import org.bukkit.Bukkit
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.inventory.ShapelessRecipe

/**
 * Register a shaped recipe for [itemStack]
 * @param matrix a list of item (or custom item) ids that represent the crafting grid
 * @see registerShapelessRecipe
 */
private fun registerShapedRecipe(itemStack: ItemStack, matrix: List<ItemType?>) {
	val key =  StellaricaServer.namespacedKey("recipe_${ItemType.of(itemStack).getStringId()}")
	if (Bukkit.getRecipe(key) != null) {
		StellaricaServer.klogger.warn { "A recipe is already registered with key ${key.key}!" }
		StellaricaServer.klogger.warn { "Cannot register bukkit shapeless recipe for ${ItemType.of(itemStack).getStringId()}" }
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
		recipe.setIngredient(str[i], matrix[i]!!.getBukkitItemStack())
	}
	recipe.shape(*shape.chunked(3).toTypedArray())
	Bukkit.addRecipe(recipe)
}

/**
 * Register a shapeless recipe for [itemStack]
 * @param ingredients the crafting ingredients
 * @see registerShapedRecipe
 */
private fun registerShapelessRecipe(itemStack: ItemStack, ingredients: List<ItemType>) {
	val id = ItemType.of(itemStack).getStringId()
	val key = StellaricaServer.namespacedKey("recipe_$id")
	if (Bukkit.getRecipe(key) != null) {
		StellaricaServer.klogger.warn { "A recipe is already registered with key ${key.key}!" }
		StellaricaServer.klogger.warn { "Cannot register bukkit shapeless recipe for $id" }
		return
	}
	val recipe = ShapelessRecipe(key, itemStack)
	for (ingredient in ingredients) {
		recipe.addIngredient(ingredient.getBukkitItemStack())
	}
	Bukkit.addRecipe(recipe)
}