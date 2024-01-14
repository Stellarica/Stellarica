package net.stellarica.server.multiblock

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import net.stellarica.common.coordinate.BlockPosition
import net.stellarica.server.Multiblocks
import net.stellarica.server.StellaricaServer.Companion.identifier
import net.stellarica.server.StellaricaServer.Companion.klogger
import net.stellarica.server.material.item.custom.DebugCustomItems
import net.stellarica.server.material.item.type.ItemType
import net.stellarica.server.util.Tasks
import net.stellarica.server.util.extension.toBlockPosition
import net.stellarica.server.util.extension.toLocation
import net.stellarica.server.util.extension.toNamespacedKey
import net.stellarica.server.util.extension.vanilla
import net.stellarica.server.util.sendRichActionBar
import org.bukkit.Chunk
import org.bukkit.World
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.world.ChunkLoadEvent
import org.bukkit.event.world.ChunkUnloadEvent
import org.bukkit.persistence.PersistentDataType

object MultiblockHandler : Listener {
	internal val multiblocks = mutableMapOf<Chunk, MutableSet<MultiblockInstance>>()

	operator fun get(chunk: Chunk) = multiblocks.getOrPut(chunk) {
		klogger.warn { "bug! aaaaa!" }
		mutableSetOf()
	} // todo: dont getorput


	init {
		Tasks.syncRepeat(5, 20) {
			for ((_, mbSet) in multiblocks) {
				val invalid = mutableSetOf<MultiblockInstance>()
				for (multiblock in mbSet) {
					if (!multiblock.validate()) {
						invalid.add(multiblock)
					} else {
						//multiblock.type.tick(multiblock)
					}
				}
				mbSet.removeAll(invalid)
			}
		}
	}

	fun detect(origin: BlockPosition, world: World): MultiblockInstance? {
		val possible = mutableListOf<MultiblockInstance>()
		for (type in Multiblocks) {
			val instance = type.detectMultiblock(origin, world)
			if (instance != null) {
				possible.add(instance)
			}
		}
		// return the largest possible, in case there are multiple
		return possible.maxByOrNull { it.type.blocks.size }?.also {
			val chunk = world.getChunkAt(origin.toLocation(world))
			multiblocks.getOrPut(chunk) { mutableSetOf() }.add(it)
			chunk.vanilla.isUnsaved = true
		}
	}

	@EventHandler
	fun onPlayerAttemptDetect(event: PlayerInteractEvent) {
		if (event.action != Action.RIGHT_CLICK_BLOCK) return
		if (event.item?.let { ItemType.of(it) } != ItemType.of(DebugCustomItems.DETECTOR)) return
		multiblocks[event.clickedBlock!!.chunk]?.firstOrNull { it.origin == event.clickedBlock!!.toBlockPosition() }
				?.let {
					event.player.sendRichActionBar("<dark_green>Found already detected ${it.type.displayName}")
					return
				}
		detect(event.clickedBlock!!.toBlockPosition(), event.player.world)?.let {
			event.player.sendRichActionBar("<green>Detected ${it.type.displayName}")
			return
		}
		event.player.sendRichActionBar("<red>No multiblock detected")
	}

	private val pdcKey = identifier("multiblocks").toNamespacedKey()

	@OptIn(ExperimentalSerializationApi::class, ExperimentalStdlibApi::class)
	@EventHandler
	fun onChunkLoad(event: ChunkLoadEvent) {
		val bytes = event.chunk.persistentDataContainer.get(pdcKey, PersistentDataType.BYTE_ARRAY) ?: return
		if (bytes.size <= 2) return // cursed hack, look into this!

		val pos = "(${event.chunk.x}, ${event.chunk.z})"
		try {
			if (multiblocks.containsKey(event.chunk))
				klogger.warn { "Chunk $pos already has multiblocks, which will be overwritten!" }

			multiblocks[event.chunk] = Cbor.decodeFromByteArray<MutableSet<MultiblockInstance>>(bytes);
		}
		catch (e: Exception) {
			e.printStackTrace()
			klogger.error { "Could not load multiblocks in chunk $pos" }
			multiblocks[event.chunk] = mutableSetOf()
		}
	}

	@OptIn(ExperimentalSerializationApi::class)
	@EventHandler
	fun onChunkUnload(event: ChunkUnloadEvent) {
		val mb = multiblocks.remove(event.chunk) ?: mutableSetOf()
		try {
			event.chunk.persistentDataContainer.set(pdcKey, PersistentDataType.BYTE_ARRAY, Cbor.encodeToByteArray(mb))
		} catch (e: Exception) {
			e.printStackTrace()
			klogger.error { "Could not save multiblocks to chunk (${event.chunk.x}, ${event.chunk.z})" }
		}
	}
}