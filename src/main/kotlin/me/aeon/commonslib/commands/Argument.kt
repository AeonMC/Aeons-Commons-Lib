package me.aeon.commonslib.commands

/**
 * Describes a [Subcommand] argument
 */
@Suppress("unused")
class Argument private constructor(
    // e.g. "<target>"
    val name: String,
    // First after the subcommand e.g. "/cfa set <target> <fontId>", would be 0
    // Suggestions when autocompleting the subcommand, e.g. a list of online player names
    val suggestions: MutableList<String>,
    // When the suggestion list above is empty, retrieve from this one
    val fallbackSugestions: MutableList<String>?,
    val fallbackMinInputLength: Int = 1,
    val isOptional: Boolean = false
) {

    class Builder {

        private lateinit var name: String
        private lateinit var suggestions: MutableList<String>
        private var fallbackSuggestions: MutableList<String>? = null
        private var fallbackMinInputLength = 1
        private var isOptional = false

        /**
         * Argument name, e.g. when one executes "/cf"
         */
        fun name(name: String) = apply { this.name = name }

        /**
         * What should be suggested when one tries to tab-complete the subcommand
         */
        fun suggestions(suggestions: List<String>) =
            apply {
                if (suggestions is MutableList) this.suggestions = suggestions
                else this.suggestions = suggestions.toMutableList()
            }

        /**
         * Suggestions when the main [suggestions] list is empty
         * @param minInputLength When the main suggestion list is empty,
         * how many characters should already be provided as an input to
         * look for fallback suggestions? Default value is 1
         */
        fun fallbackSuggestions(fallbackSuggestions: List<String>?, minInputLength: Int = 1) =
            apply {
                this.fallbackMinInputLength = minInputLength

                if (fallbackSuggestions == null) {
                    this.fallbackSuggestions = null
                    return@apply
                }

                this.fallbackSuggestions =
                    if (fallbackSuggestions is MutableList) fallbackSuggestions
                    else fallbackSuggestions.toMutableList()
            }

        fun fallbackMinInputLength(length: Int) =
            apply { this.fallbackMinInputLength = length }

        fun optional(optional: Boolean) =
            apply { this.isOptional = optional }


        /**
         * Builds the [Argument]
         */
        fun build(): Argument = Argument(name, suggestions, fallbackSuggestions, fallbackMinInputLength, isOptional)

    }

}