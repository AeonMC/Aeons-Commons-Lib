package xyz.aeonxd.commonslib.scheduler

import org.bukkit.plugin.java.JavaPlugin

@Suppress("UNUSED")
interface TaskScheduler {

    fun init(plugin: JavaPlugin)

    /* Synchronous */
    fun runTask(task: () -> Unit)
    fun runTaskLater(delay: Long, task: () -> Unit)
    fun runTaskTimer(delay: Long, period: Long, task: () -> Unit)

    /* Asynchronous */
    fun runTaskAsync(task: () -> Unit)
    fun runTaskLaterAsync(delay: Long, task: () -> Unit)
    fun runTaskTimerAsync(delay: Long, period: Long, task: () -> Unit)

}