package me.aeon.commonslib.commands

import me.aeon.commonslib.message.MessageParser

interface MessageParserProvider {
    val messageParser: MessageParser
}