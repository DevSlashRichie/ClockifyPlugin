package io.github.ricardormdev.clockifyplugin.settings

import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBPasswordField
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JPanel

class AppSettingsComponent {

    private val email = JBTextField()
    private val password = JBPasswordField()

    private val mainPanel: JPanel = FormBuilder.createFormBuilder()
        .addLabeledComponent(JBLabel("Username or Email: "), email, 1, false)
        .addLabeledComponent(JBLabel("Password: "), password, 1, false)
        .addComponentFillVertically(JPanel(), 0)
        .panel

    fun getPanel() : JPanel = mainPanel

    fun getEmail() : String {
        return email.text
    }

    fun getPassword(): String {
        return String(password.password)
    }

    fun getPreferredFocusedComponent() : JComponent = email

    fun setEmail(value: String) { email.text = value  }

    fun setPassword(value: String) { password.text = value }

}