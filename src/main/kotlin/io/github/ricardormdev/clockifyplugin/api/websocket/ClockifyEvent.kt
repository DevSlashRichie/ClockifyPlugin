package io.github.ricardormdev.clockifyplugin.api.websocket

enum class ClockifyEvent {

    TIME_ENTRY_STARTED,
    TIME_ENTRY_STOPPED,
    TIME_ENTRY_DELETED,
    TIME_ENTRY_UPDATED,
    TIME_ENTRY_CREATED,
    NEW_NOTIFICATIONS,
    TIME_TRACKING_SETTINGS_UPDATED,
    WORKSPACE_SETTINGS_UPDATED,
    CHANGED_ADMIN_PERMISSION,
    UPDATE_DASHBOARD_TIMERS,
    ACTIVE_WORKSPACE_CHANGED;

    companion object {
        fun get(by: String) : ClockifyEvent? {
            return values().firstOrNull {
                by.equals(it.name, ignoreCase = true)
            }
        }
    }

}