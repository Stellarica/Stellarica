package io.github.hydrazinemc.hydrazine.server.multiblocks

import io.github.hydrazinemc.hydrazine.server.HydrazineServer.Companion.plugin
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
object MultiblockPDC : PersistentDataType<Array<PersistentDataContainer>, Set<MultiblockInstance>> {
	override fun getPrimitiveType(): Class<Array<PersistentDataContainer>> {
		return Array<PersistentDataContainer>::class.java
	}

	override fun getComplexType(): Class<Set<MultiblockInstance>> {
		return mutableSetOf<MultiblockInstance>().javaClass
	}

	override fun toPrimitive(complex: Set<MultiblockInstance>, context: PersistentDataAdapterContext):
			Array<PersistentDataContainer> {
		val result = arrayOfNulls<PersistentDataContainer>(complex.size)

		complex.forEachIndexed { index, multiblock ->
			val container = context.newPersistentDataContainer()

			container.set(NamespacedKey(plugin, "type"), PersistentDataType.STRING, multiblock.type.name)
			container.set(NamespacedKey(plugin, "uuid"), PersistentDataType.STRING, multiblock.uuid.toString())
			container.set(NamespacedKey(plugin, "x"), PersistentDataType.DOUBLE, multiblock.origin.x) // unnecessary
			container.set(NamespacedKey(plugin, "y"), PersistentDataType.DOUBLE, multiblock.origin.y) // double
			container.set(NamespacedKey(plugin, "z"), PersistentDataType.DOUBLE, multiblock.origin.z) // alert
			container.set(NamespacedKey(plugin, "facing"), PersistentDataType.STRING, multiblock.facing.toString())
			container.set(NamespacedKey(plugin, "world"), PersistentDataType.STRING, multiblock.origin.world.name)
			container.set(NamespacedKey(plugin, "data"), PersistentDataType.TAG_CONTAINER, multiblock.data)
			result[index] = container
		}

		return result.requireNoNulls()
	}

	override fun fromPrimitive(primitive: Array<PersistentDataContainer>, context: PersistentDataAdapterContext):
			Set<MultiblockInstance> {
		val result = mutableSetOf<MultiblockInstance>()

		primitive.forEach { persistentDataContainer ->
			val name = persistentDataContainer.get(NamespacedKey(plugin, "type"), PersistentDataType.STRING)!!
			val uuid =
				UUID.fromString(persistentDataContainer.get(NamespacedKey(plugin, "uuid"), PersistentDataType.STRING)!!)
			val x = persistentDataContainer.get(NamespacedKey(plugin, "x"), PersistentDataType.DOUBLE)!!
			val y = persistentDataContainer.get(NamespacedKey(plugin, "y"), PersistentDataType.DOUBLE)!!
			val z = persistentDataContainer.get(NamespacedKey(plugin, "z"), PersistentDataType.DOUBLE)!!
			val f = BlockFace.valueOf(
				persistentDataContainer.get(
					NamespacedKey(plugin, "facing"),
					PersistentDataType.STRING
				)!!
			)
			val w = persistentDataContainer.get(NamespacedKey(plugin, "world"), PersistentDataType.STRING)!!

			result.add(
				MultiblockInstance(
					Multiblocks.types.first { it.name == name },
					uuid,
					Location(Bukkit.getWorld(w), x, y, z), f,
					persistentDataContainer.get(NamespacedKey(plugin, "data"), PersistentDataType.TAG_CONTAINER)!!
				)
			)
		}

		return result.toSet()
	}
}
