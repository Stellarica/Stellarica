package net.stellarica.server.crafts.pilotables.starships.subsystems.armor

import net.stellarica.server.events.StellaricaConfigReloadEvent
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.spigotmc.SpigotConfig.config

object ArmorValues : Listener {
	private var values = mapOf<Material, Float>()

	operator fun get(type: Material): Float = values[type] ?: 0f

	@EventHandler
	fun onConfigReload(event: StellaricaConfigReloadEvent) {
		values = config.getConfigurationSection("armorValues")?.getKeys(false)?.map {
			Material.valueOf(it) to config.getDouble("armorValues.$it").toFloat()
		}?.toMap() ?: mapOf()
	}
}