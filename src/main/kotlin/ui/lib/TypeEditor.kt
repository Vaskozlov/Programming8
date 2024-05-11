package ui.lib

import kotlinx.coroutines.launch
import ui.table.panels.OrganizationPanel
import javax.swing.JComboBox

class TypeEditor(private val panel: OrganizationPanel) : JComboBox<String>() {
    @Volatile
    private var isUnderTextModification = false

    private val tableViewScope = panel.parent.tablePage.tableViewScope

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

        addItemListener {
            if (!isUnderTextModification) {
                tableViewScope.launch {
                    panel.getOrganizationByIdInUI()?.let { org ->
                        panel.parent.setType(org, it.item.toString())
                    }
                }
            }
        }
    }
}