package xyz.aeonxd.commonslib.timing

import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import xyz.aeonxd.commonslib.util.ComponentUtil.toComponent
import java.util.logging.Level.SEVERE
import kotlin.time.Duration
import kotlin.time.TimeSource.Monotonic.ValueTimeMark
import kotlin.time.TimeSource.Monotonic.markNow

/**
 * Utility for measuring elapsed time
 */
object TimeTracker {

    private data class TimeRecord(val label: String, val mark: ValueTimeMark)
    data class ElapsedTimeRecord(val position: Int, val label: String, val duration: Duration)
    data class CompleteElapsedTimeRecord(
        val elapsedTimeRecords: List<ElapsedTimeRecord>,
        val totalDuration: Duration
    )

    private infix fun String.at(mark: ValueTimeMark) = TimeRecord(this, mark)

    fun newSession(startDirectly: Boolean = false): TrackingSession = TrackingSession(startDirectly)

    class TrackingSession(startDirectly: Boolean) {
        private val timeRecords = mutableListOf<TimeRecord>()
        private var totalDuration = Duration.ZERO

        init {
            if (startDirectly) {
                mark("Started")
            }
        }

        fun mark(label: String) {
            val now = markNow()
            if (timeRecords.isNotEmpty()) {
                val previousMark = timeRecords.last().mark
                totalDuration += now - previousMark
            }
            timeRecords.add(label at now)
        }

        fun getRecords(): List<ElapsedTimeRecord> {
            var counter = 1
            return timeRecords.windowed(2) { (previous, current) ->
                ElapsedTimeRecord(counter++, current.label, current.mark - previous.mark)
            }
        }

        fun sendMessage(sender: CommandSender) {
            val records = getRecords()
            var elapsedDuration = Duration.ZERO

            records.forEach { record ->
                elapsedDuration += record.duration
                sender.sendMessage("<#ACE1AF>#${record.position} <white>${record.label} in <#ACE1AF>${record.duration}".toComponent())
            }

            sender.sendMessage("<#ACE1AF>=== <white>Finished in <#ACE1AF><bold>$elapsedDuration".toComponent())
        }

        fun logToConsole() {
            val records = getRecords()
            var elapsedDuration = Duration.ZERO

            val logger = Bukkit.getLogger()
            records.forEach { record ->
                elapsedDuration += record.duration
                logger.log(SEVERE, "#${record.position} ${record.label} in ${record.duration}")
            }

            logger.log(SEVERE, "=== Finished in $elapsedDuration")
        }
    }

}

/**
 * object Timing {
 *
 *     data class Record(val label: String, val mark: ValueTimeMark)
 *     private infix fun String.at(mark: ValueTimeMark) = Record(this, mark)
 *
 *     class Session(private val records: MutableList<Record>) {
 *
 *         fun mark(label: String) = records.add(label at markNow())
 *
 *         fun sendMessage(sender: CommandSender) {
 *             var i = 1
 *             records.windowed(2) { (previous, current) ->
 *                 sender.sendMessage("&#ace1af#${i++} &f${current.label} in &#ace1af${current.mark - previous.mark}")
 *             }
 *             sender.sendMessage("&#ace1af=== &fFinished in &#ace1af${records.last().mark - records.first().mark}")
 *         }
 *
 *     }
 *
 *     fun newSession(): Session = Session(mutableListOf())
 *
 * }
 */