package net.stellarica.server.util

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandCompletion
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Description
import co.aikar.commands.annotation.Subcommand
import com.comphenix.protocol.wrappers.nbt.NbtCompound
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import net.minecraft.core.BlockPos
import net.stellarica.common.util.OriginRelative
import net.stellarica.server.StellaricaServer
import net.stellarica.server.StellaricaServer.Companion.scriptingEnabled
import net.stellarica.server.craft.starship.Starship
import net.stellarica.server.material.custom.item.isPowerable
import net.stellarica.server.material.custom.item.power
import net.stellarica.server.material.type.block.BlockType
import net.stellarica.server.material.type.item.CustomItemType
import net.stellarica.server.material.type.item.ItemType
import net.stellarica.server.multiblock.MultiblockHandler
import net.stellarica.server.multiblock.MultiblockInstance
import net.stellarica.server.multiblock.Multiblocks
import net.stellarica.server.multiblock.data.ThrusterMultiblockData
import net.stellarica.server.multiblock.matching.BlockTagMatcher
import net.stellarica.server.multiblock.matching.MultiBlockMatcher
import net.stellarica.server.multiblock.matching.SingleBlockMatcher
import net.stellarica.server.transfer.FuelPacket
import net.stellarica.server.transfer.PipeHandler
import net.stellarica.server.util.extension.asMiniMessage
import net.stellarica.server.util.extension.craft
import net.stellarica.server.util.extension.formatted
import net.stellarica.server.util.extension.toBlockPos
import net.stellarica.server.util.extension.toLocation
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.World
import org.bukkit.command.CommandSender
import org.bukkit.craftbukkit.v1_19_R3.inventory.CraftItemStack
import org.bukkit.entity.Player
import org.graalvm.polyglot.Context
import org.graalvm.polyglot.PolyglotException
import java.io.BufferedOutputStream
import java.io.ObjectOutputStream
import java.io.PrintStream
import java.io.PrintWriter
import java.io.StringWriter
import java.io.Writer
import java.util.logging.Handler
import java.util.logging.LogRecord

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
	private fun onMultiblockInstance(sender: Player) {
		val pos = sender.getTargetBlockExact(10) ?: return
		val mb = MultiblockHandler[pos.chunk].firstOrNull { it.contains(pos.toBlockPos()) } ?: run {
			sender.sendRichMessage("<red>No multiblock found!")
			return
		}
		sender.sendRichMessage(dumpMultiblockInstance(mb))
	}

	@Subcommand("multiblock|mb instance")
	@Description("Get multiblock instance information")
	private fun onMultiblockInstance(sender: CommandSender, x: Int, y: Int, z: Int, world: World? = null) {
		val w = if (world == null && sender is Player) sender.world else world ?: return
		val pos = BlockPos(x, y, z)
		val mb = MultiblockHandler[pos.toLocation(w).chunk].firstOrNull { it.contains(pos) } ?: run {
			sender.sendRichMessage("<red>No multiblock found!")
			return
		}
		sender.sendRichMessage(dumpMultiblockInstance(mb))
	}

	private fun dumpMultiblockInstance(mb: MultiblockInstance): String = """
		<white>Type: <click:run_command:/debug mb type ${mb.type.id.path}><aqua><u>${mb.type.displayName}</u></click>
		<white>Origin:<gray> ${mb.origin.formatted}
		<white>Direction:<gray> ${mb.direction}
		<white>ID:<gray> ${mb.id}
		<white>Data:<gray> ${Json.encodeToString(mb.data)}
		""".trimIndent()


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
		sender.sendRichMessage("<green>Displaying ship contents")
	}

	@Subcommand("starship|ship thrusters")
	private fun onShipThrusters(sender: Player) {
		val ship = getShip(sender) ?: return
		sender.sendRichMessage(
				"""
			<white>Ship Heading:<gray> ${ship.heading.formatted}
			<white>Raw Ship Thrust:<gray> ${ship.thrusters.calculateTotalThrust().formatted}
			<white>Actual Ship Thrust:<gray> ${ship.thrusters.calculateActualThrust(ship.heading).formatted}
			<white>Ship Mass:<gray> ${ship.mass}
			<white>Thrusters:<gray> (${ship.thrusters.thrusters.size})
		""".trimIndent()
		)
		for (thruster in ship.thrusters.thrusters.mapNotNull { ship.getMultiblock(it) }) {
			sender.sendRichMessage(
					"""
				  <gray>- <white>${thruster.type.displayName}
				     <white>Facing:<gray> ${thruster.direction}
				     <white>Warmup:<gray> ${(thruster.data as ThrusterMultiblockData).warmupPercentage}
			""".trimIndent()
			)
		}
	}

	@Subcommand("starship|ship info")
	private fun onShipInfo(sender: Player) {
		val ship = getShip(sender) ?: return
		sender.sendRichMessage("""
			<white>Origin:<gray> ${ship.origin.formatted}
			<white>Direction:<gray> ${ship.direction}
			<white>Block Count:<gray> ${ship.detectedBlockCount}
			<white>Time Spent Moving:<gray> ${ship.timeSpentMoving}ms
			""".trimIndent() + if (ship.multiblocks.isNotEmpty()) {
			"\n<white>Multiblocks: <gray>(${ship.multiblocks.size})" +
					ship.multiblocks.mapNotNull { ship.getMultiblock(it) }.joinToString { inst ->
						"\n<gray>- <click:run_command:/debug mb instance ${inst.origin.x} ${inst.origin.y} ${inst.origin.y} ${inst.world.name}><u><aqua>${inst.type.displayName}<reset>"
					}
		} else {
			"\n<white>No Multiblocks Found"
		}
		)
		onShipThrusters(sender)
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
			<white>ID:<gray> ${custom.id}
			<white>Display Name:<gray> ${custom.name}<reset>
			<white>Custom Model Data:<gray> ${custom.modelData}
			<white>Base Material:<gray> ${custom.base}
			""".trimIndent() +
						if (item.isPowerable) {
							"\n<white>Power: <gray>${item.power}/${custom.maxPower}\n "
						} else ""
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

	@Subcommand("script eval")
	fun onScriptEval(sender: Player) {
		if (!scriptingEnabled) {
			sender.sendRichMessage("<red>Scripting has been disabled! Check the log for details")
			return
		}
		val item = (sender.inventory.itemInMainHand as? CraftItemStack)?.handle ?: return
		val json = item.orCreateTag.getList("pages", 8).getString(0)
		val text = LegacyComponentSerializer.legacyAmpersand().serialize(GsonComponentSerializer.gson().deserialize(json))
		val ctx = Context.newBuilder("python")
			.allowExperimentalOptions(true)
			.option("sandbox.MaxCPUTime", "2000ms")
			.option("sandbox.MaxCPUTimeCheckInterval", "5ms")
			.option("sandbox.MaxHeapMemory", "100MB")
			.option("sandbox.MaxStatements", "500")
			.option("sandbox.MaxStatementsIncludeInternal", "false")
			.build()
		try {
			ctx.eval("python", text)
		}
		catch (e: PolyglotException) {
			if (e.isResourceExhausted) {
				sender.sendRichMessage("<red><b>Script exceeded resource limits!")
			} else if (e.isHostException) {
				sender.sendRichMessage("<red><b>Internal error executing script!")
			} else {
				sender.sendRichMessage("<red><b>Script error!")
			}
			sender.sendRichMessage(
				"""
				<red>${e.localizedMessage}
				<red>Line: ${e.sourceLocation?.startLine}, Col: ${e.sourceLocation?.startColumn}
				<red>Near: <gray>${e.sourceLocation?.characters}
				""".trimIndent()
			)
		}
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