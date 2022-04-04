package io.github.hydrazinemc.hydrazine

import co.aikar.commands.PaperCommandManager
import io.github.hydrazinemc.hydrazine.commands.ConfigCommand
import io.github.hydrazinemc.hydrazine.customMaterials.CustomBlocksListener
import io.github.hydrazinemc.hydrazine.events.HydrazineConfigReloadEvent
import io.github.hydrazinemc.hydrazine.starships.listeners.InterfaceListener
import io.github.hydrazinemc.hydrazine.starships.Starship
import io.github.hydrazinemc.hydrazine.starships.StarshipBlockSetter
import io.github.hydrazinemc.hydrazine.starships.listeners.StarshipControlListener
import io.github.hydrazinemc.hydrazine.utils.ConfigurableValues
import io.github.hydrazinemc.hydrazine.utils.Vector3
import io.github.hydrazinemc.hydrazine.utils.rotateCoordinates
import org.bukkit.Bukkit.getPluginManager
import org.bukkit.plugin.java.JavaPlugin

class Hydrazine : JavaPlugin() {
	companion object {
		lateinit var plugin: Hydrazine
			private set

		var activeStarships = mutableSetOf<Starship>()
	}

	override fun onEnable() {
		plugin = this

		getPluginManager().registerEvents(CustomBlocksListener(), this)
		getPluginManager().registerEvents(InterfaceListener(), this)
		getPluginManager().registerEvents(StarshipControlListener(), this)

		//   /-\
		//  / ! \  MUST BE CALLED AFTER REGISTERING EVENTS!
		// /_____\
		saveDefaultConfig()
		reloadConfig()

		val commandManager = PaperCommandManager(this)
		commandManager.registerCommand(ConfigCommand())

		StarshipBlockSetter.runTaskTimer(plugin, 0, 1)

		val one = Vector3(4.0, 3.0, 6.0)
		val origin = Vector3(2.0, 0.0, -2.0)
		val amount = Math.PI / 2
		logger.warning("$one rotated around $origin by $amount radians is ${rotateCoordinates(one, origin, amount)}")
	}

	override fun reloadConfig() {
		super.reloadConfig()
		getPluginManager().callEvent(HydrazineConfigReloadEvent())
		ConfigurableValues.loadFromConfig()
	}
}
