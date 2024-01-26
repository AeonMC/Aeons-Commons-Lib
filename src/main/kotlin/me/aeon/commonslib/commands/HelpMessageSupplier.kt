package me.aeon.commonslib.commands

import me.aeon.commonslib.components.Replacers.Companion.replacedWith
import me.aeon.commonslib.message.MessageParser
import me.aeon.commonslib.message.MessageParser.ParseType.*
import me.aeon.commonslib.message.MessageSender
import org.bukkit.command.CommandSender

@Suppress("unused")
class HelpMessageSupplier(
    private val command: CoreCommand, private val messagePath: String,
    private val messageParser: MessageParser, private val messageSender: MessageSender
) {

    private val helpMessageMap = hashMapOf(
        HEADER to messageParser.get(messagePath, HEADER),
        FOOTER to messageParser.get(messagePath, FOOTER)
    )

    fun send(recipient: CommandSender, commandAlias: String) {
        // Send header
        helpMessageMap[HEADER]?.forEach { messageSender.send(recipient, it) }

        // Parse body and send for each Subcommand
        val body = messageParser.get(messagePath, BODY)
        command.subcommands.forEach { subcommand ->

            if (subcommand is SubcommandArgumentProvider) {
                val replacers = buildList {
                    add("%command%" replacedWith "/$commandAlias")
                    add("%description%" replacedWith  subcommand.description)
                    add("%subcommand+args%" replacedWith
                        "${subcommand.name} ${subcommand.arguments().joinToString(separator = " ") { it.name }}")
                }

                body.forEach { messageSender.send(recipient, it, replacers) }
                return@forEach
            }

            val replacers = buildList {
                add("%command%" replacedWith "/$commandAlias")
                add("%subcommand+args%" replacedWith subcommand.name)
                add("%description%" replacedWith subcommand.description)
            }

            body.forEach { messageSender.send(recipient, it, replacers) }
        }

        // Send footer
        helpMessageMap[FOOTER]?.forEach { messageSender.send(recipient, it) }
    }

}