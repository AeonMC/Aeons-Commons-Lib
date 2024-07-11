package xyz.aeonxd.commonslib.update

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import xyz.aeonxd.commonslib.scheduler.TaskScheduler
import java.io.IOException
import java.net.URL
import kotlin.time.TimeSource.Monotonic.markNow

@Suppress("UNUSED")
abstract class UpdateChecker : Listener {

    abstract val scheduler: TaskScheduler
    abstract val url: String
    abstract val currentVersion: String
    lateinit var newVersion: String
        private set
    abstract val checkInterval: Long
    var hasUpdateAvailable: Boolean = false

    /**
     * Registers the listener that executes [informUpdateOnJoin]
     */
    abstract fun registerInformer()

    /**
     * Gets called directly after an update is found
     */
    abstract fun onUpdateAvailable()

    /**
     * Gets called directly if there was something wrong with checking for an update
     */
    abstract fun onException(exception: IOException)

    /**
     * Gets called every time a player joins
     */
    @EventHandler
    abstract fun informUpdateOnJoin(event: PlayerJoinEvent)

    /**
     * Starts the task to check for updates every [checkInterval] ticks
     */
    fun start() {
        scheduler.runTaskTimerAsync(0L, checkInterval * 20L, ::checkForUpdates)
    }

    /**
     * The main task for update checking
     */
    open fun checkForUpdates() {
        newVersion = try {
            URL(url).openStream().bufferedReader().use { reader ->
                reader.readLine()
            }
        } catch (ex: IOException) {
            return onException(ex)
        }

        if (currentVersion != newVersion) {
            scheduler.runTask {
                hasUpdateAvailable = true
                registerInformer()
                onUpdateAvailable()
            }
        }
    }

    open fun checkForUpdates(
        onUpdateFound: (UpdateCheckResult) -> Unit,
        onNoUpdateFound: () -> Unit
    ) {
        val start = markNow()

        val newVersion = try {
            URL(url).openStream().bufferedReader().use { reader ->
                reader.readLine()
            }
        } catch (ex: IOException) {
            return onException(ex)
        }

        if (currentVersion == newVersion) {
            scheduler.runTask {
                onNoUpdateFound()
            }
            return
        }

        scheduler.runTask {
            onUpdateFound(UpdateCheckResult(currentVersion, newVersion, start.elapsedNow()))
        }
    }

}