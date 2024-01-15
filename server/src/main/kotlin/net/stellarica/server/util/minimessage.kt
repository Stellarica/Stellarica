package net.stellarica.server.util

import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.Tag
import net.kyori.adventure.title.TitlePart

private val miniMessage = MiniMessage.builder().editTags {
	it.tag("error", Tag.styling(TextColor.color(0xBF441B)))
	it.tag("success", Tag.styling(TextColor.color(0x3ABF41)))
	it.tag("debug", Tag.styling(TextColor.color(0x4B4B6E)))
}.build()

val String.asMiniMessage: Component get() = miniMessage.deserialize(this.trimIndent())


fun Audience.sendRichMessage(message: String) = this.sendMessage(message.asMiniMessage)
fun Audience.sendRichActionBar(message: String) = this.sendActionBar(message.asMiniMessage)
fun Audience.sendRichTitle(message: String, part: TitlePart<Component>) =
	this.sendTitlePart(part, message.asMiniMessage)
