package net.stellarica.server.material.item.type

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.stellarica.server.StellaricaServer.Companion.identifier
import net.stellarica.server.material.block.type.BlockType
import net.stellarica.server.material.item.CustomItem
import net.stellarica.server.material.item.ItemPower.Companion.power
import net.stellarica.server.util.asMiniMessage
import net.stellarica.server.util.extension.toNamespacedKey
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack
import org.bukkit.persistence.PersistentDataType

@JvmInline
value class CustomItemType(val item: CustomItem) : ItemType {
	companion object {
		val key = identifier("custom_item_id").toNamespacedKey()
	}

	override fun getVanillaItemStack(count: Int): ItemStack {
		return (getBukkitItemStack(count) as CraftItemStack).handle
	}

	override fun getBukkitItemStack(count: Int): org.bukkit.inventory.ItemStack {
		val stack = org.bukkit.inventory.ItemStack(item.base.getBukkitItem(), count)
		stack.editMeta { meta ->
			meta.persistentDataContainer.set(
				key,
				PersistentDataType.STRING,
				item.id.toString()
			)
			meta.displayName(item.name.asMiniMessage.decoration(TextDecoration.ITALIC, false))

			val loreComponents = mutableListOf<Component>() // this can be code golfed
			item.lore.forEach { loreComponents.add(it.asMiniMessage.decoration(TextDecoration.ITALIC, false)) }
			if (item.isPowerable) {
				// extra lore slot for the power line
				loreComponents.add("if you see this, it's a bug".asMiniMessage)
			}
			meta.lore(loreComponents)
			meta.setCustomModelData(item.modelData)
		}
		if (item.isPowerable) stack.power!!.power = 0
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
