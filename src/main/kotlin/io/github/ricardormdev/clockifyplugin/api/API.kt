package io.github.ricardormdev.clockifyplugin.api

import io.github.ricardormdev.clockifyplugin.api.models.*
import io.github.ricardormdev.clockifyplugin.api.websocket.authentication.AuthenticateTokens
import kong.unirest.Unirest
import java.time.Instant
import java.util.*

private val url: (String) -> String = { "https://global.api.clockify.me/$it" }

class API(auth: AuthenticateTokens) {
    private val token = auth.retrieveToken()

    private fun <T> request(endPoint: String, type: Class<out T>, fail: T) : T {
        if(token.isEmpty())
            return fail

        val url = url(endPoint)

        return Unirest.get(url)
            .header("Accept", "*/*")
            .header("X-Auth-Token", token)
            .asObject(type).body ?: fail
    }

    fun getUser(userId: String) : UserInterface {
        return request("users/$userId", UserInterface::class.java, UserInterface("", "", ""))
    }

    fun getWorkspaces() : Array<Workspace> {
        return request("workspaces", Array<Workspace>::class.java, arrayOf())
    }

    fun getProjects(workspaceId: String) : Array<Project> {
        return request("workspaces/$workspaceId/projects", Array<Project>::class.java, arrayOf())
    }

    fun getTags(workspaceId: String) : Array<Tag> {
        return request("workspaces/$workspaceId/tags", Array<Tag>::class.java, arrayOf())
    }

    fun getEntryInProgress(workspaceId: String, userId: String) : TimeEntry? {
        val entries = request("/v1/workspaces/${workspaceId}/user/${userId}/time-entries?in-progress=true&hydrated=true",
            Array<TimeEntry>::class.java, arrayOf())

        if (entries.isNotEmpty()) {
            return entries[0]
        }
        return null
    }

    fun setDefaultWorkspace(workspaceId: String, userId: String) {

        val url = url("users/$userId/defaultWorkspace/$workspaceId")

        Unirest.post(url)
            .header("Content-Type", "application/json")
            .header("X-Auth-Token", token)
            .accept("application/json")
            .asEmpty()

    }

    fun startWorking(workspaceId: String, projectId: String, billable: Boolean, description : String) {
        val url = url("workspaces/$workspaceId/timeEntries/full")
        val currentTime = Instant.now().toString()

        Unirest.post(url)
            .header("Content-Type", "application/json")
            .header("X-Auth-Token", token)
            .accept("application/json")
            .body("""
                {
                    "projectId": "$projectId",
                    "description": "$description",
                    "billable": "$billable",
                    "start": "$currentTime"
                }
            """.trimIndent())
            .asEmpty()
    }

    fun endWorking(workspaceID: String) {
        val url = url("workspaces/${workspaceID}/timeEntries/endStarted")

        val date = Instant.now().toString()

        Unirest.put(url)
            .header("Content-Type", "application/json")
            .accept("application/json")
            .header("X-Auth-Token", token)
            .body(EndRequest(date))
            .asEmpty()
    }

}
