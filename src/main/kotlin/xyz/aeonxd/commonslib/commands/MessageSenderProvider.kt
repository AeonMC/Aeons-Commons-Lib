package xyz.aeonxd.commonslib.commands

import xyz.aeonxd.commonslib.message.MessageSender

interface MessageSenderProvider {
    val messageSender: MessageSender
}