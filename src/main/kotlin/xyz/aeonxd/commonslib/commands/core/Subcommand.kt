package xyz.aeonxd.commonslib.commands.core

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin
import xyz.aeonxd.commonslib.commands.argument.ArgumentProvider
import xyz.aeonxd.commonslib.message.MessageKeyRepo
import xyz.aeonxd.commonslib.message.MessageParserProvider
import xyz.aeonxd.commonslib.message.MessageSenderProvider
import xyz.aeonxd.commonslib.replacer.Replacers.replacedWith

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
     * Names the subcommand is also identified by
     */
    open val aliases: List<String> = emptyList()

    /**
     * Description of what the subcommand does
     */
    abstract val description: String

    /**
     * All subcommand identifiers (name and aliases)
     */
    val identifiers by lazy {
        buildList {
            add(name)
            addAll(aliases)
        }
    }

    abstract fun execute(
        sender: CommandSender, commandAlias: String,
        subcommandAlias: String, args: Array<out String>
    )

    final override fun execute(
        sender: CommandSender,
        commandAlias: String,
        args: Array<out String>
    ) = execute(sender, commandAlias, args[0], args.copyOfRange(1, args.size))

    final override fun onCommand(
        sender: CommandSender, command: Command,
        commandAlias: String, args: Array<out String>
    ): Boolean {
        addReplacer("%subcommandDescription%" replacedWith description)

        /* Permission check */
        if (!checkPermission(sender)) {
            messageSender.sendWithReplacers(sender, MessageKeyRepo.GENERAL_NO_PERMISSION)
            return true
        }

        val argSize = args.size

        /**
         * Assumed to be a subcommand with arguments,
         * e.g. /cfa reset <target>
         */
        if (this is ArgumentProvider) {
            /* Execution condition (provided args are in range) */
            if ((argSize - 1) in minArgSize()..maxArgSize()) {
                execute(sender, commandAlias, args[0], args.copyOfRange(1, args.size))
                return true
            }

            addReplacer("%subcommand+args%" replacedWith "${args[0]} ${arguments().joinToString(separator = " ") { it.name }}")
        } else { /* Subcommand with no arguments, e.g. /cf reset */
            if (argSize == 1) {
                execute(sender, commandAlias, args[0], args.copyOfRange(1, args.size))
                return true
            }
            addReplacer("%subcommand+args%" replacedWith args[0])
        }

        messageSender.sendWithReplacers(sender, MessageKeyRepo.GENERAL_COMMAND_USAGE)
        return true
    }

    /**
     * Executes [suggest] while only providing the subcommand arguments and not the subcommand alias itself
     */
    final override fun onTabComplete(
        sender: CommandSender, command: Command,
        commandAlias: String, args: Array<out String>
    ): MutableList<String> {
        return suggest(sender, command, commandAlias, args.copyOfRange(1, args.size))
    }

}