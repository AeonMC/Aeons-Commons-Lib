package xyz.aeonxd.commonslib.message

import dev.dejvokep.boostedyaml.YamlDocument
import dev.dejvokep.boostedyaml.block.implementation.Section
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextReplacementConfig
import xyz.aeonxd.commonslib.replacer.Replacers.replacedWith
import xyz.aeonxd.commonslib.message.MessageKeyRepo.PREFIX
import xyz.aeonxd.commonslib.util.ComponentUtil.toComponent

class MessageParser(private val config: YamlDocument) {

    /**
     * Gets the value(s) from the specified path
     *
     * @param route The path in the [config][YamlDocument] to retrieve the value from
     * @return Deserialized values retrieved from the specified path
     */
    fun get(route: String): List<Component> {
        val components = mutableListOf<Component>()

        if (config.isSection(route)) {
            // Retrieves all the Components from the Section
            config.getSection(route) ?: throw IllegalStateException("Section $route is invalid")
            components.addAll(get("$route.header"))
            components.addAll(get("$route.body"))
            components.addAll(get("$route.footer"))

            return components
        } else if (config.isList(route)) {
            // Retrieves all the Components from the String list
            config.getStringList(route)
                .map { it.toComponent() }
                .forEach { components.add(it) }

            return components
        }

        // Retrieves the Component from the string
        val text: String = config.getString(route) ?: return components

        if (text.isEmpty()) return components

        components.add(text.toComponent())
        return components
    }

    /**
     * @param route The path to the [Section]
     * @return [List] of [Component]s in a [Section]
     */
    fun get(route: String, type: ParseType): List<Component> {
        return if (!config.isSection(route)) emptyList() else get(route + type.route())
    }

    /**
     * Refers to the header, body, or footer of a [Section]
     *
     * Example: get("help-layout", ParseType.**HEADER**)
     */
    enum class ParseType(private val route: String) {
        HEADER(".header"),
        BODY(".body"),
        FOOTER(".footer");

        fun route() = route
    }

    /**
     * Any occurrence of "**%prefix%**" gets replaced with the config value set
     *
     * The replacement can be retrieved with **`get("prefix")`**
     */
    fun prefixReplacer(): TextReplacementConfig = "%prefix%" replacedWith get(PREFIX)

}