package me.aeon.commonslib.commands

fun interface SubcommandArgumentProvider {
    fun arguments(): List<Argument>

    fun minArgSize(): Int = arguments().count { !it.isOptional }
    fun maxArgSize(): Int = arguments().size
}