package me.aeon.commonslib.components

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage

class ComponentUtil private constructor() {

    companion object {
        private val miniMessage = MiniMessage.miniMessage()

        /**
         * Extension function that converts a [String] into a [Component]
         */
        @JvmStatic
        fun String.toComponent() = miniMessage.deserialize(this)
    }

}