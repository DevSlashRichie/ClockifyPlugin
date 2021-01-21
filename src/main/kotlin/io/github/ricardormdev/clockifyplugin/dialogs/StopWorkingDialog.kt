package io.github.ricardormdev.clockifyplugin.dialogs

import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.JBLabel
import javax.swing.JComponent
import javax.swing.JPanel

class StopWorkingDialog : DialogWrapper(false) {

    private val panel = JPanel()
    private val announce = JBLabel("Click OK to stop working")

    init {
        init()
        title = "Stop Working"
    }

    override fun createCenterPanel(): JComponent {
        panel.add(announce)
        return panel
    }

}