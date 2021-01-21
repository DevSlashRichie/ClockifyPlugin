package io.github.ricardormdev.clockifyplugin.api.models

data class UserInterface(var id: String, var name: String, var activeWorkspace: String)

data class Workspace(var id: String, var name: String)

data class Project(var id: String, var name: String)

data class Tag(var id: String, var name: String)

data class Task(var id: String, var name: String)

interface Request

data class TimeEntry(var id: String, val billable: Boolean, val description: String, val projectId: String, var timeInterval: TimeInterval?) : Request

data class TimeInterval(var start: String?, var end: String?, var duration: String?)

data class EndRequest(val end: String) : Request
