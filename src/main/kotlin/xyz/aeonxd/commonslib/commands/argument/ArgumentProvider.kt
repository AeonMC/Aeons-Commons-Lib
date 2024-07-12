package xyz.aeonxd.commonslib.commands.argument

fun interface ArgumentProvider {
    fun arguments(): List<Argument<*>>

    fun minArgSize(): Int = arguments().count { !it.isOptional }
    fun maxArgSize(): Int = arguments().size
}