package ui.table.panels

import ui.lib.GuiLocalization
import ui.lib.MigFontLayout
import ui.lib.buttonClickAdapter
import ui.lib.buttonDoubleClickAdapter
import ui.panels.SelectLanguagePanel
import javax.swing.JPanel


class ActionPanel(tablePanel: TablePanel) : JPanel() {
    private val layout = MigFontLayout("insets 0")
    
    private val tablePage = tablePanel.tablePage
    private val unselectOrganizationButton = buttonClickAdapter { tablePage.unselectOrganization() }
    private val addOrganizationButton = buttonClickAdapter { tablePage.addOrganization() }
    private val addOrganizationIfMaxButton = buttonClickAdapter { tablePage.addOrganizationIfMax() }
    
    private val clearOrganizationsButton = buttonDoubleClickAdapter { mouseEvent ->
        if (mouseEvent.clickCount == 2) {
            tablePage.clearOrganizations()
        }
    }
    
    private val clearOrganizationAddressButton = buttonClickAdapter { tablePage.clearOrganizationAddress() }
    private val removeAllByPostalAddressButton = buttonClickAdapter { tablePage.removeAllByPostalAddress() }
    private val selectLangaugePanel = SelectLanguagePanel(15, tablePanel.tablePage.tableViewScope)
    
    init {
        setLayout(layout)
        
        add(selectLangaugePanel, "wrap")
        add(addOrganizationButton)
        add(addOrganizationIfMaxButton)
        add(unselectOrganizationButton, "wrap")
        add(clearOrganizationAddressButton)
        add(removeAllByPostalAddressButton)
        add(clearOrganizationsButton)
        
        GuiLocalization.addActionAfterLanguageUpdate {
            addOrganizationButton.text = GuiLocalization.currentLocale.uiAddOrganization()
            addOrganizationIfMaxButton.text = GuiLocalization.currentLocale.uiAddOrganizationIfMax()
            unselectOrganizationButton.text = GuiLocalization.currentLocale.uiUnselectOrganization()
            clearOrganizationsButton.text = GuiLocalization.currentLocale.uiClearOrganizations()
            clearOrganizationAddressButton.text = GuiLocalization.currentLocale.uiClearAddress()
            removeAllByPostalAddressButton.text = GuiLocalization.currentLocale.uiRemoveAllByAddress()
        }
    }
}