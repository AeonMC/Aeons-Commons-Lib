package xyz.aeonxd.commonslib.commands

import net.kyori.adventure.text.TextReplacementConfig
import org.bukkit.command.CommandSender
import xyz.aeonxd.commonslib.replacer.Replacers.replacedWith
import xyz.aeonxd.commonslib.message.MessageParser
import xyz.aeonxd.commonslib.message.MessageParser.ParseType.*
import xyz.aeonxd.commonslib.message.MessageSender

class HelpMessageSupplier(
    private val command: CoreCommand<*>,
    private val messagePath: String,
    private val messageParser: MessageParser,
    private val messageSender: MessageSender
) {

    private val helpMessageMap = mapOf(
        HEADER to messageParser.get(messagePath, HEADER),
        FOOTER to messageParser.get(messagePath, FOOTER)
    )

    fun send(recipient: CommandSender, commandAlias: String) {
        /* Send header */
        helpMessageMap[HEADER]?.forEach { messageSender.send(recipient, it) }

        /* Parse body and send for each Subcommand */
        val body = messageParser.get(messagePath, BODY)
        command.subcommands.forEach { subcommand ->
            val localReplacers = mutableListOf<TextReplacementConfig>()
            with(localReplacers) {
                add("%command%" replacedWith commandAlias)
                add("%description%" replacedWith subcommand.description)
            }

            if (subcommand is SubcommandArgumentProvider) {
                localReplacers.add("%subcommand+args%" replacedWith "${subcommand.name} ${subcommand.arguments().joinToString(separator = " ") { it.name }}")
            } else {
                localReplacers.add("%subcommand+args%" replacedWith subcommand.name)
            }

            body.forEach { messageSender.send(recipient, it, localReplacers) }
        }

        /* Send footer */
        helpMessageMap[FOOTER]?.forEach { messageSender.send(recipient, it) }
    }

}