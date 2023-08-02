package net.stellarica.server.multiblock

import net.stellarica.common.coordinate.BlockPosition
import net.stellarica.server.Multiblocks
import net.stellarica.server.StellaricaServer.Companion.identifier
import net.stellarica.server.material.custom.item.type.DebugCustomItems
import net.stellarica.server.material.type.item.ItemType
import net.stellarica.server.util.Tasks
import net.stellarica.server.util.extension.sendRichActionBar
import net.stellarica.server.util.extension.toBlockPosition
import net.stellarica.server.util.extension.toLocation
import net.stellarica.server.util.extension.vanilla
import org.bukkit.Chunk
import org.bukkit.World
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
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

	@EventHandler
	fun onChunkLoad(event: ChunkLoadEvent) {

	}

	@EventHandler
	fun onChunkUnload(event: ChunkUnloadEvent) {
		// saveToChunk(event.chunk)
		multiblocks.remove(event.chunk)
	}
}