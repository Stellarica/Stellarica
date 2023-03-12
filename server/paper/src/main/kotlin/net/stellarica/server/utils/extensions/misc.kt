package net.stellarica.server.utils.extensions

import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage

/**
 * This string as a Component, using MiniMessage formatting
 */
val String.asMiniMessage: Component get() = MiniMessage.miniMessage().deserialize(this.trimIndent())


fun Audience.sendRichMessage(message: String) = this.sendMessage(message.asMiniMessage)

fun Audience.sendRichActionBar(message: String) = this.sendActionBar(message.asMiniMessage)