package net.stellarica.server.material.type.item

import net.minecraft.resources.ResourceLocation
import net.stellarica.server.material.custom.item.CustomItem
import net.stellarica.server.material.custom.item.CustomItems
import net.stellarica.server.material.type.block.BlockType
import org.bukkit.craftbukkit.v1_19_R2.inventory.CraftItemStack

interface ItemType {
	fun getVanillaItemStack(count: Int = 1): net.minecraft.world.item.ItemStack
	fun getBukkitItemStack(count: Int = 1): org.bukkit.inventory.ItemStack
	fun getBukkitItem(): org.bukkit.Material
	fun getVanillaItem(): net.minecraft.world.item.Item
	fun getId(): ResourceLocation
	fun getStringId(): String = getId().path
	fun getBlock(): BlockType?
	val isCustom: Boolean
		get() = this is CustomItemType

	companion object {
		fun of(item: CustomItem): CustomItemType {
			return CustomItemType(item)
		}

		fun of(item: net.minecraft.world.item.Item): VanillaItemType {
			return VanillaItemType(item)
		}

		fun of(item: org.bukkit.inventory.ItemStack): ItemType {
			TODO()
		}

		fun of(item: net.minecraft.world.item.ItemStack): ItemType {
			TODO()
		}

		fun of(item: org.bukkit.Material): VanillaItemType {
			return VanillaItemType((org.bukkit.inventory.ItemStack(item) as CraftItemStack).handle.item)
		}

		fun of(item: ResourceLocation): ItemType? {
			return CustomItems.byId(item)?.let { CustomItemType(it) }
				?: org.bukkit.Material.getMaterial(item.path)?.let { of(it) }
		}
	}
}