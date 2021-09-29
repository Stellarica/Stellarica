package io.github.petercrawley.minecraftstarshipplugin

import io.github.petercrawley.minecraftstarshipplugin.commands.CommandTabComplete
import io.github.petercrawley.minecraftstarshipplugin.commands.Commands
import io.github.petercrawley.minecraftstarshipplugin.customblocks.CustomBlocksListener
import io.github.petercrawley.minecraftstarshipplugin.starships.InterfaceListener
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.translatable
import net.kyori.adventure.text.format.Style.style
import net.kyori.adventure.text.format.TextColor.color
import net.kyori.adventure.text.format.TextDecoration
import org.bstats.bukkit.Metrics
import org.bukkit.Bukkit
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import org.hjson.JsonObject
import org.hjson.JsonValue
import java.io.File

class MinecraftStarshipPlugin: JavaPlugin() {
	companion object {
		// I spent far too long trying to do this with kotlin getters and setters... I give up.
		private lateinit var plugin: MinecraftStarshipPlugin

		fun getPlugin(): MinecraftStarshipPlugin {
			return plugin
		}

		fun itemWithName(material: org.bukkit.Material, name: String, colorR: Int = 255, colorG: Int = 255, colorB: Int = 255, bold: Boolean = false, italic: Boolean = false): ItemStack {
			val item = ItemStack(material)

			val itemMeta = item.itemMeta

			itemMeta.displayName(Component.text(name).style(style(color(colorR, colorG, colorB)).decoration(TextDecoration.ITALIC, italic).decoration(TextDecoration.BOLD, bold)))

			item.itemMeta = itemMeta

			return item
		}

		fun itemWithTranslatableName(material: org.bukkit.Material, name: String, colorR: Int = 255, colorG: Int = 255, colorB: Int = 255, bold: Boolean = false, italic: Boolean = false): ItemStack {
			val item = ItemStack(material)

			val itemMeta = item.itemMeta

			itemMeta.displayName(translatable(name).style(style(color(colorR, colorG, colorB)).decoration(TextDecoration.ITALIC, italic).decoration(TextDecoration.BOLD, bold)))

			item.itemMeta = itemMeta

			return item
		}

		var forcedUndetectable = mutableSetOf<io.github.petercrawley.minecraftstarshipplugin.customblocks.MSPMaterial>()
		var defaultUndetectable = mutableSetOf<io.github.petercrawley.minecraftstarshipplugin.customblocks.MSPMaterial>()

		var mainConfig: JsonObject = JsonObject()
	}

	private fun saveDefault(name:String) {
		if (!File(plugin.dataFolder, name).exists()){
			// Although it won't overwrite it creates some useless warnings
			plugin.saveResource(name, false)
		}
	}

	override fun onEnable() {
		Metrics(this, 12863)

		plugin = this

		reloadConfig()

		Bukkit.getPluginManager().registerEvents(InterfaceListener(), this)
		Bukkit.getPluginManager().registerEvents(CustomBlocksListener(), this)

		plugin.getCommand("msp")!!.setExecutor(Commands())
		plugin.getCommand("msp")!!.tabCompleter = CommandTabComplete()
	}

	override fun reloadConfig() {
		plugin.saveDefault("undetectables.hjson")
		plugin.saveDefault("config.hjson")

		// Get the non-detectable blocks from the config file
		forcedUndetectable = mutableSetOf()
		defaultUndetectable = mutableSetOf()

		val configFile = JsonValue.readHjson(File(plugin.dataFolder, "undetectables.hjson").bufferedReader()).asObject()

		configFile["forcedUndetectable"].asArray().forEach {
			val value = io.github.petercrawley.minecraftstarshipplugin.customblocks.MSPMaterial(it.asString())

			if (value == null) logger.warning("No Material for $value! Make sure all forced undetectable blocks are correctly named!")
			else forcedUndetectable.add(value)
		}

		configFile["defaultUndetectable"].asArray().forEach {
			val value = io.github.petercrawley.minecraftstarshipplugin.customblocks.MSPMaterial(it.asString())

			if (value == null) logger.warning("No Material for $value! Make sure all default undetectable blocks are correctly named!")
			else defaultUndetectable.add(value)
		}

		mainConfig = JsonValue.readHjson(File(plugin.dataFolder, "config.hjson").bufferedReader()).asObject()!!
	}
}