package io.github.ricardormdev.clockifyplugin.dialogs

import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import io.github.ricardormdev.clockifyplugin.PluginDataController
import io.github.ricardormdev.clockifyplugin.PluginLoader
import io.github.ricardormdev.clockifyplugin.dialogs.custompanels.IComboBox
import java.awt.event.ItemEvent
import java.util.concurrent.Executors
import javax.swing.JComponent
import javax.swing.JPanel

class StartWorkingDialog(private val project: Project) : DialogWrapper(false) {

    companion object {
        private val threadPool = Executors.newCachedThreadPool()
    }

    lateinit var workspace: IComboBox
    lateinit var description: JBTextField
    lateinit var projectName: IComboBox
    lateinit var tags: IComboBox
    lateinit var billable: JBCheckBox

    init {
        init()
        title = "I'm Working On"
    }

    private val dataController: PluginDataController
        get() = PluginLoader.plugin.dataController

    private fun placeFields() {
        val currentFile = FileEditorManager.getInstance(project).selectedEditor?.file?.name ?: project.name

        val workspaces = dataController.workspaces.map { it.name }.toTypedArray()
        val pickedWorkspaceIndex = workspaces.indexOf(dataController.getWorkspaceByID(dataController.user.activeWorkspace).name)

        workspace = IComboBox(workspaces)
        workspace.selectedIndex = pickedWorkspaceIndex

        description = JBTextField("Working on: $currentFile")
        projectName = IComboBox(dataController.projects.map { it.name }.toTypedArray())
        tags = IComboBox(dataController.tags.map { it.name }.toTypedArray())
        billable = JBCheckBox("", true)

    }

    private fun silentUpdate(item: String) {
        projectName.removeAllItems()
        tags.removeAllItems()

        this.myOKAction.isEnabled = false
        this.projectName.isEditable = false
        this.tags.isEditable = false

        projectName.addItem("Loading...")
        tags.addItem("Loading...")

        threadPool.submit {
            dataController.fetchSpecific(item)

            projectName.removeAllItems()
            tags.removeAllItems()

            dataController.projects.forEach { projectName.addItem(it.name) }
            dataController.tags.forEach { tags.addItem(it.name) }

            this.myOKAction.isEnabled = true
            this.projectName.isEditable = false
            this.tags.isEditable = false
        }

    }


    override fun createCenterPanel(): JComponent {
        dataController.fetchData()
        placeFields()

        val panel = FormBuilder.createFormBuilder()
            .addLabeledComponent(JBLabel("Workspace: "), workspace, 1, false)
            .addLabeledComponent(JBLabel("Description: "), description, 1, false)
            .addLabeledComponent(JBLabel("Project Name: "), projectName, 1, false)
            .addLabeledComponent(JBLabel("Tag: "), tags, 1, false)
            .addLabeledComponent(JBLabel("Billable: "), billable, 1, false)
            .addComponentFillVertically(JPanel(), 1)
            .panel

        workspace.addItemListener {
            if(it.stateChange == ItemEvent.SELECTED) {
                val item  = it.item as String
                silentUpdate(item)
            }
        }

        return panel
    }

}