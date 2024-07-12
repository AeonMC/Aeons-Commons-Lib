package xyz.aeonxd.commonslib.commands.argument

import org.bukkit.command.CommandSender

/**
 * Represents a (sub)command argument
 */
@Suppress("UNUSED")
class Argument<T> private constructor(
    val name: String,
    val suggestions: () -> MutableList<T>,
    val suggestionCondition: (CommandSender, T) -> Boolean,
    val fallbackSuggestions: () -> MutableList<T>,
    val fallbackMinInputLength: Int,
    val isOptional: Boolean,
    val mapToString: (T) -> String
) {

    open class Builder<T>() {

        class Str() : Builder<String>() {
            constructor(name: String) : this() { name(name) }
        }

        private lateinit var name: String
        private var suggestions: () -> MutableList<T> = { mutableListOf() }
        private var suggestionCondition: (CommandSender, T) -> Boolean = { _, _ -> true }
        private var fallbackSuggestions: () -> MutableList<T> = { mutableListOf() }
        private var fallbackMinInputLength = 1
        private var isOptional = false
        private var mapToString: (T) -> String = { it.toString() }

        constructor(name: String) : this() {
            name(name)
        }

        fun name(name: String) = apply { this.name = name }

        fun suggestions(suggestions: () -> MutableList<T>) = apply {
            this.suggestions = suggestions
        }

        fun suggestionCondition(condition: (CommandSender, T) -> Boolean) = apply {
            this.suggestionCondition = condition
        }

        fun fallbackSuggestions(minInputLength: Int = 1, suggestions: () -> MutableList<T>) = apply {
            fallbackMinInputLength(minInputLength)
            this.fallbackSuggestions = suggestions
        }

        fun fallbackMinInputLength(length: Int) = apply {
            this.fallbackMinInputLength = length
        }

        fun optional(optional: Boolean) = apply {
            this.isOptional = optional
        }

        fun stringMapper(mapper: (T) -> String) = apply {
            this.mapToString = mapper
        }

        fun build(): Argument<T> = Argument(
            name,
            suggestions,
            suggestionCondition,
            fallbackSuggestions,
            fallbackMinInputLength,
            isOptional,
            mapToString
        )
    }
}