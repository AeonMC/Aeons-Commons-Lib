package me.aeon.commonslib.commands

import org.bukkit.command.CommandSender

interface Permissible {
    val permission: String
    fun checkPermission(sender: CommandSender) = sender.hasPermission(permission)
}