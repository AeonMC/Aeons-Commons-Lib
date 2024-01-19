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

        private val MAPPER_FROM_STRING: (String) -> Component = { it.toComponent() }
        private val MAPPER_FROM_STRING_LIST: (List<String>) -> Component = { strings ->
            Component.join(
                newLineJoinConfig,
                strings.map { it.toComponent() }
            )
        }
        private val MAPPER_FROM_LIST: (List<Component>) -> Component = {
            Component.join(newLineJoinConfig, it)
        }

        private fun <T> buildReplacementConfig(matcher: String, replacement: T, mapper: (T) -> Component): TextReplacementConfig {
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
        fun withString(matcher: String, replacement: String) =
            buildReplacementConfig(matcher, replacement, MAPPER_FROM_STRING)

        @JvmStatic
        fun withStringList(matcher: String, replacement: List<String>) =
            buildReplacementConfig(matcher, replacement, MAPPER_FROM_STRING_LIST)

        @JvmStatic
        fun withComponentList(matcher: String, replacement: List<Component>) =
            buildReplacementConfig(matcher, replacement, MAPPER_FROM_LIST)

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
        fun replace(components: List<Component>, replacers: List<TextReplacementConfig>): List<Component> {
            return components.map { replace(it, replacers) }
        }

    }

}