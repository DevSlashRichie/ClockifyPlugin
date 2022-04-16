package io.github.ricardormdev.clockifyplugin.widget

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.StatusBarWidget
import com.intellij.util.Consumer
import io.github.ricardormdev.clockifyplugin.PluginLoader
import io.github.ricardormdev.clockifyplugin.dialogs.StartWorkingDialog
import io.github.ricardormdev.clockifyplugin.dialogs.StopWorkingDialog
import io.github.ricardormdev.clockifyplugin.notification.Notifier
import org.jetbrains.annotations.Nls
import java.awt.event.MouseEvent

class WidgetPresentation(private val project: Project) : StatusBarWidget.TextPresentation {

    @Nls
    override fun getTooltipText(): String {
        return "Clockify Timer"
    }

    override fun getClickConsumer(): Consumer<MouseEvent> = Consumer {
        if (!PluginLoader.plugin.logged) {
            Notifier.notifyWarning("You aren't logged in!")
            return@Consumer
        }

        val handle = PluginLoader.plugin

        if (handle.working) {
            val endDialog = StopWorkingDialog()

            if (endDialog.showAndGet()) {
                handle.stopWorking()
            }

        } else {
            val startDialog = StartWorkingDialog(project)

            if (startDialog.showAndGet()) {
                val workspaceId: String =
                    handle.dataController.getWorkspaceByName(startDialog.workspace.selectedItem!!.toString()).id

                val pickedProject = startDialog.projectName;

                val projectId =
                    if (pickedProject.isEnabled)
                        handle.dataController.getProjectByName(pickedProject.toString()).id
                    else
                        null

                val billable = startDialog.billable.isSelected
                val description = startDialog.description.text

                handle.startWorking(workspaceId, projectId, billable, description)
            }
        }


    }

    override fun getText(): String {
        return PluginLoader.plugin.getStatusMessage()
    }

    override fun getAlignment(): Float {
        return 0F
    }

}