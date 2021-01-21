package io.github.ricardormdev.clockifyplugin.settings

import com.intellij.credentialStore.CredentialAttributes
import com.intellij.credentialStore.Credentials
import com.intellij.credentialStore.generateServiceName
import com.intellij.ide.passwordSafe.PasswordSafe
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil

val settingsState: ApplicationSettingsState
    get() = ServiceManager.getService(ApplicationSettingsState::class.java)

@State(name = "io.github.ricardormdev.clockifyplugin.settings.AppSettings", storages = [Storage("clockify.xml")])
class ApplicationSettingsState : PersistentStateComponent<ApplicationSettingsState> {
    data class UserID(var value: String = "")

    var userID = UserID()

    fun getCredentialsAttributes(key: String) : CredentialAttributes  {
        return CredentialAttributes(generateServiceName("Clockify", key))
    }

    fun getCredentials(): Credentials? {
        return PasswordSafe.instance.get(getCredentialsAttributes("tokenGetter"))
    }

    fun setCredentials(password: String, username: String) {
        val credentials = Credentials(username, password)
        PasswordSafe.instance.set(getCredentialsAttributes("tokenGetter"), credentials)
    }

    fun getToken(type: String) : String {
        return PasswordSafe.instance.getPassword(getCredentialsAttributes(type)) ?: ""
    }

    fun setToken(type: String, token: String) {
        val credentials = Credentials("", token)
        PasswordSafe.instance.set(getCredentialsAttributes(type), credentials)
    }

    override fun getState(): ApplicationSettingsState {
        return this
    }

    override fun loadState(state: ApplicationSettingsState) {
        XmlSerializerUtil.copyBean(state, this)
    }

}