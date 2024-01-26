package me.aeon.commonslib.message

import dev.dejvokep.boostedyaml.YamlDocument
import dev.dejvokep.boostedyaml.block.implementation.Section
import me.aeon.commonslib.components.ComponentUtil.Companion.toComponent
import me.aeon.commonslib.components.Replacers
import me.aeon.commonslib.components.Replacers.Companion.replacedWith
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextReplacementConfig

class MessageParser(private val config: YamlDocument) {

    /**
     * Gets the value(s) from the specified path
     *
     * @param key The path in the [config][YamlDocument] to retrieve the value from
     * @return Deserialized values retrieved from the specified path
     */
    fun get(key: String): List<Component> {
        val components = mutableListOf<Component>()

        if (config.isSection(key)) {
            // Retrieves all the Components from the Section
            config.getSection(key) ?: throw IllegalStateException("Section $key is invalid")
            components.addAll(get("$key.header"))
            components.addAll(get("$key.body"))
            components.addAll(get("$key.footer"))

            return components
        } else if (config.isList(key)) {
            // Retrieves all the Components from the String list
            config.getStringList(key)
                .map { it.toComponent() }
                .forEach { components.add(it) }

            return components
        }

        // Retrieves the Component from the string
        val text: String = config.getString(key) ?: return components

        if (text.isEmpty()) return components

        components.add(text.toComponent())
        return components
    }

    /**
     * @param key The path to the [Section]
     * @return [List] of [Component]s in a [Section]
     */
    fun get(key: String, type: ParseType): List<Component> {
        return if (!config.isSection(key)) emptyList() else get(key + type.path())
    }

    /**
     * Refers to the header, body, or footer of a [Section]
     *
     * Example: get("help-layout", ParseType.**HEADER**)
     */
    enum class ParseType(private val path: String) {
        HEADER(".header"),
        BODY(".body"),
        FOOTER(".footer");

        fun path() = path
    }

    /**
     * Any occurrence of "**%prefix%**" gets replaced with the config value set
     *
     * The replacement can be retrieved with **`get("prefix")`**
     */
    fun prefixReplacer(): TextReplacementConfig = "%prefix%" replacedWith get("prefix")

}