package me.aeon.commonslib.message

import me.aeon.commonslib.components.Replacers
import me.aeon.commonslib.components.Replacers.Companion.replace
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextReplacementConfig
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender

class MessageSender(private val parser: MessageParser) {

    private val prefixReplacer = parser.prefixReplacer()

    fun send(recipient: CommandSender, key: String, replacers: List<TextReplacementConfig>) {
        parser.get(key)
            .forEach { send(recipient, replace(it, replacers)) }
    }

    fun send(recipient: CommandSender, key: String, replacer: TextReplacementConfig) {
        parser.get(key)
            .forEach { send(recipient, it.replaceText(replacer)) }
    }

    fun send(recipient: CommandSender, key: String) {
        parser.get(key)
            .forEach { send(recipient, it) }
    }

    fun send(recipient: CommandSender, message: Component, replacers: List<TextReplacementConfig>) {
        send(recipient, replace(message, replacers))
    }

    /**
     * Sends the specified [message] while adding the default replacers (%prefix% and any PAPI placeholder)
     *
     * Every `send` function funnels down into this one
     */
    fun send(recipient: CommandSender, message: Component) {
        recipient.sendMessage(replace(message, defaultReplacers(recipient)))
    }

    val defaultReplacers: (recipient: CommandSender) -> List<TextReplacementConfig> = { recipient ->
        buildList {
            add(prefixReplacer)
            Replacers.withPAPI(if (recipient is OfflinePlayer) recipient else null)
        }
    }

}