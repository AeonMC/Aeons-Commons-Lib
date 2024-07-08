package xyz.aeonxd.commonslib.config.parser

import dev.dejvokep.boostedyaml.block.implementation.Section
import xyz.aeonxd.commonslib.replacer.PlaceholderReplacer
import xyz.aeonxd.commonslib.replacer.PlaceholderReplacer.Companion.setPlaceholders
import xyz.aeonxd.commonslib.util.GeneralUtil.isTrue
import xyz.aeonxd.commonslib.util.PlaceholderViewerProvider

interface ValueParser : PlaceholderViewerProvider {

    val section: Section?
    val placeholderReplacers: List<PlaceholderReplacer<*>>
        get() = emptyList()

    fun parse(route: String): String? = parse(section, route)
    fun <T : Number> parseNumber(route: String, mapper: NumberMapper<T>): T? = parseNumber(section, route, mapper)
    fun <T : Number> parseNumber(route: String, def: T, mapper: NumberMapper<T>): T? = parseNumber(section, route, def, mapper)
    fun parseList(route: String): List<String> = parseList(section, route)
    fun <E : Enum<E>> parseEnum(route: String, clazz: Class<E>): E? = parseEnum(section, route, clazz)
    fun <E : Enum<E>> parseEnum(route: String, clazz: Class<E>, def: E): E = parseEnum(section, route, clazz, def)
    fun isOptionEnabled(route: String): Boolean = isOptionEnabled(section, route)
    fun parsePAPIBeforeLocal(): Boolean = parsePAPIBeforeLocal(section)

    fun parse(section: Section?, route: String): String? =
        section?.getString(route)
            ?.applyPlaceholders(section)

    fun <T : Number> parseNumber(section: Section?, route: String, mapper: NumberMapper<T>): T? {
        return parse(section, route)?.let { mapper.mapper(it) }
    }

    fun <T : Number> parseNumber(section: Section?, route: String, def: T, mapper: NumberMapper<T>): T {
        return parseNumber(section, route, mapper) ?: def
    }

    fun parseList(section: Section?, route: String): List<String> =
        section?.getStringList(route)
            ?.map { it.applyPlaceholders(section) }
            ?: emptyList()

    fun <E : Enum<E>> parseEnum(section: Section?, route: String, clazz: Class<E>): E? {
        val enumString = parse(section, route)
            ?: return null

        return clazz.enumConstants.find {
            it.name.equals(enumString, true)
        }
    }

    fun <E : Enum<E>> parseEnum(section: Section?, route: String, clazz: Class<E>, def: E): E {
        return parseEnum(section, route, clazz) ?: def
    }


    fun isOptionEnabled(section: Section?, route: String) =
        parse(section, route)
            ?.isTrue()
            ?: false

    fun parsePAPIBeforeLocal(section: Section?): Boolean =
        isOptionEnabled(section, "parse_papi_before_local")


    fun String.applyPlaceholders(section: Section?): String =
        this.let { str ->
            if (parsePAPIBeforeLocal(section)) {
                this.setPAPIPlaceholders().let { placeholderReplacers.setPlaceholders(it) }
            } else {
                placeholderReplacers.setPlaceholders(str).setPAPIPlaceholders()
            }
        }

}