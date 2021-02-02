package io.github.ricardormdev.clockifyplugin

import io.github.ricardormdev.clockifyplugin.api.API
import io.github.ricardormdev.clockifyplugin.api.models.Project
import io.github.ricardormdev.clockifyplugin.api.models.Tag
import io.github.ricardormdev.clockifyplugin.api.models.UserInterface
import io.github.ricardormdev.clockifyplugin.api.models.Workspace
import io.github.ricardormdev.clockifyplugin.api.websocket.authentication.AuthenticateTokens
import io.github.ricardormdev.clockifyplugin.api.websocket.authentication.UserLogin
import io.github.ricardormdev.clockifyplugin.settings.ApplicationSettingsState
import io.github.ricardormdev.clockifyplugin.settings.settingsState
import io.mockk.*
import org.junit.Before
import org.junit.Test
import kotlin.test.assertTrue

class RepositoryTest {

    @Test
    fun testWorking() {
        val plugin = buildPlugin()

        assertTrue(plugin.getStatusMessage().contains("You are not working."), "The message should mention is not working")
        plugin.startWorking("workspaceA", "a", true, "Random Description")
        assertTrue(plugin.logged, "The user should be logged.")
        assertTrue(plugin.working, "The status should be working.")
        val timer : PluginTimer = getField("timer", plugin)
        assertTrue(plugin.getStatusMessage().contains("You've worked for:"), "The message should mention is working")
        Thread.sleep(1000)
        assertTrue(timer.getTiming() > 0, "The timer should be started.")
    }

    fun buildPlugin() : Plugin {
        val plugin = Plugin()
        val auther = mockAuthenticator()
        val controller = mockController()
        val api = mockAPI()

        assignVarToObj("user", controller, api.getUser("user"))
        assignVarToObj("dataController", plugin, controller)
        assignVarToObj("api", plugin, api)
        assignVarToObj("timer", plugin, PluginTimer(plugin))
        assignVarToObj("authenticator", plugin, auther)
        plugin.logged = true

        return plugin
    }

    fun assignVarToObj(name: String, target: Any, value: Any) {
        val field = target::class.java.getDeclaredField(name)
        field.isAccessible = true
        field.set(target, value)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> getField(fieldName: String, target: Any) : T {
        val field = target::class.java.getDeclaredField(fieldName)
        field.isAccessible = true
        return field.get(target) as T
    }

    fun mockController() : PluginDataController {
       return PluginDataController(mockAPI())
    }

    fun mockAuthenticator(): AuthenticateTokens {
        return mockkClass(AuthenticateTokens::class) {
            every { retrieveFullUser() } returns  UserLogin("user", "email@domain.com", "UserName", "token", "refreshToken", arrayOf())
        }
    }

    fun mockAPI(): API {
        return mockkClass(API::class) {
            every { getWorkspaces() } returns arrayOf(
                Workspace("workspaceA", "workspaceA"),
                Workspace("workspaceB", "workspaceB")
            )

            every { getProjects("workspaceA") } returns
                    arrayOf(Project("a", "Project A"), Project("b", "Project B"), Project("b", "Project B"))

            every { getProjects("workspaceB") } returns arrayOf(
                Project("a", "Project A"),
                Project("b", "Project B"),
                Project("b", "Project B"))

            every { getUser("user") } returns
                    UserInterface("user", "UserName", "workspaceA")

            every { getTags(any()) } returns
                    arrayOf(Tag("TagA", "TagNameA"), Tag("TagB", "TagNameB"))


            every { startWorking(any(), any(), any(), any()) } returns Unit
        }
    }

}