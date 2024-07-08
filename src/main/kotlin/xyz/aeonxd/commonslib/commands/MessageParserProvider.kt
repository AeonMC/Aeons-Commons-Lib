package xyz.aeonxd.commonslib.commands

import xyz.aeonxd.commonslib.message.MessageParser

interface MessageParserProvider {
    val messageParser: MessageParser
}