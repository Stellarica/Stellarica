package net.stellarica.server.material.type.item

import net.kyori.adventure.text.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.stellarica.server.StellaricaServer
import net.stellarica.server.material.custom.item.CustomItem
import net.stellarica.server.material.type.block.BlockType
import net.stellarica.server.utils.extensions.asMiniMessage
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.craftbukkit.v1_19_R2.inventory.CraftItemStack
import org.bukkit.persistence.PersistentDataType

@JvmInline
value class CustomItemType(val item: CustomItem): ItemType {

	override fun getVanillaItemStack(count: Int): ItemStack {
		return (getBukkitItemStack(count) as CraftItemStack).handle
	}

	override fun getBukkitItemStack(count: Int): org.bukkit.inventory.ItemStack {
		val stack = org.bukkit.inventory.ItemStack(item.base.getBukkitItem(), count)
		stack.editMeta { meta ->
			meta.displayName(item.name.asMiniMessage)
			val loreComponents = mutableListOf<Component>() // this can be code golfed
			item.lore.forEach { loreComponents.add(it.asMiniMessage) }

			if (item.isPowerable) { // todo: don't do this here. Duplicating the power lore is bad
				loreComponents.add("<gray>Power: 0/${item.maxPower}".asMiniMessage)
				meta.persistentDataContainer.set(
					NamespacedKey(StellaricaServer.plugin, "power"),
					PersistentDataType.INTEGER,
					0
				)
			}


			meta.lore(loreComponents)
			meta.setCustomModelData(item.modelData)
			meta.persistentDataContainer.set(
				NamespacedKey(StellaricaServer.plugin, "custom_item_id"),
				PersistentDataType.STRING,
				item.id.toString()
			)
		}
		return stack
	}

	override fun getBukkitItem(): Material {
		return item.base.getBukkitItem()
	}

	override fun getVanillaItem(): Item {
		return item.base.getVanillaItem()
	}

	override fun getId(): ResourceLocation {
		return item.id
	}

	override fun getBlock(): BlockType? {
		return item.block?.let { BlockType.of(it) }
	}
}