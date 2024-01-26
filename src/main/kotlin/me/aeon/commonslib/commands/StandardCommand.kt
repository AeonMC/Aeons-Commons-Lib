package me.aeon.commonslib.commands

import me.aeon.commonslib.message.MessageSender
import org.bukkit.command.TabExecutor
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.util.StringUtil

/**
 * Returns a new list containing elements that start with [input]
 */
fun List<String>.filterInput(input: String): MutableList<String> {
    if (this.isEmpty()) return mutableListOf()
    if (this.size == 1 && this[0].startsWith(input)) return toMutableList()
    return StringUtil.copyPartialMatches(input, this, mutableListOf())
}

abstract class StandardCommand<T>(
    @Suppress("unused")
    protected val plugin: T,
) : TabExecutor, Permissible where T : JavaPlugin,
                                   T : MessageParserProvider,
                                   T : MessageSenderProvider {

    protected val messageSender: MessageSender = plugin.messageSender

}