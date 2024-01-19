package me.aeon.commonslib.commands

import me.aeon.commonslib.message.MessageSender

interface MessageSenderProvider {
    val messageSender: MessageSender
}