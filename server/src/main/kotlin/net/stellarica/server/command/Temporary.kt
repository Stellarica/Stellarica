package net.stellarica.server.command

import cloud.commandframework.annotations.Argument
import cloud.commandframework.annotations.CommandMethod
import net.minecraft.core.Direction
import net.minecraft.world.level.block.Rotation
import net.stellarica.common.coordinate.BlockPosition
import net.stellarica.server.craft.CraftTransformation
import net.stellarica.server.craft.starship.Starship
import net.stellarica.server.util.Tasks
import net.stellarica.server.util.extension.toBlockPosition
import net.stellarica.server.util.wrapper.ServerWorld
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import kotlin.system.measureTimeMillis

@Suppress("unused")
object Temporary {
	val ships = mutableMapOf<Player, Starship>()

	@CommandMethod("ship pilot")
	fun pilot(sender: Player) = Tasks.sync {
		// just a debugging command, but uh, pretty sure cloud has a way to run commands synchronously
		// probably should just use that rather than tasks.sync
		val ship = ships.getOrPut(sender) {
			Starship().also {
				it.setup(
					sender.getTargetBlockExact(16)!!.location.toBlockPosition(),
					ServerWorld(sender.world)
				)
			}
		}
		sender.sendRichMessage("<blue>Piloting ship at ${ship.origin}!")
	}

	@CommandMethod("ship release")
	fun release(sender: Player) {
		if (ships.remove(sender) != null) {
			sender.sendRichMessage("<blue>Released ship")
		} else {
			sender.sendRichMessage("<red>You aren't piloting a ship!")
		}
	}

	@CommandMethod("ship detect")
	fun detect(sender: Player) = Tasks.sync {
		val ship = ships.getOrElse(sender) {
			sender.sendRichMessage("<red>You aren't piloting a ship!")
			return@sync
		}
		val oldCount = ship.blockCount;
		val time = measureTimeMillis {
			ship.detect()
		}
		sender.sendRichMessage("<green>(Re)-Detected craft! ($oldCount -> ${ship.blockCount}) in ${time}ms")
	}

	@CommandMethod("ship translate <x> <y> <z>")
	fun translate(sender: Player, @Argument("x") deltaX: Int, @Argument("y") deltaY: Int, @Argument("z") deltaZ: Int) {
		val ship = ships.getOrElse(sender) {
			sender.sendRichMessage("<red>You aren't piloting a ship!")
			return
		}
		val delta = BlockPosition(deltaX, deltaY, deltaZ)
		Tasks.sync {
			// hey look! a race condition!
			// this is debug code anyway so I don't care
			val time = measureTimeMillis {
				ship.transform(CraftTransformation({ pos ->
					pos + delta
				}, Rotation.NONE, ship.world))
			}
			sender.sendRichMessage("<green>Moved in ${time}ms")
		}
	}

	@CommandMethod("ship dump")
	fun dump(sender: CommandSender){
		sender.sendRichMessage("<blue>${ships.keys}")

	}
	
	@CommandMethod("removeme")
	fun temp(sender: Player) {
		val o = BlockPosition(20,12,3)
		sender.location.toBlockPosition()
			.also { println(it) }
			.getAsRelative(o, Direction.EAST)
			.also { println(it) }
			.getGlobalPosition(o, Direction.EAST)
			.also { println(it) }
	}
}
