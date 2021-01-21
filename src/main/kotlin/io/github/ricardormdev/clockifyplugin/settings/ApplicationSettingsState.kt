package io.github.ricardormdev.clockifyplugin.settings

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil

val settingsState: ApplicationSettingsState
    get() = ServiceManager.getService(ApplicationSettingsState::class.java)

@State(name = "io.github.ricardormdev.carlitosplugin.settings.AppSettings", storages = [Storage("carlitos.xml")])
class ApplicationSettingsState : PersistentStateComponent<ApplicationSettingsState> {

    data class UserID(var value: String = "")
    data class Email(var value: String = "")
    data class Password(var value: String = "")
    data class Token(var value: String = "")
    data class RefreshToken(var value: String = "")

    var userID = UserID()
    var email = Email()
    var password = Password()
    var token = Token()
    var refreshToken = RefreshToken()

    override fun getState(): ApplicationSettingsState {
        return this
    }

    override fun loadState(state: ApplicationSettingsState) {
        XmlSerializerUtil.copyBean(state, this)
    }

}