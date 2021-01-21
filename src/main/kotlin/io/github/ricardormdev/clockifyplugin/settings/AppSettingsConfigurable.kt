package io.github.ricardormdev.clockifyplugin.settings

import com.intellij.openapi.options.Configurable
import io.github.ricardormdev.clockifyplugin.PluginLoader
import org.jetbrains.annotations.Nls
import javax.swing.JComponent

class AppSettingsConfigurable : Configurable {

    private var component: AppSettingsComponent? = null

    override fun createComponent(): JComponent? {
        component = AppSettingsComponent()
        return component?.getPanel()
    }

    override fun isModified(): Boolean {
        val credentials = settingsState.getCredentials()

        val username = credentials?.userName ?: ""
        val password = credentials?.getPasswordAsString() ?: ""

        return !component?.getEmail().equals(username) or
                !component?.getPassword().equals(password)
    }

    override fun apply() {
        settingsState.setCredentials(component?.getPassword() ?: "", component?.getEmail() ?: "")

        settingsState.setToken("token", "")
        settingsState.setToken("refreshToken", "")

        PluginLoader.plugin.doLoad()
    }

    override fun reset() {
        val credentials = settingsState.getCredentials()

        val username = credentials?.userName ?: ""
        val password = credentials?.getPasswordAsString() ?: ""

        component?.setEmail(username)
        component?.setPassword(password)

        PluginLoader.plugin.doLoad()
    }

    @Nls(capitalization = Nls.Capitalization.Title)
    override fun getDisplayName(): String {
        return "Carlitos Plugin Configuration"
    }

    override fun disposeUIResources() {
        component = null
    }

    override fun getPreferredFocusedComponent(): JComponent {
        return component?.getPreferredFocusedComponent()!!
    }
}