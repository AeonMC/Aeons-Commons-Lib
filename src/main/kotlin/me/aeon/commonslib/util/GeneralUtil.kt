package me.aeon.commonslib.util

import me.clip.placeholderapi.PlaceholderAPIPlugin
import org.bukkit.util.StringUtil

object GeneralUtil {

    /**
     * Whether [this][String] equals to [PlaceholderAPIPlugin.booleanTrue] or "TRUE" (case insensitive)
     */
    @JvmStatic
    fun String.isTrue() = this.equals(PlaceholderAPIPlugin.booleanTrue(), true)
            || this.equals("TRUE", true)

    /**
     * Converts [this][String] into a [Boolean]
     * @see [isTrue]
     */
    @JvmStatic
    fun String?.asBoolean() = this?.isTrue() ?: false

    /**
     * Filters elements which start with [input]
     */
    @JvmStatic
    fun List<String>.filterInput(input: String): MutableList<String> {
        if (this.isEmpty()) return mutableListOf()
        if (this.size == 1 && this[0].startsWith(input)) return toMutableList()
        return StringUtil.copyPartialMatches(input, this, mutableListOf())
    }

    /**
     * Filters elements which contain [keyword]
     */
    @JvmStatic
    fun List<String>.filterContains(keyword: String): MutableList<String> {
        if (isEmpty()) return mutableListOf()
        return filter { it.contains(keyword) }.toMutableList()
    }

}