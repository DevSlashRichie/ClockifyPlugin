package io.github.ricardormdev.clockifyplugin

import io.github.ricardormdev.clockifyplugin.api.API
import io.github.ricardormdev.clockifyplugin.api.models.Project
import io.github.ricardormdev.clockifyplugin.api.models.Tag
import io.github.ricardormdev.clockifyplugin.api.models.UserInterface
import io.github.ricardormdev.clockifyplugin.api.models.Workspace
import io.github.ricardormdev.clockifyplugin.api.websocket.authentication.AuthenticateTokens
import io.github.ricardormdev.clockifyplugin.settings.settingsState

class PluginDataController(private val api: API) {

    lateinit var user: UserInterface
    lateinit var workspaces: Array<Workspace>
    lateinit var projects: Array<Project>
    lateinit var tags: Array<Tag>

    fun prepareUser(authenticator: AuthenticateTokens) : Boolean {
        settingsState.userID.run {
            if(value.isBlank()) {
                val newValue = authenticator.retrieveFullUser()?.id
                if(newValue == null) {
                    "Something went wrong".print()
                    return false
                } else value = newValue
            }
            user = api.getUser(value)
            return true
        }
    }

    fun updateUser() {
        user = api.getUser(user.id)
    }

    fun fetchData() {
        workspaces = api.getWorkspaces()
        projects = api.getProjects(user.activeWorkspace)
        tags = api.getTags(user.activeWorkspace)
    }

    fun fetchSpecific(workspaceID: String) {
        workspaces = api.getWorkspaces()

        workspaces.filter { it.name == workspaceID }.first {
            user.activeWorkspace = it.id
            api.setDefaultWorkspace(it.id, user.id)
            true
        }

        val activeWorkspace = user.activeWorkspace
        projects = api.getProjects(activeWorkspace)
        tags = api.getTags(activeWorkspace)
    }

    fun getWorkspaceByID(id: String) = workspaces.firstOrNull { it.id == id }

    fun getWorkspaceByName(name: String) = workspaces.first { it.name == name }

    fun getProjectByName(name: String) = projects.first { it.name == name }

    fun getTagsByName(name: String) = tags.first { it.name == name }

}