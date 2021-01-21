package io.github.ricardormdev.clockifyplugin

import java.util.*

class PluginTimer(private val plugin: Plugin) {

    private lateinit var timer: Timer
    private var seconds = 0

    fun startTimer() {
        seconds = 0
        timer = Timer()
        val task = object : TimerTask() {
            override fun run() {
                plugin.updateContainers()
                seconds++
            }
        }
        timer.schedule(task, 0, 1000L)
    }

    fun stopTimer() {
        timer.cancel()
        plugin.updateContainers()
    }

    fun getTiming() : Int {
        return seconds
    }

}