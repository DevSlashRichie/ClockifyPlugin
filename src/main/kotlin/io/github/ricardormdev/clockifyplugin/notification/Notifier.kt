package io.github.ricardormdev.clockifyplugin.notification

import com.intellij.notification.NotificationDisplayType
import com.intellij.notification.NotificationGroup
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.options.ShowSettingsUtil
import com.intellij.openapi.util.IconLoader
import io.github.ricardormdev.clockifyplugin.PluginLoader
import io.github.ricardormdev.clockifyplugin.settings.AppSettingsConfigurable

class Notifier {

    companion object {
        private val NOTIFICATION_GROUP = NotificationGroup("Clockify Notification Group",
            NotificationDisplayType.BALLOON, true)

        fun notifyWarning(content: String) {
            PluginLoader.plugin.containers.forEach {
                NOTIFICATION_GROUP.createNotification("Clockify Status", content, NotificationType.WARNING).notify(it.key)
            }
        }

        fun notifyError(content: String, showSettings: Boolean = true) {
            PluginLoader.plugin.containers.forEach {
                val not = NOTIFICATION_GROUP.createNotification("Clockify Status", content, NotificationType.ERROR)
                if(showSettings)
                    not.addAction(OpenSettings())
                not.notify(it.key)
            }
        }

        fun notifyInfo(content: String) {
            val icon = IconLoader.getIcon("/icons/clockify-icon.svg")
            val not = NOTIFICATION_GROUP.createNotification("Clockify Status", content, NotificationType.INFORMATION)
            not.icon = icon
            PluginLoader.plugin.containers.forEach {
                not.notify(it.key)
            }
        }

    }

    class OpenSettings : AnAction("Open Clockify Settings") {
        override fun actionPerformed(e: AnActionEvent) {
            ShowSettingsUtil.getInstance().showSettingsDialog(e.project, AppSettingsConfigurable::class.java)
        }
    }
}