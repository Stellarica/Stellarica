package net.stellarica.server.util.extension

import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.title.TitlePart
import net.minecraft.core.BlockPos

/**
 * This string as a Component, using MiniMessage formatting
 */
val String.asMiniMessage: Component get() = MiniMessage.miniMessage().deserialize(this.trimIndent())


fun Audience.sendRichMessage(message: String) = this.sendMessage(message.asMiniMessage)

fun Audience.sendRichActionBar(message: String) = this.sendActionBar(message.asMiniMessage)

fun Audience.sendRichTitle(message: String, part: TitlePart<Component>) =
	this.sendTitlePart(part, message.asMiniMessage)

val BlockPos.formatted
	get() = "<dark_gray>(<gray>${this.x}<dark_gray>, <gray>${this.y}<dark_gray>, <gray>${this.z}<dark_gray>)<reset>"