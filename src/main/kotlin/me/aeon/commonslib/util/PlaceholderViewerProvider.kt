package me.aeon.commonslib.util

import me.clip.placeholderapi.PlaceholderAPI
import org.bukkit.OfflinePlayer

interface PlaceholderViewerProvider {

    val viewer: OfflinePlayer?

    fun String.setPAPIPlaceholders(): String = setPAPIPlaceholders(viewer)

    companion object {
        @JvmStatic
        fun String.setPAPIPlaceholders(viewer: OfflinePlayer?): String =
            PlaceholderAPI.setPlaceholders(viewer, this)
    }

}