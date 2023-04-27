package net.stellarica.server.util

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandCompletion
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Description
import co.aikar.commands.annotation.Subcommand
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.stellarica.common.util.OriginRelative
import net.stellarica.server.StellaricaServer
import net.stellarica.server.craft.starship.Starship
import net.stellarica.server.material.custom.item.isPowerable
import net.stellarica.server.material.custom.item.power
import net.stellarica.server.material.type.block.BlockType
import net.stellarica.server.material.type.item.CustomItemType
import net.stellarica.server.material.type.item.ItemType
import net.stellarica.server.multiblock.MultiblockHandler
import net.stellarica.server.multiblock.data.ThrusterMultiblockData
import net.stellarica.server.multiblock.matching.BlockTagMatcher
import net.stellarica.server.multiblock.matching.MultiBlockMatcher
import net.stellarica.server.multiblock.matching.SingleBlockMatcher
import net.stellarica.server.multiblock.type.Multiblocks
import net.stellarica.server.transfer.FuelPacket
import net.stellarica.server.transfer.PipeHandler
import net.stellarica.server.util.extension.craft
import net.stellarica.server.util.extension.toBlockPos
import net.stellarica.server.util.extension.toLocation
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@Suppress("Unused")
@CommandAlias("debug|db")
@CommandPermission("stellarica.debug")
class DebugCommands : BaseCommand() {
	@Subcommand("pipe node")
	fun onPipeNode(sender: Player) {
		PipeHandler[sender.world][sender.getTargetBlockExact(10)?.toBlockPos()]?.also {
			sender.sendRichMessage(
				"(${it.pos.x}, ${it.pos.y}, ${it.pos.z})\n" +
						"F: ${it.content.joinToString(", ") { (it as FuelPacket).content.toString() }}\n" +
						"C: ${it.connections.joinToString(", ") { "(${it.x}, ${it.y}, ${it.z})" }}"
			)
		}
	}

	@Subcommand("pipe addFuel")
	fun onPipeAddFuel(sender: Player, fuel: Int) {
		PipeHandler[sender.world][sender.getTargetBlockExact(10)?.toBlockPos()]!!.content.add(FuelPacket(fuel))
	}

	@Subcommand("pipe sum")
	fun onPipeSum(sender: Player) {
		var sum = 0
		var count = 0
		PipeHandler[sender.world].values.forEach {
			sum += it.content.sumOf {
				(it as? FuelPacket)?.content ?: 0
			}; count++
		}
		sender.sendRichMessage("$sum across $count nodes")
	}

	@Subcommand("multiblock|mb type")
	@Description("Get information about a multiblock type")
	@CommandCompletion("@multiblocks")
	fun onMultiblockType(sender: CommandSender, id: String) {
		val type = Multiblocks.byId(StellaricaServer.identifier(id)) ?: run {
			sender.sendRichMessage("<red>No multiblock with the id '$id' found.")
			return
		}
		sender.sendRichMessage("<bold><gold>Multiblock:</bold> ${type.id}")

		// Do you have string formatting nightmares?
		// If you didn't, now you will
		// :help_me:
		sender.sendRichMessage(type.blocks.entries.joinToString("\n") {
			"""
			<dark_gray>${
				"(<gray>${it.key.x}<dark_gray>,<gray> ${it.key.y}<dark_gray>,<gray> ${it.key.z}<dark_gray>)".padEnd(
					61
				)
			} <gray><bold>></bold> <green><bold>${
				when (it.value) {
					is MultiBlockMatcher -> "MULTI  </bold><gray>>\n       - " + (it.value as MultiBlockMatcher).types.joinToString(
						"\n       - "
					) {
						"<gold>${
							it.getId().toString().split(":").let { "<gray>${it.first()}:<gold>${it[1]}" }
						}<gray>,"
					}

					is SingleBlockMatcher -> "SINGLE </bold><gray>> <gold>${
						(it.value as SingleBlockMatcher).block.getId().toString().split(":")
							.let { "<gray>${it.first()}:<gold>${it[1]}" }
					}"

					is BlockTagMatcher -> "TAG    </bold><gray>> <aqua>" + (it.value as BlockTagMatcher).tag.location
					else -> "<red>Unknown Matcher"
				}
			}<reset>
			""".trimIndent()
		})
	}


	@Subcommand("multiblock|mb dump")
	@Description("Dump loaded multiblocks")
	fun onMultiblockDump(sender: CommandSender) {
		sender.sendRichMessage(MultiblockHandler.multiblocks.toString())
	}

	@Subcommand("multiblock|mb instance")
	@Description("Get multiblock instance information")
	fun onMultiblockInstance(sender: Player) {
		val block = sender.getTargetBlockExact(10)
		val mb = MultiblockHandler[block!!.chunk].firstOrNull { it.contains(block.toBlockPos()) } ?: run {
			sender.sendRichMessage("<red>No multiblock found!")
			return
		}
		sender.sendRichMessage(
			"""
			Type: ${mb.type.displayName}
			Origin: ${mb.origin}
			Direction: ${mb.direction}
			ID: ${mb.id}
			Data: ${Json.encodeToString(mb.data)}
		""".trimIndent()
		)
	}

	@Subcommand("starship|ship contents")
	@Description("View the ship's contants")
	private fun onShipShowHitbox(sender: Player) {
		val ship = getShip(sender) ?: return
		for ((col, extremes) in ship.contents) {
			for (y in extremes.first..extremes.second) {
				val pos = OriginRelative(col.x, y, col.z).getBlockPos(ship.origin, ship.direction)
				sender.world.spawnParticle(
					Particle.BLOCK_MARKER,
					pos.toLocation(sender.world),
					1,
					0.0,
					0.0,
					0.0,
					0.0,
					Material.BARRIER.createBlockData()
				)
			}
		}

		sender.world.spawnParticle(
			Particle.BLOCK_MARKER,
			ship.origin.toLocation(sender.world),
			1,
			0.0,
			0.0,
			0.0,
			0.0,
			Material.LAPIS_BLOCK.createBlockData()
		)
		for (multiblock in ship.multiblocks) {
			val pos = multiblock.getBlockPos(ship.origin, ship.direction)
			sender.world.spawnParticle(
				Particle.BLOCK_MARKER,
				pos.toLocation(sender.world),
				1,
				0.0,
				0.0,
				0.0,
				0.0,
				Material.EMERALD_BLOCK.createBlockData()
			)
		}
	}

	@Subcommand("starship|ship contains")
	@Description("Whether a block is 'inside' the ship")
	private fun onShipCheckContains(sender: Player) {
		val ship = getShip(sender) ?: return
		sender.sendRichMessage(ship.contains(sender.getTargetBlockExact(20)?.toBlockPos()).toString())
	}

	@Subcommand("starship|ship thrusters")
	private fun onShipThrusters(sender: Player) {
		val ship = getShip(sender) ?: return
		sender.sendRichMessage(
			"""
			Ship Heading: ${ship.heading}
			Raw Ship Thrust: ${ship.thrusters.calculateTotalThrust()}
			Actual Ship Thrust: ${ship.thrusters.calculateActualThrust(ship.heading)}
			Ship Mass: ${ship.mass}
			""".trimIndent()
		)
		for (thruster in ship.thrusters.thrusters.mapNotNull { ship.getMultiblock(it) }) {
			sender.sendRichMessage(
				"""
				${thruster.type.displayName}
				-	Facing: ${thruster.direction}
				-	Warmup ${(thruster.data as ThrusterMultiblockData).warmupPercentage}
				""".trimIndent()
			)
		}
	}

	@Subcommand("material|mat get")
	@Description("Check whether the held item is a custom item")
	fun onMaterialGetItem(sender: Player) {
		val item = sender.inventory.itemInMainHand
		val custom = (ItemType.of(item) as? CustomItemType)?.item ?: run {
			sender.sendRichMessage("<gold>This item is not a custom item!")
			return
		}
		sender.sendRichMessage(
			"""
			<green>---- Custom Item ----
			</green>
			ID: ${custom.id}
			Display Name: ${custom.name}<reset>
			Custom Model Data: ${custom.modelData}
			Base Material: ${custom.base}
			${
				if (item.isPowerable) {
					"Power: ${item.power}/${custom.maxPower}\n "
				} else {
					" "
				}
			}
			""".trimIndent()
		)
	}


	@Subcommand("material|mat setpower|sp")
	@Description("Set the power of the held item")
	fun onMaterialSetPower(sender: Player, power: Int) {
		val item = sender.inventory.itemInMainHand
		val custom = ItemType.of(item) as? CustomItemType ?: run {
			sender.sendRichMessage("<gold>This item is not a custom item!")
			return
		}
		if (!item.isPowerable) {
			sender.sendRichMessage("<gold>This item is not powerable!")
			return
		}
		item.power = power
		sender.sendRichMessage("<green>Set power to ${item.power}/${custom.item.maxPower}")
	}


	@Subcommand("material|mat block")
	@Description("Get the block type of the block you're looking at")
	fun onMaterialGetBlock(sender: Player) {
		val block = sender.getTargetBlockExact(10)
		sender.sendRichMessage("<green>Block: ${BlockType.of(block!!)}")
	}

	private fun getShip(sender: Player): Starship? {
		return ((sender.craft ?: run {
			sender.sendRichMessage("<red>You are not riding a craft!")
			return null
		}) as? Starship) ?: run {
			sender.sendRichMessage("<gold>This craft is not a Starship!")
			return null
		}
	}
}