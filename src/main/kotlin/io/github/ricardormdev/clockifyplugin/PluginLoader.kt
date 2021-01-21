package io.github.ricardormdev.clockifyplugin

import com.intellij.openapi.application.PreloadingActivity
import com.intellij.openapi.progress.ProgressIndicator

class PluginLoader : PreloadingActivity() {

    companion object {
        // Our plugin instance
        val plugin = Plugin()
    }

    override fun preload(indicator: ProgressIndicator) {
        if(!indicator.isCanceled) {
            plugin.doLoad()
        }
    }

}