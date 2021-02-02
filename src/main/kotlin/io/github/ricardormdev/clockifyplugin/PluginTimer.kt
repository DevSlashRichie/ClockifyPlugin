package io.github.ricardormdev.clockifyplugin

import java.util.*

class PluginTimer(private val plugin: Plugin) {

    private lateinit var timer: Timer
    private var seconds = 0

    /**
     * Start the counter of the plugin.
     * @param startSeconds Set an already counting seconds. Default 0
     */
    fun startTimer(startSeconds: Int = 0) {
        seconds = startSeconds
        timer = Timer()
        val task = object : TimerTask() {
            override fun run() {
                plugin.updateContainers()
                seconds++
            }
        }
        timer.schedule(task, 0, 1000L)
    }

    /**
     * Stop counting secconds, this method is just local.
     */
    fun stopTimer() {
        timer.cancel()
        plugin.updateContainers()
    }

    /**
     * Get how many seconds had been passed.
     * @return The seconds as an Int
     */
    fun getTiming() : Int {
        return seconds
    }

}