package io.github.hydrazinemc.hydrazine.utils.extensions

import io.github.hydrazinemc.hydrazine.utils.locations.BlockLocation
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.block.Block

/**
 * This string as a Component, using MiniMessage formatting
 */
val String.asMiniMessage: Component get() = MiniMessage.miniMessage().deserialize(this.trimIndent())

val Block.blockLocation: BlockLocation get() = BlockLocation(this.location)
