package xyz.aeonxd.commonslib.commands

import net.kyori.adventure.text.TextReplacementConfig
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.plugin.java.JavaPlugin
import xyz.aeonxd.commonslib.components.Replacers.replacedWith
import xyz.aeonxd.commonslib.message.MessageKeyRepo
import xyz.aeonxd.commonslib.message.MessageSender

@Suppress("UNUSED")
abstract class StandardCommand<T>(
    @Suppress("UNUSED")
    protected val plugin: T,
) : TabExecutor, Permissible, ReplacerUtilizer where T : JavaPlugin,
                                                     T : MessageParserProvider,
                                                     T : MessageSenderProvider {

    override val replacers = mutableListOf<TextReplacementConfig>()
    protected val messageSender: MessageSender = plugin.messageSender

    /**
     * The execution logic of the command.
     * Before this function is executed, it is
     * made sure that the sender has permission
     * @param commandAlias Main command identifier used
     * @param args Command arguments
     */
    abstract fun execute(
        sender: CommandSender,
        commandAlias: String,
        args: Array<out String>
    )

    override fun onCommand(
        sender: CommandSender, command: Command,
        commandAlias: String, args: Array<out String>
    ): Boolean {
        addReplacers(
            "%sender%" replacedWith sender.name,
            "%command%" replacedWith commandAlias,
            "%description%" replacedWith command.description
        )

        if (!checkPermission(sender)) {
            messageSender.sendWithReplacers(sender, MessageKeyRepo.GENERAL_NO_PERMISSION, true)
            return true
        }

        execute(sender, commandAlias, args)
        clearReplacers()
        return true
    }

}