package ui.lib

import ui.OrganizationPanel
import javax.swing.JComboBox

class TypeEditor(private val panel: OrganizationPanel) : JComboBox<String>() {
    @Volatile
    private var isUnderTextModification = false

    fun setTextNoUpdate(text: String?) {
        runCatching {
            isUnderTextModification = true
            selectedItem = text
        }

        isUnderTextModification = false
    }

    init {
        addItem("COMMERCIAL")
        addItem("PUBLIC")
        addItem("PRIVATE_LIMITED_COMPANY")
        addItem("OPEN_JOINT_STOCK_COMPANY")
        addItem("null")

        prototypeDisplayValue = "XXXXXXXXXXXXXXXXXXXXXX";

        addItemListener {
            if (!isUnderTextModification) {
                panel.getOrganizationByIdInUI()?.let { org ->
                    panel.parent.setType(org, it.item.toString())
                }
            }
        }
    }
}