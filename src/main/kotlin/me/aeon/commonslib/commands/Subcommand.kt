package me.aeon.commonslib.commands

import me.aeon.commonslib.components.Replacers.replacedWith
import me.aeon.commonslib.message.MessageKeyRepo.GENERAL_COMMAND_USAGE
import me.aeon.commonslib.message.MessageKeyRepo.GENERAL_NO_PERMISSION
import me.aeon.commonslib.util.GeneralUtil.filterContains
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin

/**
 * Describes a subcommand of a [CoreCommand] with possible arguments (by implementing [SubcommandArgumentProvider])
 */
@Suppress("UNUSED")
abstract class Subcommand<T>(
    plugin: T
) : StandardCommand<T>(plugin) where T : JavaPlugin,
                                     T : MessageParserProvider,
                                     T : MessageSenderProvider {

    /**
     * Name of the subcommand
     */
    abstract val name: String

    /**
     * Names the subcommand is also identified as
     */
    open val aliases: List<String> = emptyList()

    /**
     * Description of what the subcommand does
     */
    abstract val description: String

    /**
     * All subcommand identifiers (name and aliases)
     */
    fun identifiers() = sequenceOf(name).plus(aliases)

    /**
     * The execution logic of the subcommand.
     * Before this function is executed, it is
     * made sure that the sender has permission
     * to both the main command and the subcommand.
     * The command is ready to execute without any exceptions
     * @param commandAlias Main command identifier used
     * @param subcommandAlias Subcommand identifier used
     * @param args Subcommand arguments
     */
    abstract fun execute(
        sender: CommandSender, commandAlias: String,
        subcommandAlias: String, args: Array<out String>
    )

    final override fun onCommand(
        sender: CommandSender, command: Command,
        commandAlias: String, args: Array<out String>
    ): Boolean {

        /* Permission check */
        if (!checkPermission(sender)) {
            messageSender.send(sender, GENERAL_NO_PERMISSION)
            return true
        }

        val argSize = args.size

        // Assumed to be a subcommand with arguments,
        // e.g. /cfa reset <target>
        if (this is SubcommandArgumentProvider) {

            /* Execution condition */
            if ((argSize - 1 >= minArgSize()) && (argSize - 1 <= maxArgSize())) {
                execute(sender, commandAlias, args[0], args.copyOfRange(1, args.size))
                return true
            }

            val replacers = buildList {
                add("%command%" replacedWith "/$commandAlias")
                add("%subcommand+args%" replacedWith
                    "${args[0]} ${arguments().joinToString(separator = " ") { it.name }}")
                add("%description%" replacedWith description)
            }

            messageSender.send(sender, GENERAL_COMMAND_USAGE, replacers)
            return true
        } else {
            if (argSize == 1) {
                execute(sender, commandAlias, args[0], args.copyOfRange(1, args.size))
                return true
            }
        }

        val replacers = buildList {
            add("%command%" replacedWith "/$commandAlias")
            add("%subcommand+args%" replacedWith args[0])
            add("%description%" replacedWith description)
        }

        messageSender.send(sender, GENERAL_COMMAND_USAGE, replacers)
        return true
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): MutableList<String> {
        return suggest(sender, command, label, args[0], args.copyOfRange(1, args.size))
    }

    /**
     * The suggestions when the [CommandSender] presses the **TAB** key
     */
    open fun suggest(
        sender: CommandSender, command: Command,
        commandAlias: String, subcommandAlias: String,
        args: Array<out String>
    ): MutableList<String> {

        /* Insufficient permission */
        if (!checkPermission(sender)) return mutableListOf()

        /* No need for other suggestions */
        if (this !is SubcommandArgumentProvider) {
            return mutableListOf()
        }

        val arguments = arguments()
        val lastIndex = args.size - 1

        /* Exceeding arguments */
        if (args.size > arguments.size) return mutableListOf()

        val argument = arguments[lastIndex]
        val input = args[lastIndex]
        val suggestions = argument.suggestions.filterContains(input)

        /* Qualify for fallback suggestion */
        if (suggestions.isEmpty() && input.length >= argument.fallbackMinInputLength) {
            val fallbackSuggestions = argument.fallbackSuggestions ?: return mutableListOf()
            if (fallbackSuggestions.isNotEmpty()) {
                return fallbackSuggestions.filterContains(input)
            }
        }

        return suggestions
    }

}