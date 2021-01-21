package io.github.ricardormdev.clockifyplugin.widget

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.StatusBar
import com.intellij.openapi.wm.StatusBarWidget
import io.github.ricardormdev.clockifyplugin.PluginLoader

class WidgetItself(private val project: Project) : StatusBarWidget {

    private val id = "io.github.ricardormdev.clockifyplugin.WidgetStatusBar"

    override fun dispose() {
        PluginLoader.plugin.containers.remove(project)
    }

    override fun ID(): String {
        return id
    }

    override fun getPresentation(): StatusBarWidget.WidgetPresentation {
        return WidgetPresentation(project)
    }

    override fun install(statusBar: StatusBar) {
        PluginLoader.plugin.containers[project] = id
    }

}