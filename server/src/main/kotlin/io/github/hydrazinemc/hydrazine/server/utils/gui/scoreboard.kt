package io.github.hydrazinemc.hydrazine.server.utils.gui

import io.github.hydrazinemc.hydrazine.server.utils.extensions.asMiniMessage
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.entity.Player
import org.bukkit.scoreboard.DisplaySlot


fun Player.setScoreboardContents(title: String, lines: List<String>) {
	@Suppress("DEPRECATION") // uh, should probably fix this
	val objective = this.scoreboard.getObjective("hud") ?: this.scoreboard.registerNewObjective(
		"hud",
		"dummy",
		"<b><gold>Dummy Title Text".asMiniMessage // default, gets overwritten
	)
	objective.displaySlot = DisplaySlot.SIDEBAR // Shouldn't need to spam this, but it shouldn't hurt either
	if (objective.displayName() != title.asMiniMessage) objective.displayName(title.asMiniMessage)
	// Clear the current data
	this.scoreboard.entries.forEach(this.scoreboard::resetScores)

	// Reversed because highest score (index) displays at the top, and we want to preserve the order
	lines.reversed().forEachIndexed { i, line ->
		// Can't just use components because scoreboard bad, so we serialize it to section symbol formatting
		// This doesn't support a lot of Component/MiniMessage's advanced features, but colors work.
		objective.getScore(LegacyComponentSerializer.legacySection().serialize(line.asMiniMessage)).score = i
	}
}
