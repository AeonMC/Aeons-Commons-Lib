package xyz.aeonxd.commonslib.update

import java.lang.IllegalStateException

data class PluginVersion(
    val major: Int,
    val minor: Int,
    val patch: Int
) : Comparable<PluginVersion> {

    override fun compareTo(other: PluginVersion): Int {
        if (this.major != other.major) {
            return this.major.compareTo(other.major)
        }

        if (this.minor != other.minor) {
            return this.minor.compareTo(other.minor)
        }

        return this.patch.compareTo(other.patch)
    }


    companion object {

        @JvmStatic
        fun ofNullable(version: String): PluginVersion? {
            val parts = version.split('.', limit = 3)
                .mapNotNull { it.toIntOrNull() }
            if (parts.size != 3) return null

            return PluginVersion(parts[0], parts[1], parts[2])
        }

        @JvmStatic
        fun of(version: String): PluginVersion = ofNullable(version)
            ?: throw IllegalStateException("Could not construct a PluginVersion from \"$version\"")

    }

}
