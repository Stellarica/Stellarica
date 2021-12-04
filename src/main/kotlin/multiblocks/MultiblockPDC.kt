package io.github.petercrawley.minecraftstarshipplugin.multiblocks

import io.github.petercrawley.minecraftstarshipplugin.MinecraftStarshipPlugin.Companion.plugin
import org.bukkit.NamespacedKey
import org.bukkit.persistence.PersistentDataAdapterContext
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType

class MultiblockPDC: PersistentDataType<Array<PersistentDataContainer>, MutableSet<Multiblock>> {
	override fun getPrimitiveType(): Class<Array<PersistentDataContainer>> {
		return Array<PersistentDataContainer>::class.java
	}

	override fun getComplexType(): Class<MutableSet<Multiblock>> {
		return mutableSetOf<Multiblock>().javaClass
	}

	override fun toPrimitive(complex: MutableSet<Multiblock>, context: PersistentDataAdapterContext): Array<PersistentDataContainer> {
		val result = arrayOfNulls<PersistentDataContainer>(complex.size)

		complex.forEachIndexed { index, multiblock ->
			val container = context.newPersistentDataContainer()

			container.set(NamespacedKey(plugin, "name"), PersistentDataType.STRING, multiblock.name)
			container.set(NamespacedKey(plugin, "x"), PersistentDataType.INTEGER, multiblock.x)
			container.set(NamespacedKey(plugin, "y"), PersistentDataType.INTEGER, multiblock.y)
			container.set(NamespacedKey(plugin, "z"), PersistentDataType.INTEGER, multiblock.z)
			container.set(NamespacedKey(plugin, "t"), PersistentDataType.INTEGER, multiblock.t)
			container.set(NamespacedKey(plugin, "r"), PersistentDataType.BYTE, multiblock.r)

			result[index] = container
		}

		return result.requireNoNulls()
	}

	override fun fromPrimitive(primitive: Array<PersistentDataContainer>, context: PersistentDataAdapterContext): MutableSet<Multiblock> {
		val result = mutableSetOf<Multiblock>()

		primitive.forEach { persistentDataContainer ->
			val name = persistentDataContainer.get(NamespacedKey(plugin, "name"), PersistentDataType.STRING)!!
			val x = persistentDataContainer.get(NamespacedKey(plugin, "x"), PersistentDataType.INTEGER)!!
			val y = persistentDataContainer.get(NamespacedKey(plugin, "y"), PersistentDataType.INTEGER)!!
			val z = persistentDataContainer.get(NamespacedKey(plugin, "z"), PersistentDataType.INTEGER)!!
			val t = persistentDataContainer.get(NamespacedKey(plugin, "t"), PersistentDataType.INTEGER)!!
			val r = persistentDataContainer.get(NamespacedKey(plugin, "r"), PersistentDataType.BYTE)!!

			result.add(Multiblock(name, x, y, z, r, t))
		}

		return result
	}
}