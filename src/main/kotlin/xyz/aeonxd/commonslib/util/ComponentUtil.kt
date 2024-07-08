package xyz.aeonxd.commonslib.util

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage

object ComponentUtil {

    private val miniMessage = MiniMessage.miniMessage()

    /**
     * Converts [any nullable][Any] object into a [Component]
     */
    @JvmStatic
    fun Any?.toComponent() = miniMessage.deserialize(this.toString())

}