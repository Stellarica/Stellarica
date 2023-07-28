package net.stellarica.server.material.custom.item

import net.minecraft.resources.ResourceLocation
import net.stellarica.server.material.custom.block.CustomBlock
import net.stellarica.server.material.type.item.VanillaItemType
import org.bukkit.enchantments.Enchantment

/**
 * Holds data for a custom item
 */
class CustomItem(
	/** The ID of the item. */
	val id: ResourceLocation,

	/** The display name of the item, MiniMessage formatting is allowed */
	val name: String,

	/** The item's lore, MiniMessage formatting is allowed */
	val lore: List<String>,

	/** The vanilla material behind this item */
	val base: VanillaItemType,

	/** Custom model data for this item */
	val modelData: Int,

	/** The allowed enchants for this item */
	val allowedEnchants: MutableSet<Enchantment>? = null,

	/** The block this item can be placed as */
	val block: CustomBlock? = null,

	/**
	 * The amount of power this item can hold.
	 * If this is zero, the item cannot be powered
	 * */
	val maxPower: Int = 0
) {
	val isPowerable = maxPower > 0
}
