package io.github.hydrazinemc.hydrazine.utils.gui

import io.github.hydrazinemc.hydrazine.Hydrazine.Companion.plugin
import io.github.hydrazinemc.hydrazine.utils.extensions.asMiniMessage
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.entity.Player
import org.bukkit.scoreboard.DisplaySlot

open class ScoreboardDisplay {
	private val scoreboard = plugin.server.scoreboardManager.newScoreboard

	fun setScoreboard(player: Player, title: String, lines: List<String>){
		if (lines.isEmpty()) return
		player.scoreboard = scoreboard

		@Suppress("DEPRECATION") // uh, should probably fix this
		val objective = player.scoreboard.getObjective("hud") ?: player.scoreboard.registerNewObjective(
			"hud",
			"dummy",
			"<b><gold>Dummy Title Text".asMiniMessage // default, gets overwritten
		)
		objective.displaySlot = DisplaySlot.SIDEBAR // Shouldn't need to spam this, but it shouldn't hurt either
		if (objective.displayName() != title.asMiniMessage) objective.displayName(title.asMiniMessage)

		// Clear the current data
		player.scoreboard.entries.forEach(player.scoreboard::resetScores)

		// Reversed because highest score (index) displays at the top, and we want to preserve the order
		lines.reversed().forEachIndexed { i, line ->
			// Can't just use components because scoreboard bad, so we serialize it to section symbol formatting
			// This doesn't support a lot of Component/MiniMessage's advanced features, but colors work.
			objective.getScore(LegacyComponentSerializer.legacySection().serialize(line.asMiniMessage)).score = i
		}
	}
}