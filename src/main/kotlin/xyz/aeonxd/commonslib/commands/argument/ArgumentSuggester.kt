package xyz.aeonxd.commonslib.commands.argument

import org.bukkit.command.Command
import org.bukkit.command.CommandSender

fun interface ArgumentSuggester {

    /**
     * The suggestions when the [CommandSender] presses the **TAB** key
     */
    fun suggest(
        sender: CommandSender, command: Command,
        commandAlias: String, args: Array<out String>
    ): MutableList<String>

}