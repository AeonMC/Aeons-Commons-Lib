package me.aeon.commonslib.commands

import me.aeon.commonslib.message.MessageSender
import org.bukkit.command.TabExecutor
import org.bukkit.plugin.java.JavaPlugin

abstract class StandardCommand<T>(
    @Suppress("UNUSED")
    protected val plugin: T,
) : TabExecutor, Permissible where T : JavaPlugin,
                                   T : MessageParserProvider,
                                   T : MessageSenderProvider {

    protected val messageSender: MessageSender = plugin.messageSender

}