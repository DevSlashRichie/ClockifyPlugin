package io.github.ricardormdev.clockifyplugin.dialogs.custompanels

import com.intellij.openapi.ui.ComboBox

class IComboBox(items: Array<out String>) : ComboBox<String>(items) {

    init {
        this.isEnabled = items.isNotEmpty()
        if(items.isEmpty())
            addItem("Nothing Found")
    }

}