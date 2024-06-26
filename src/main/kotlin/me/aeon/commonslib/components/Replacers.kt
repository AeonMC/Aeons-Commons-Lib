package me.aeon.commonslib.components

import me.aeon.commonslib.components.ComponentUtil.Companion.toComponent
import me.clip.placeholderapi.PlaceholderAPI
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.JoinConfiguration
import net.kyori.adventure.text.TextReplacementConfig
import org.bukkit.OfflinePlayer

@Suppress("unused")
class Replacers private constructor() {

    companion object {

        private val newLineJoinConfig = JoinConfiguration.separator(Component.newline())

        private val MAPPER_FROM_STRING_LIST: (List<String>) -> Component = { strings ->
            Component.join(
                newLineJoinConfig,
                strings.map { it.toComponent() }
            )
        }
        private val MAPPER_FROM_LIST: (List<Component>) -> Component = { Component.join(newLineJoinConfig, it) }
        private val MAPPER_DIRECT: (Component) -> Component = { it }
        private val MAPPER_FROM_ANY: (Any?) -> Component = { it.toComponent() }

        private fun <T> buildReplacementConfig(matcher: String, replacement: T, mapper: (T) -> Component = MAPPER_FROM_ANY): TextReplacementConfig {
            return TextReplacementConfig.builder()
                .matchLiteral(matcher)
                .replacement(mapper(replacement))
                .build()
        }

        private fun buildReplacementConfig(viewer: OfflinePlayer?): TextReplacementConfig {
            return TextReplacementConfig.builder()
                .match(PlaceholderAPI.getPlaceholderPattern())
                .replacement { result, _ ->
                    PlaceholderAPI.setPlaceholders(viewer, result.group()).toComponent()
                }
                .build()
        }

        @JvmStatic
        infix fun String.replacedWith(replacement: Any) =
            buildReplacementConfig(this, replacement, MAPPER_FROM_ANY)

        @JvmStatic
        infix fun String.replacedWith(replacement: () -> Any) =
            this replacedWith replacement()

        @JvmStatic
        infix fun String.replacedWith(replacement: Component) =
            buildReplacementConfig(this, replacement, MAPPER_DIRECT)

        @JvmStatic
        infix fun String.replacedWith(replacement: List<Component>) =
            buildReplacementConfig(this, replacement, MAPPER_FROM_LIST)


        @JvmStatic
        fun withPAPI(viewer: OfflinePlayer?) =
            buildReplacementConfig(viewer)

        @JvmStatic
        fun replace(component: Component, replacers: List<TextReplacementConfig>): Component {
            var comp = component
            replacers.forEach { comp = comp.replaceText(it) }
            return comp
        }

        @JvmStatic
        fun replace(component: Component, vararg replacers: TextReplacementConfig): Component {
            return replace(component, replacers.toList())
        }

        @JvmStatic
        fun replace(components: List<Component>, replacers: List<TextReplacementConfig>): List<Component> {
            return components.map { replace(it, replacers) }
        }

        @JvmStatic
        fun replace(components: List<Component>, vararg replacers: TextReplacementConfig): List<Component> {
            return replace(components, replacers.toList())
        }

    }

}