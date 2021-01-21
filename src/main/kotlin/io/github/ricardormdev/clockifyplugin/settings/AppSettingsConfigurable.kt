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
        return !component?.getEmail().equals(settingsState.email.value) or
                !component?.getPassword().equals(settingsState.password.value)
    }

    override fun apply() {
        settingsState.email.value = component?.getEmail() ?: ""
        settingsState.password.value = component?.getPassword() ?: ""
        settingsState.refreshToken.value = ""
        settingsState.token.value = ""
        settingsState.token.value = ""

        PluginLoader.plugin.doLoad()
    }

    override fun reset() {
        component?.setEmail(settingsState.email.value)
        component?.setPassword(settingsState.password.value)
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