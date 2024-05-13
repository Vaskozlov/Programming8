package ui.lib

import kotlinx.coroutines.launch
import ui.table.panels.OrganizationPanel
import java.awt.event.ItemEvent
import javax.swing.JComboBox

class TypeEditor(private val panel: OrganizationPanel) : JComboBox<String>() {
    companion object {
        @Volatile
        var isUnderTextModification = false
    }

    private val tableViewScope = panel.parent.tablePage.tableViewScope

    fun setTextNoUpdate(text: String?) {
        runCatching {
            isUnderTextModification = true
            selectedItem = text
        }

        isUnderTextModification = false
    }

    private fun updateType(itemEvent: ItemEvent) = tableViewScope.launch {
        val type = GuiLocalization.parseOrganizationType(itemEvent.item.toString())

        panel.getOrganizationByIdInUI()?.let { org ->
            panel.parent.setType(org, type.toString())
        }
    }

    init {
        GuiLocalization.addActionAfterLanguageUpdate {
            val selected = selectedIndex
            removeAllItems()

            addItem(GuiLocalization.currentLocale.uiTypeNull())
            addItem(GuiLocalization.currentLocale.uiTypeCommercial())
            addItem(GuiLocalization.currentLocale.uiTypePublic())
            addItem(GuiLocalization.currentLocale.uiTypePrivateLimitedCompany())
            addItem(GuiLocalization.currentLocale.uiTypeOpenJointStockCompany())

            selectedIndex = selected
        }

        addItemListener {
            if (!isUnderTextModification) {
                updateType(it)
            }
        }
    }
}