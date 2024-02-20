package net.stellarica.server.material.loader

import com.sksamuel.hoplite.ConfigLoader
import com.sksamuel.hoplite.ConfigLoaderBuilder
import net.stellarica.server.StellaricaServer.Companion.plugin

object MaterialLoader {
	private val blockDir = plugin.dataFolder.resolve("content/blocks")
	private val itemDir = plugin.dataFolder.resolve("content/items")

	fun load() {
		loadItems()
	}

	private fun loadItems() {
		val loader = ConfigLoader()

		itemDir.listFiles()!!.map {
			println("Loading item: ${it.name}")
			loader.loadConfigOrThrow<ItemConfig>(it.absolutePath)
		}
	}
}
