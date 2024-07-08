package xyz.aeonxd.commonslib.replacer

class PlaceholderReplacer<E>(
    private val placeholderReplacerProvider: PlaceholderReplacerProvider<E>
) where E : Enum<E>,
        E : PlaceholderProvider {

    fun setPlaceholders(input: String): String {
        return placeholderReplacerProvider.getReplacers()
            .entries.fold(input) { result, (placeholderProvider, replacement) ->
                result.replace(placeholderProvider.placeholder, replacement)
            }
    }

    companion object {
        @JvmStatic
        fun List<PlaceholderReplacer<*>>.setPlaceholders(input: String): String {
            return this.fold(input) { result, replacer ->
                replacer.setPlaceholders(result)
            }
        }
    }

}