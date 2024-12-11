package plugin.artofluxis.project.util.other

import plugin.artofluxis.project.plugin
import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitTask
import kotlin.time.Duration

/**
 * Function to run an asynchronous loop.
 * @param period The interval at which the code is executed
 * @param delay Delay before starting in ticks
 * @param action Code to be executed in each iteration of the loop
 */
fun runTaskTimer(period: Duration, delay: Int = 0, action: (BukkitTask) -> Unit) {
    Bukkit.getScheduler().runTaskTimer(plugin, { task -> action(task) }, delay.toLong(), period.toTicks)
}

// Extension to convert a duration (Duration) into the number of game ticks.
// One Minecraft tick equals 50 milliseconds.
val Duration.toTicks: Long
    get() = this.inWholeMilliseconds / 50

/**
 * Function to run a delayed task after the specified number of ticks.
 * @param delay Delay before execution in ticks
 * @param action Code to be executed after the delay
 */
fun runTaskLater(delay: Int, action: BukkitTask.() -> Unit) {
    Bukkit.getScheduler().runTaskLater(plugin, { task -> task.action() }, delay.toLong())
}

/**
 * Function to run a task asynchronously to avoid blocking the server's main thread.
 * @param action Code to be executed asynchronously
 */
fun runTaskAsync(action: BukkitTask.() -> Unit) {
    Bukkit.getScheduler().runTaskAsynchronously(plugin, action)
}