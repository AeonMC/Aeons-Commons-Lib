package xyz.aeonxd.commonslib.commands

/**
 * Represents a (sub)command argument
 */
@Suppress("UNUSED")
class Argument private constructor(
    val name: String,
    val suggestions: () -> MutableList<String>,
    val fallbackSuggestions: () -> MutableList<String>,
    val fallbackMinInputLength: Int = 1,
    val isOptional: Boolean = false
) {

    class Builder {

        private lateinit var name: String
        private var suggestions: () -> MutableList<String> = { mutableListOf() }
        private var fallbackSuggestions: () -> MutableList<String> = { mutableListOf() }
        private var fallbackMinInputLength = 1
        private var isOptional = false

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
            fallbackSuggestions,
            fallbackMinInputLength,
            isOptional
        )

    }

}