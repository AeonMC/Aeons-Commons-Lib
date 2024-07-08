package xyz.aeonxd.commonslib.commands

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextReplacementConfig
import org.bukkit.command.CommandSender
import xyz.aeonxd.commonslib.message.MessageSender

sealed interface ReplacerUtilizer {
    val replacers: MutableList<TextReplacementConfig>
    fun addReplacer(replacer: TextReplacementConfig) = replacers.add(replacer)
    fun addReplacers(vararg replacers: TextReplacementConfig) = replacers.forEach { addReplacer(it) }

    fun clearReplacers() = replacers.clear()

    /* For ease of use */
    fun ReplacerUtilizer.copyReplacersTo(destination: ReplacerUtilizer) {
        destination.clearReplacers()
        this.replacers.forEach { replacer ->
            destination.addReplacer(replacer)
        }
    }
    fun ReplacerUtilizer.copyReplacersFrom(source: ReplacerUtilizer) {
        this.clearReplacers()
        source.replacers.forEach { replacer ->
            this.addReplacer(replacer)
        }
    }

    fun MessageSender.sendWithReplacers(recipient: CommandSender, route: String) = send(recipient, route, replacers)
    fun MessageSender.sendWithReplacers(recipient: CommandSender, message: Component) = send(recipient, message, replacers)
}