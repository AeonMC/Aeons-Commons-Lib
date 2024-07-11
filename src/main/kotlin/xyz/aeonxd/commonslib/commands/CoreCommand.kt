package xyz.aeonxd.commonslib.commands

import net.kyori.adventure.text.TextReplacementConfig
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.plugin.java.JavaPlugin
import xyz.aeonxd.commonslib.replacer.Replacers.replacedWith
import xyz.aeonxd.commonslib.message.MessageKeyRepo.GENERAL_COMMAND_DOES_NOT_EXIST
import xyz.aeonxd.commonslib.message.MessageKeyRepo.GENERAL_NO_PERMISSION
import xyz.aeonxd.commonslib.message.MessageSender
import xyz.aeonxd.commonslib.replacer.ReplacerUtilizer
import xyz.aeonxd.commonslib.util.GeneralUtil.filterContains

abstract class CoreCommand<T>(
    protected val plugin: T,
    private val helpMessagePath: String // The supplier of help message (when no arguments are passed)
) : TabExecutor, Permissible, ReplacerUtilizer where T : JavaPlugin,
                                                     T : MessageParserProvider,
                                                     T : MessageSenderProvider {

    override val replacers = mutableListOf<TextReplacementConfig>()
    protected val messageSender: MessageSender = plugin.messageSender

    val subcommands: List<Subcommand<*>> by lazy { subcommands() }
    val helpMessageSupplier by lazy {
        HelpMessageSupplier(
            this, helpMessagePath,
            plugin.messageParser,
            plugin.messageSender
        )
    }

    private val tabSuggestions: List<String> by lazy { tabSuggestions() }
    private val fallbackMinInputLength = 1

    init {
        fun validateSubcommandArgs(arguments: List<Argument>) {
            val optionalCount = arguments.count { it.isOptional }
            if (optionalCount > 1) {
                throw IllegalStateException("Argument cannot contain multiple subarguments")
            }

            if (optionalCount == 1 && !arguments.last().isOptional) {
                throw IllegalStateException("Optional argument must be at the end")
            }
        }

        subcommands
            .filterIsInstance<SubcommandArgumentProvider>()
            .forEach { validateSubcommandArgs(it.arguments()) }
    }

    /**
     * Returns the subcommands of this command
     */
    abstract fun subcommands(): List<Subcommand<*>>

    /**
     * Populate the [suggestion list][tabSuggestions] with subcommand names
     */
    private fun tabSuggestions(): List<String> {
        return subcommands
            .map { it.name }
    }

    /**
     * Returns the found [Subcommand] or `null` if not found
     */
    private fun findSubcommand(arg: String): Subcommand<*>? {
        return subcommands.firstOrNull { subcommand ->
            subcommand.identifiers
                .any { id -> id.equals(arg, true) }
        }
    }

    override fun onCommand(
        sender: CommandSender, command: Command,
        commandAlias: String, args: Array<out String>
    ): Boolean {
        addReplacers(
            "%sender%" replacedWith sender.name,
            "%command%" replacedWith commandAlias,
            "%commandDescription%" replacedWith command.description
        )

        if (!checkPermission(sender)) {
            messageSender.sendWithReplacers(sender, GENERAL_NO_PERMISSION, true)
            return true
        }

        if (args.isEmpty()) {
            copyReplacersTo(helpMessageSupplier)
            helpMessageSupplier.send(sender, commandAlias)
            helpMessageSupplier.clearReplacers()
            return true
        }

        val subcommandName = args[0]
        addReplacer("%subcommand%" replacedWith subcommandName)
        val subcommand = findSubcommand(subcommandName)
        if (subcommand == null) {
            messageSender.sendWithReplacers(sender, GENERAL_COMMAND_DOES_NOT_EXIST, true)
            return true
        }

        copyReplacersTo(subcommand)
        subcommand.onCommand(sender, command, commandAlias, args)
        subcommand.clearReplacers()
        clearReplacers()

        return true
    }

    /**
     * Possible returns
     * - an empty list if sender has no access to this command
     * - subcommand names if arguments size is 1, unless it's empty - then returns the alias names
     * - the found subcommand's implementation of [onTabComplete] if arguments size isn't 1
     * - an empty list if no subcommand was found
     */
    override fun onTabComplete(
        sender: CommandSender, command: Command,
        alias: String, args: Array<out String>
    ): MutableList<String> {
        if (!checkPermission(sender)) return mutableListOf()
        val input = args[0]

        if (args.size == 1) {

            val availableSubcommands = subcommands.filter { sender.hasPermission(it.permission) }

            /* Main subcommand names */
            val suggestions = availableSubcommands
                .map { it.name }
                .filterContains(input)

            /* Fallback (subcommand alias names) */
            if (suggestions.isEmpty() && input.length >= fallbackMinInputLength) {
                return availableSubcommands
                    .flatMap { it.aliases }
                    .filterContains(input)
            }

            return suggestions
        }

        return findSubcommand(input)
            ?.onTabComplete(sender, command, alias, args)
            ?: mutableListOf()
    }

}