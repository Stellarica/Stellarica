package io.github.hydrazinemc.hydrazine.multiblocks

import io.github.hydrazinemc.hydrazine.Hydrazine.Companion.plugin
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.NamespacedKey
import org.bukkit.block.BlockFace
import org.bukkit.persistence.PersistentDataAdapterContext
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType
import java.util.UUID

/**
 * PersistentDataType for storing detected multiblocks in chunk data
 */
class MultiblockPDC : PersistentDataType<Array<PersistentDataContainer>, MutableSet<Multiblock>> {
	override fun getPrimitiveType(): Class<Array<PersistentDataContainer>> {
		return Array<PersistentDataContainer>::class.java
	}

	override fun getComplexType(): Class<MutableSet<Multiblock>> {
		return mutableSetOf<Multiblock>().javaClass
	}

	override fun toPrimitive(complex: MutableSet<Multiblock>, context: PersistentDataAdapterContext):
			Array<PersistentDataContainer> {
		val result = arrayOfNulls<PersistentDataContainer>(complex.size)

		complex.forEachIndexed { index, multiblock ->
			val container = context.newPersistentDataContainer()

			container.set(NamespacedKey(plugin, "name"), PersistentDataType.STRING, multiblock.name)
			container.set(NamespacedKey(plugin, "uuid"), PersistentDataType.STRING, multiblock.uuid.toString())
			container.set(NamespacedKey(plugin, "x"), PersistentDataType.DOUBLE, multiblock.origin.x) // unnecessary
			container.set(NamespacedKey(plugin, "y"), PersistentDataType.DOUBLE, multiblock.origin.y) // double
			container.set(NamespacedKey(plugin, "z"), PersistentDataType.DOUBLE, multiblock.origin.z) // alert
			container.set(NamespacedKey(plugin, "t"), PersistentDataType.INTEGER, multiblock.t)
			container.set(NamespacedKey(plugin, "facing"), PersistentDataType.STRING, multiblock.facing.toString())
			container.set(NamespacedKey(plugin, "world"), PersistentDataType.STRING, multiblock.origin.world.name)

			result[index] = container
		}

		return result.requireNoNulls()
	}

	override fun fromPrimitive(primitive: Array<PersistentDataContainer>, context: PersistentDataAdapterContext):
			MutableSet<Multiblock> {
		val result = mutableSetOf<Multiblock>()

		primitive.forEach { persistentDataContainer ->
			val name = persistentDataContainer.get(NamespacedKey(plugin, "name"), PersistentDataType.STRING)!!
			val uuid =
				UUID.fromString(persistentDataContainer.get(NamespacedKey(plugin, "uuid"), PersistentDataType.STRING)!!)
			val x = persistentDataContainer.get(NamespacedKey(plugin, "x"), PersistentDataType.DOUBLE)!!
			val y = persistentDataContainer.get(NamespacedKey(plugin, "y"), PersistentDataType.DOUBLE)!!
			val z = persistentDataContainer.get(NamespacedKey(plugin, "z"), PersistentDataType.DOUBLE)!!
			val t = persistentDataContainer.get(NamespacedKey(plugin, "t"), PersistentDataType.INTEGER)!!
			val f = BlockFace.valueOf(
				persistentDataContainer.get(
					NamespacedKey(plugin, "facing"),
					PersistentDataType.STRING
				)!!
			)
			val w = persistentDataContainer.get(NamespacedKey(plugin, "world"), PersistentDataType.STRING)!!

			result.add(Multiblock(name, uuid, Location(Bukkit.getWorld(w), x, y, z), f, t))
		}

		return result
	}
}
