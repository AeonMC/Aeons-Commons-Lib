package xyz.aeonxd.commonslib.scheduler

import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

@Suppress("UNUSED")
class DefaultTaskScheduler : TaskScheduler {

    private val scheduler = Bukkit.getScheduler()
    private lateinit var plugin: JavaPlugin

    override fun init(plugin: JavaPlugin) {
        this.plugin = plugin
    }


    override fun runTask(task: () -> Unit) {
        scheduler.runTask(plugin, task)
    }

    override fun runTaskLater(delay: Long, task: () -> Unit) {
        scheduler.runTaskLater(plugin, task, delay)
    }

    override fun runTaskTimer(delay: Long, period: Long, task: () -> Unit) {
        scheduler.runTaskTimer(plugin, task, delay, period)
    }


    override fun runTaskAsync(task: () -> Unit) {
        scheduler.runTaskAsynchronously(plugin, task)
    }

    override fun runTaskLaterAsync(delay: Long, task: () -> Unit) {
        scheduler.runTaskLaterAsynchronously(plugin, task, delay)
    }

    override fun runTaskTimerAsync(delay: Long, period: Long, task: () -> Unit) {
        scheduler.runTaskTimerAsynchronously(plugin, task, delay, period)
    }

}