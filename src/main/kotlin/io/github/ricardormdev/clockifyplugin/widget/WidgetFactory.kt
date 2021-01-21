package io.github.ricardormdev.clockifyplugin.widget

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.StatusBar
import com.intellij.openapi.wm.StatusBarWidget
import com.intellij.openapi.wm.StatusBarWidgetFactory

/**
 * This will create the widget (the factory itself) AND load all the data from the
 * controller [Plugin] to the widget of that project.
 */
class WidgetFactory : StatusBarWidgetFactory {

    override fun getId(): String = "io.github.ricardormdev.carlitosideaplugin"

    override fun getDisplayName(): String = "Carlitos Plugin"

    override fun isAvailable(project: Project): Boolean = true

    override fun disposeWidget(widget: StatusBarWidget) { }

    override fun canBeEnabledOn(statusBar: StatusBar): Boolean = true

    // CREATE A NEW WIDGET FOR EACH PROJECT.
    override fun createWidget(project: Project): StatusBarWidget {
        return WidgetItself(project)
    }
}