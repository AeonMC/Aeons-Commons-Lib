package xyz.aeonxd.commonslib.config.creator

import dev.dejvokep.boostedyaml.YamlDocument
import dev.dejvokep.boostedyaml.dvs.Pattern
import dev.dejvokep.boostedyaml.dvs.segment.Segment
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.io.IOException

@Suppress("UNUSED")
abstract class ConfigurationCreator {

    private val major = Segment.range(0, Int.MAX_VALUE)
    private val minor = Segment.range(0, 10)
    private val patch = Segment.range(0, 100)
    private val separator = Segment.literal(".")
    protected val pattern = Pattern(major, separator, minor, separator, patch)

    abstract val fileName: String
    abstract val plugin: JavaPlugin

    open fun generalSettings(): GeneralSettings = GeneralSettings.builder()
        .setKeyFormat(GeneralSettings.KeyFormat.STRING)
        .setUseDefaults(false)
        .build()

    open fun loaderSettings(): LoaderSettings = LoaderSettings.builder()
        .setAutoUpdate(true)
        .build()

    open fun dumperSettings(): DumperSettings = DumperSettings.DEFAULT

    open fun updaterSettings(): UpdaterSettings = UpdaterSettings.builder()
        .setVersioning(pattern, "config-version")
        .build()

    fun createConfiguration(): YamlDocument {
        val defaults = requireNotNull(plugin.getResource(fileName))

        return try {
            YamlDocument.create(
                File(plugin.dataFolder, fileName),
                defaults, generalSettings(), loaderSettings(), dumperSettings(), updaterSettings())
        } catch (e: IOException) {
            try {
                YamlDocument.create(defaults)
            } catch (ex: IOException) {
                throw RuntimeException(ex)
            }
        }
    }

}