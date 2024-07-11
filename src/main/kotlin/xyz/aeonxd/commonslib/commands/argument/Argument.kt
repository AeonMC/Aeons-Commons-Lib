package xyz.aeonxd.commonslib.commands.argument

import org.bukkit.command.CommandSender

/**
 * Represents a (sub)command argument
 */
@Suppress("UNUSED")
class Argument private constructor(
    val name: String,
    val suggestions: () -> MutableList<String>,
    val suggestionCondition: (CommandSender, String) -> Boolean,
    val fallbackSuggestions: () -> MutableList<String>,
    val fallbackMinInputLength: Int = 1,
    val isOptional: Boolean = false
) {

    class Builder() {

        private lateinit var name: String
        private var suggestions: () -> MutableList<String> = { mutableListOf() }
        private var suggestionCondition: (CommandSender, String) -> Boolean = { _, _ -> true }
        private var fallbackSuggestions: () -> MutableList<String> = { mutableListOf() }
        private var fallbackMinInputLength = 1
        private var isOptional = false

        constructor(name: String) : this() { name(name) }

        /**
         * Argument suggestion name
         */
        fun name(name: String) = apply { this.name = name }

        /**
         * What should be suggested when one tries to tab-complete the subcommand
         */
        fun suggestions(suggestions: () -> MutableList<String>) =
            apply {
                this.suggestions = suggestions
            }

        /* Only returns the suggestions which pass the condition */
        fun suggestionCondition(condition: (CommandSender, String) -> Boolean) =
            apply {
                this.suggestionCondition = condition
            }

        /**
         * Suggestions when the main [suggestions] list is empty
         * @param minInputLength When the main suggestion list is empty,
         * how many characters should already be provided as an input to
         * look for fallback suggestions? Default value is 1
         */
        fun fallbackSuggestions(minInputLength: Int = 1, fallbackSuggestions: () -> MutableList<String>) =
            apply {
                fallbackMinInputLength(minInputLength)
                this.fallbackSuggestions = fallbackSuggestions
            }

        fun fallbackMinInputLength(length: Int) =
            apply { this.fallbackMinInputLength = length }

        fun optional(optional: Boolean) =
            apply { this.isOptional = optional }


        /**
         * Builds the [Argument]
         */
        fun build(): Argument = Argument(
            name,
            suggestions,
            suggestionCondition,
            fallbackSuggestions,
            fallbackMinInputLength,
            isOptional
        )

    }

}