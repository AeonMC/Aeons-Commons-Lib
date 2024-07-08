package xyz.aeonxd.commonslib.message

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextReplacementConfig
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import xyz.aeonxd.commonslib.commands.ReplacerUtilizer
import xyz.aeonxd.commonslib.components.Replacers
import xyz.aeonxd.commonslib.components.Replacers.replace

class MessageSender(val parser: MessageParser) {

    private val prefixReplacer = parser.prefixReplacer()

    fun send(recipient: CommandSender, route: String, replacers: List<TextReplacementConfig>) {
        parser.get(route)
            .forEach { send(recipient, replace(it, replacers)) }
    }

    fun send(recipient: CommandSender, route: String, vararg replacers: TextReplacementConfig) {
        send(recipient, route, replacers.toList())
    }

    fun send(recipient: CommandSender, route: String, replacer: TextReplacementConfig) {
        parser.get(route)
            .forEach { send(recipient, it.replaceText(replacer)) }
    }

    fun send(recipient: CommandSender, route: String) {
        parser.get(route)
            .forEach { send(recipient, it) }
    }

    fun send(recipient: CommandSender, message: Component, replacers: List<TextReplacementConfig>) {
        send(recipient, replace(message, replacers))
    }

    fun send(recipient: CommandSender, message: Component, vararg replacers: TextReplacementConfig) {
        send(recipient, replace(message, *replacers))
    }

    /**
     * Sends the specified [message] while applying the default replacers (%prefix% and any PAPI placeholder)
     *
     * Every `send` function funnels down into this one
     */
    fun send(recipient: CommandSender, message: Component) {
        recipient.sendMessage(replace(message, getDefaultReplacers(recipient)))
    }

    private fun getDefaultReplacers(
        recipient: CommandSender
    ): List<TextReplacementConfig> = buildList {
        add(prefixReplacer)
        add(Replacers.withPAPI(if (recipient is OfflinePlayer) recipient else null))
    }

}