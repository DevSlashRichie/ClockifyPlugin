package io.github.ricardormdev.clockifyplugin

import org.junit.Test

class RepositoryTest {

    @Test
    fun testLogin() {



    }

    @Test
    fun testWorking() {
        val plugin = Plugin()
        val timer = PluginTimer(plugin)

        timer.startTimer()
        println(timer.getTiming())
        assert(timer.getTiming() > 0)
    }

}