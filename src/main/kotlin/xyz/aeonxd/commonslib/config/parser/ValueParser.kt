package xyz.aeonxd.commonslib.config.parser

import dev.dejvokep.boostedyaml.block.implementation.Section
import xyz.aeonxd.commonslib.replacer.PlaceholderReplacer
import xyz.aeonxd.commonslib.replacer.PlaceholderReplacer.Companion.setPlaceholders
import xyz.aeonxd.commonslib.util.GeneralUtil.isTrue
import xyz.aeonxd.commonslib.util.PlaceholderViewerProvider

@Suppress("UNUSED")
interface ValueParser : PlaceholderViewerProvider {

    val section: Section?
    val placeholderReplacers: List<PlaceholderReplacer<*>>
        get() = emptyList()

    fun parse(route: String): String? = parse(section, route)
    fun <T : Number> parseNumber(route: String, mapper: NumberMapper<T>): T? = parseNumber(section, route, mapper)
    fun <T : Number> parseNumber(route: String, def: T, mapper: NumberMapper<T>): T = parseNumber(section, route, def, mapper)
    fun parseInt(route: String): Int? = parseInt(section, route)
    fun parseInt(route: String, def: Int): Int = parseInt(section, route, def)
    fun parseLong(route: String): Long? = parseLong(section, route)
    fun parseLong(route: String, def: Long): Long? = parseLong(section, route, def)
    fun parseList(route: String): List<String> = parseList(section, route)
    fun <E : Enum<E>> parseEnum(route: String, clazz: Class<E>): E? = parseEnum(section, route, clazz)
    fun <E : Enum<E>> parseEnum(route: String, clazz: Class<E>, def: E): E = parseEnum(section, route, clazz, def)
    fun parseBoolean(route: String): Boolean = parseBoolean(section, route)
    fun parsePAPIBeforeLocal(): Boolean = parsePAPIBeforeLocal(section)

    fun parse(section: Section?, route: String): String? =
        section?.getString(route)
            ?.applyPlaceholders(section)

    fun <T : Number> parseNumber(section: Section?, route: String, mapper: NumberMapper<T>): T? {
        return parse(section, route)?.let { mapper.map(it) }
    }

    fun <T : Number> parseNumber(section: Section?, route: String, def: T, mapper: NumberMapper<T>): T {
        return parseNumber(section, route, mapper) ?: def
    }

    fun parseInt(section: Section?, route: String): Int? = parseNumber(section, route, NumberMapper.Int)
    fun parseInt(section: Section?, route: String, def: Int): Int = parseNumber(section, route, NumberMapper.Int) ?: def
    fun parseLong(section: Section?, route: String): Long? = parseNumber(section, route, NumberMapper.Long)
    fun parseLong(section: Section?, route: String, def: Long): Long = parseNumber(section, route, NumberMapper.Long) ?: def


    fun parseList(section: Section?, route: String): List<String> =
        section?.getStringList(route)
            ?.map { it.applyPlaceholders(section) }
            ?: emptyList()

    fun <E : Enum<E>> parseEnum(section: Section?, route: String, clazz: Class<E>): E? {
        val enumString = parse(section, route)
            ?: return null

        return clazz.enumConstants.firstOrNull {
            it.name.equals(enumString, true)
        }
    }

    fun <E : Enum<E>> parseEnum(section: Section?, route: String, clazz: Class<E>, def: E): E {
        return parseEnum(section, route, clazz) ?: def
    }


    fun parseBoolean(section: Section?, route: String) =
        parse(section, route)
            ?.isTrue()
            ?: false

    fun parsePAPIBeforeLocal(section: Section?): Boolean =
        parseBoolean(section, "parse_papi_before_local")


    fun String.applyPlaceholders(section: Section?): String =
        this.let { str ->
            if (parsePAPIBeforeLocal(section)) {
                this.setPAPIPlaceholders().let { placeholderReplacers.setPlaceholders(it) }
            } else {
                placeholderReplacers.setPlaceholders(str).setPAPIPlaceholders()
            }
        }

}