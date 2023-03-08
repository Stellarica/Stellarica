package net.stellarica.server.material.type.item

import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import org.bukkit.Material

@JvmInline
value class VanillaItemType(val item: Item): ItemType {
	override fun getVanillaItemStack(count: Int): net.minecraft.world.item.ItemStack {
		return net.minecraft.world.item.ItemStack(item, count)
	}

	override fun getBukkitItemStack(count: Int): org.bukkit.inventory.ItemStack {
		return org.bukkit.inventory.ItemStack(getBukkitItem(), count)
	}

	override fun getBukkitItem(): Material {
		return Material.getMaterial(getStringId())!!
	}

	override fun getVanillaItem(): Item {
		return item
	}

	override fun getId(): ResourceLocation {
		@Suppress("DEPRECATION")
		return item.builtInRegistryHolder().key().location()
	}
}