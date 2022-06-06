package io.github.hydrazinemc.hydrazine.utils.extensions

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.command.CommandSender

/**
 * Alias for sendMessage(<message>.asMiniMessage)
 */
fun CommandSender.sendMiniMessage(message: String) = sendMessage(message.asMiniMessage)

/**
 * This string as a Component, using MiniMessage formatting
 */
val String.asMiniMessage: Component get() = MiniMessage.miniMessage().deserialize(this.trimIndent())

