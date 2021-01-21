package io.github.ricardormdev.clockifyplugin

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.WindowManager
import io.github.ricardormdev.clockifyplugin.api.API
import io.github.ricardormdev.clockifyplugin.api.websocket.ClockifyEvent
import io.github.ricardormdev.clockifyplugin.api.websocket.WebSocketClient
import io.github.ricardormdev.clockifyplugin.api.websocket.authentication.AuthenticateTokens
import io.github.ricardormdev.clockifyplugin.api.websocket.authentication.User
import io.github.ricardormdev.clockifyplugin.notification.Notifier
import io.github.ricardormdev.clockifyplugin.settings.settingsState
import java.util.logging.ConsoleHandler
import java.util.logging.Level
import java.util.logging.Logger
import kotlin.collections.HashMap


class Plugin {

    // LOAD OUR LOGGER
    private var logger: Logger = Logger.getLogger("ClockifyPlugin")

    // LOAD THE AUTHENTICATOR.
    private lateinit var authenticator: AuthenticateTokens

    // LOAD THE API
    private lateinit var api: API

    // LOAD SOCKET
    private lateinit var client: WebSocketClient

    // LOAD ALL FOREIGN CLASSES (CACHE FOR CLOCKIFY DATA).
    lateinit var dataController: PluginDataController

    val containers: HashMap<Project, String> = HashMap()

    private val sessID = random8String()

    // VARIABLES
    var logged = false
    private lateinit var timer: PluginTimer
    var working = false
    private var editableStatusMessage = ""

    fun doLoad() {
        val handler = ConsoleHandler()
        handler.level = Level.ALL
        logger.addHandler(handler)
        logger.level = Level.ALL
        logger.useParentHandlers = false

        logged = false
        editableStatusMessage = "Logging in..."
        updateContainers()

        val thread = Thread {
            if(working) {
                stopLocalWork()
                stopWorking()
            }

            "Making sure about login data".print()

            if(!verifyLoginData()) {
                "Could not login".print()
                editableStatusMessage = "Please login! Clockify might not work."
                Notifier.notifyWarning(editableStatusMessage)
                logged = false
                updateContainers()
            }

            "Authenticating user.".print()

            val credentials = settingsState.getCredentials()
            val email = credentials?.userName ?: ""
            val password = credentials?.getPasswordAsString() ?: ""

            authenticator = AuthenticateTokens(User(email, password))

            // VALIDATE INFORMATION.
            authenticator.retrieveFullUser().let {
                if(it == null) {
                    "We couldn't authenticate, wrong details. We'll try to use your saved token.".print()
                    editableStatusMessage = "Couldn't authenticate you, wrong login details."
                    Notifier.notifyError("$editableStatusMessage We'll try to use your saved token.")
                    logged = false
                    updateContainers()
                }
            }

            "Create API interface".print()

            api = API(authenticator)

            "Creating connection with clockify.".print()

            client = WebSocketClient("wss://stomp.clockify.me:80/clockify/$email/$sessID")
            client.open()
            client.authenticate(authenticator.retrieveToken())

            "Loading data from clockify.".print()

            dataController = PluginDataController(api)

            if(!dataController.prepareUser(authenticator)) {
                "Could not fetch your use id".print()
                editableStatusMessage = "Error while fetching your username."
                Notifier.notifyError(editableStatusMessage)
                updateContainers()
                logged = false
                return@Thread
            }

            logged = true

            "Starting timer interface.".print()

            timer = PluginTimer(this)

            editableStatusMessage = ""
            updateContainers()

            "Preparing connection reader.".print()
            client.registerAdapter(ClockifyEvent.TIME_ENTRY_STARTED) {
                startLocalWork()
            }

            client.registerAdapter(ClockifyEvent.TIME_ENTRY_STOPPED) {
                stopLocalWork()
            }

            client.registerAdapter(ClockifyEvent.ACTIVE_WORKSPACE_CHANGED) {
                dataController.updateUser()
            }

            Notifier.notifyInfo("Clockify loaded correctly.")
        }

        thread.start()
    }

    private fun verifyLoginData() : Boolean {
        val credentials = settingsState.getCredentials()
        val email = credentials?.userName ?: ""
        val password = credentials?.getPasswordAsString() ?: ""

        return email.isNotBlank() and password.isNotBlank()
    }

    fun updateContainers() {
        containers.forEach { (t, u) ->
            run {
                WindowManager.getInstance().getStatusBar(t).updateWidget(u)
            }
        }
    }

    fun startWorking(workspaceId: String, projectId: String, billable: Boolean, description: String) {
        if(!working) {
            api.startWorking(workspaceId, projectId, billable, description)
            dataController.user.activeWorkspace = workspaceId
            startLocalWork()
        }

    }

    fun stopWorking() {
        if(working) {
            api.endWorking(dataController.user.activeWorkspace)
            stopLocalWork()
        }
    }

    private fun startLocalWork() {
        working = true
        timer.startTimer()
    }

    private fun stopLocalWork() {
        working = false
        timer.stopTimer()
    }

    fun getStatusMessage() : String {
        if(editableStatusMessage.isNotBlank())
            return editableStatusMessage
        return if(working)
            "You've worked for: " + timer.getTiming().toFormattedTime()
        else "You are not working."
    }

}
