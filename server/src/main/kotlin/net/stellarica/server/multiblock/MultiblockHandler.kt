package net.stellarica.server.multiblock

import kotlinx.serialization.Serializable
import net.minecraft.core.BlockPos
import net.stellarica.nbt.getCompoundTag
import net.stellarica.server.StellaricaServer.Companion.identifier
import net.stellarica.server.StellaricaServer.Companion.klogger
import net.stellarica.server.material.custom.item.type.DebugCustomItems
import net.stellarica.server.material.type.item.ItemType
import net.stellarica.server.util.Tasks
import net.stellarica.server.util.extension.sendRichActionBar
import net.stellarica.server.util.extension.toBlockPos
import net.stellarica.server.util.extension.toLocation
import net.stellarica.server.util.extension.vanilla
import net.stellarica.server.util.persistence.ChunkPersistentStorage
import net.stellarica.server.util.persistence.PersistentDataContainerStorage
import net.stellarica.server.util.persistence.PlayerPersistentStorage
import org.bukkit.Chunk
import org.bukkit.World
import org.bukkit.craftbukkit.v1_19_R3.persistence.CraftPersistentDataContainer
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.world.ChunkLoadEvent
import org.bukkit.event.world.ChunkUnloadEvent

object MultiblockHandler : Listener {
	internal val multiblocks = mutableMapOf<Chunk, MutableSet<MultiblockInstance>>()

	operator fun get(chunk: Chunk) = multiblocks.getOrPut(chunk) { mutableSetOf() }

	private val namespacedKey = identifier("multiblocks")

	init {
		Tasks.syncRepeat(5, 20) {
			for ((_, mbSet) in multiblocks) {
				val invalid = mutableSetOf<MultiblockInstance>()
				for (multiblock in mbSet) {
					if (!multiblock.validate()) {
						invalid.add(multiblock)
					} else {
						multiblock.type.tick(multiblock)
					}
				}
				mbSet.removeAll(invalid)
			}
		}
	}

	fun detect(origin: BlockPos, world: World): MultiblockInstance? {
		val possible = mutableListOf<MultiblockInstance>()
		for (type in Multiblocks.all) {
			val instance = type.detect(origin, world)
			if (instance != null) {
				possible.add(instance)
			}
		}
		// return the largest possible, in case there are multiple
		return possible.maxByOrNull { it.type.blocks.size }?.also {
			val chunk = world.getChunkAt(origin.toLocation(world))
			multiblocks.getOrPut(chunk) { mutableSetOf() }.add(it)
			it.type.init(it)
			chunk.vanilla.isUnsaved = true
		}
	}

	private fun loadFromChunk(chunk: Chunk) {
		ChunkPersistentStorage(chunk).get<MutableSet<MultiblockInstance>>(namespacedKey)?.let {
			multiblocks.getOrPut(chunk) { mutableSetOf() }.addAll(it)
		}
	}

	private fun saveToChunk(chunk: Chunk) {
		ChunkPersistentStorage(chunk).set(namespacedKey, multiblocks[chunk] ?: mutableSetOf())
	}

	@EventHandler
	fun onPlayerAttemptDetect(event: PlayerInteractEvent) {
		if (event.action != Action.RIGHT_CLICK_BLOCK) return
		if (event.item?.let { ItemType.of(it) } != ItemType.of(DebugCustomItems.DETECTOR)) return
		multiblocks[event.clickedBlock!!.chunk]?.firstOrNull { it.origin == event.clickedBlock!!.toBlockPos() }?.let {
			event.player.sendRichActionBar("<dark_green>Found already detected ${it.type.displayName}")
			return
		}
		detect(event.clickedBlock!!.toBlockPos(), event.player.world)?.let {
			event.player.sendRichActionBar("<green>Detected ${it.type.displayName}")
			return
		}
		event.player.sendRichActionBar("<red>No multiblock detected")
	}

	@EventHandler
	fun onChunkLoad(event: ChunkLoadEvent) {
		try {
			loadFromChunk(event.chunk)
		} catch (e: Exception) {
			klogger.error { "Failed to load multiblocks from chunk at ${event.chunk.x}, ${event.chunk.z}" }
			e.printStackTrace()
			klogger.error { "Clearing multiblock data for the chunk" }
			multiblocks[event.chunk] = mutableSetOf()
			saveToChunk(event.chunk)
		}
	}

	@EventHandler
	fun onChunkUnload(event: ChunkUnloadEvent) {
		saveToChunk(event.chunk)
		multiblocks.remove(event.chunk)
	}

	@EventHandler
	fun onPlayerJoinDebugRemoveThisPleaseLmao(event: PlayerJoinEvent) {
		val id = identifier("debugging")
		val storage = PlayerPersistentStorage(event.player)
		val pdc = storage.getPersistentDataContainer()
		println((pdc as CraftPersistentDataContainer).raw)
		println("    " + pdc.getCompoundTag())
		try {
			val data: MutableList<Int> = storage[id] ?: mutableListOf()
			println("got $data")
			data.add((data.lastOrNull() ?: -1) + 1)
			println("now $data")
			storage[id] = data
		}
		finally {
			println((pdc as CraftPersistentDataContainer).raw)
			println("    " + pdc.getCompoundTag())
		}
	}
}