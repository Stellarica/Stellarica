package net.stellarica.server.multiblocks


import dev.onyxstudios.cca.api.v3.component.tick.ServerTickingComponent
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.chunk.LevelChunk
import net.silkmc.silk.nbt.serialization.Nbt
import net.silkmc.silk.nbt.serialization.decodeFromNbtElement
import net.silkmc.silk.nbt.serialization.encodeToNbtElement
import net.stellarica.server.Stellarica.Companion.identifier
import net.stellarica.server.events.multiblocks.MultiblockUndetectEvent
import org.bukkit.Chunk
import org.bukkit.World


class ChunkMultiblocksComponent(private val chunk: Chunk) : ServerTickingComponent {
	var multiblocks = mutableSetOf<MultiblockInstance>()
		private set

	// can't use multiblockinstance as we don't want to serialize the world or the type
	@Serializable
	private data class MultiblockData(
		val type: String,
		val oX: Int,
		val oY: Int,
		val oZ: Int,
		val direction: Direction
	) {

		constructor(multiblock: MultiblockInstance) :
				this(
					multiblock.type.id.path,
					multiblock.origin.x,
					multiblock.origin.y,
					multiblock.origin.z,
					multiblock.direction
				)

		fun toInstance(world: World) =
			MultiblockInstance(
				BlockPos(oX, oY, oZ),
				world,
				direction,
				identifier(type)
			)
	}

	override fun readFromNbt(tag: CompoundTag) {
		val world = (chunk as LevelChunk).level
		tag.get("multiblocks")?.let { nbt ->
			Nbt.decodeFromNbtElement<List<MultiblockData>>(nbt).map { it.toInstance(world) }
		}?.let { multiblocks.addAll(it) }
	}

	override fun writeToNbt(tag: CompoundTag) {
		if (multiblocks.size == 0) return
		tag.put("multiblocks", Nbt.encodeToNbtElement(multiblocks.map { MultiblockData(it) }))
	}

	override fun serverTick() {
		// validate this chunk's multiblocks
		// todo: why do this every tick?
		val invalid = mutableSetOf<MultiblockInstance>()
		multiblocks.forEach {
			if (!it.validate()) {
				MultiblockUndetectEvent.call(MultiblockUndetectEvent.EventData(it))
				invalid.add(it)
				chunk.setNeedsSaving(true)
			}
		}
		multiblocks.removeAll(invalid)
	}
}