package net.stellarica.server.material.type.item

import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Items
import net.stellarica.server.StellaricaServer
import net.stellarica.server.material.custom.item.CustomItem
import net.stellarica.server.CustomItems
import net.stellarica.server.material.type.block.BlockType
import org.bukkit.NamespacedKey
import org.bukkit.craftbukkit.v1_20_R2.inventory.CraftItemStack
import org.bukkit.persistence.PersistentDataType

interface ItemType {
	fun getVanillaItemStack(count: Int = 1): net.minecraft.world.item.ItemStack
	fun getBukkitItemStack(count: Int = 1): org.bukkit.inventory.ItemStack
	fun getBukkitItem(): org.bukkit.Material
	fun getVanillaItem(): net.minecraft.world.item.Item
	fun getId(): ResourceLocation
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
			return item.itemMeta?.persistentDataContainer?.get(
				NamespacedKey(StellaricaServer.plugin, "custom_item_id"),
				PersistentDataType.STRING,
			)?.let { id -> ResourceLocation.tryParse(id)?.let { CustomItems[it]?.let { CustomItemType(it) } } }
			// no custom item, get the vanilla item
			// handle can be null if the stack is empty
				?: VanillaItemType((item as? CraftItemStack)?.handle?.item ?: Items.AIR)
		}

		fun of(item: net.minecraft.world.item.ItemStack): ItemType {
			return of(item.bukkitStack)
		}

		fun of(item: org.bukkit.Material): VanillaItemType {
			return VanillaItemType((org.bukkit.inventory.ItemStack(item) as CraftItemStack).handle.item)
		}

		fun of(item: ResourceLocation): ItemType? {
			return CustomItems[item]?.let { CustomItemType(it) }
				?: org.bukkit.Material.getMaterial(item.path)?.let { of(it) }
		}
	}
}