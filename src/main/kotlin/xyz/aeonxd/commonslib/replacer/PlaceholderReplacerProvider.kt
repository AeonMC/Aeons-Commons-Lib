package xyz.aeonxd.commonslib.replacer

import kotlin.enums.EnumEntries

abstract class PlaceholderReplacerProvider<E>(
    private val entries: EnumEntries<E>
) where E : Enum<E>,
        E : PlaceholderProvider {

    abstract val mapBuilderAction: MutableMap<E, String>.() -> Unit

    fun getReplacers(): Map<E, String> = buildMap {
        this.mapBuilderAction()

        require(this.size == entries.size) {
            val enumClassName = this@PlaceholderReplacerProvider.entries.first()::class.simpleName
            val className = this@PlaceholderReplacerProvider::class.simpleName
            "Not every Enum of type $enumClassName has been added into the map in $className"
        }
    }

}