package xyz.aeonxd.commonslib.commands.core

import net.kyori.adventure.text.TextReplacementConfig
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.plugin.java.JavaPlugin
import xyz.aeonxd.commonslib.commands.Permissible
import xyz.aeonxd.commonslib.commands.argument.Argument
import xyz.aeonxd.commonslib.commands.argument.ArgumentProvider
import xyz.aeonxd.commonslib.commands.argument.ArgumentSuggester
import xyz.aeonxd.commonslib.message.MessageKeyRepo
import xyz.aeonxd.commonslib.message.MessageParserProvider
import xyz.aeonxd.commonslib.message.MessageSenderProvider
import xyz.aeonxd.commonslib.replacer.ReplacerUtilizer
import xyz.aeonxd.commonslib.replacer.Replacers.replacedWith
import xyz.aeonxd.commonslib.util.GeneralUtil.filterContains

abstract class StandardCommand<T>(
    @Suppress("UNUSED")
    protected val plugin: T
) : TabExecutor, /* Main command execution logic */
    ArgumentSuggester, /* Logic for suggesting arguments */
    Permissible, /* Permission-based command */
    ReplacerUtilizer,
    MessageSenderProvider
        where T : JavaPlugin,
              T : MessageParserProvider,
              T : MessageSenderProvider {


    final override val replacers = mutableListOf<TextReplacementConfig>()
    override val messageSender = plugin.messageSender


    abstract fun execute(
        sender: CommandSender,
        commandAlias: String,
        args: Array<out String>
    )

    final override fun suggest(
        sender: CommandSender, command: Command,
        commandAlias: String, args: Array<out String>
    ): MutableList<String> {
        /* Insufficient permission */
        if (!checkPermission(sender)) return mutableListOf()
        /* No need for other suggestions */
        if (this !is ArgumentProvider) return mutableListOf()

        val arguments = arguments()
        /* Exceeding arguments */
        if (args.size > arguments.size) return mutableListOf()

        val lastIndex = args.lastIndex

        @Suppress("UNCHECKED_CAST")
        val argument = arguments[lastIndex] as Argument<Any>
        val input = args[lastIndex]
        val suggestionCondition = argument.suggestionCondition
        val suggestions = argument.suggestions()
            .filter { suggestionCondition(sender, it) }
            .map { argument.mapToString(it) }
            .filterContains(input)

        /* Qualify for fallback suggestion */
        if (suggestions.isEmpty() && input.length > argument.fallbackMinInputLength) {
            val fallbackSuggestions = argument.fallbackSuggestions()
            if (fallbackSuggestions.isNotEmpty()) {
                return fallbackSuggestions
                    .filter { suggestionCondition(sender, it) }
                    .map { argument.mapToString(it) }
                    .filterContains(input)
            }
        }

        return suggestions
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
            messageSender.sendWithReplacers(sender, MessageKeyRepo.GENERAL_NO_PERMISSION, true)
            return true
        }

        execute(sender, commandAlias, args)
        clearReplacers()
        return true
    }

    override fun onTabComplete(
        sender: CommandSender, command: Command,
        commandAlias: String, args: Array<out String>
    ): MutableList<String> {
        return suggest(sender, command, commandAlias, args)
    }

}