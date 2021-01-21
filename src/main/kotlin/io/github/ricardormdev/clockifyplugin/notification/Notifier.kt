package io.github.ricardormdev.clockifyplugin.notification

import com.intellij.notification.NotificationDisplayType
import com.intellij.notification.NotificationGroup
import com.intellij.notification.NotificationType
import io.github.ricardormdev.clockifyplugin.PluginLoader

class Notifier {

    companion object {
        private val NOTIFICATION_GROUP = NotificationGroup("Clockify Notification Group",
            NotificationDisplayType.BALLOON, true)

        fun notifyWarning(content: String) {
            PluginLoader.plugin.containers.forEach {
                NOTIFICATION_GROUP.createNotification(content, NotificationType.WARNING).notify(it.key)
            }
        }

        fun notifyError(content: String) {
            PluginLoader.plugin.containers.forEach {
                NOTIFICATION_GROUP.createNotification(content, NotificationType.ERROR).notify(it.key)
            }
        }

        fun notifyInfo(content: String) {
            PluginLoader.plugin.containers.forEach {
                NOTIFICATION_GROUP.createNotification(content, NotificationType.INFORMATION).notify(it.key)
            }
        }

    }
}