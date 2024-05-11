package ui.table.panels

import lib.Localization
import ui.lib.MigFontLayout
import ui.lib.buttonClickAdapter
import ui.lib.buttonDoubleClickAdapter
import ui.lib.calculateFontSize
import javax.swing.JPanel

class ButtonPanel(tablePanel: TablePanel) : JPanel() {
    private val layout = MigFontLayout()

    private val tablePage = tablePanel.tablePage
    private val unselectOrganizationButton = buttonClickAdapter { tablePage.unselectOrganization() }
    private val addOrganizationButton = buttonClickAdapter {
        tablePage.addOrganization()
    }

    private val addOrganizationIfMaxButton = buttonClickAdapter {
        tablePage.addOrganizationIfMax()
    }

    private val clearOrganizationsButton = buttonDoubleClickAdapter { mouseEvent ->
        if (mouseEvent.clickCount == 2) {
            tablePage.clearOrganizations()
        }
    }

    private val clearOrganizationAddressButton = buttonClickAdapter {
        tablePage.clearOrganizationAddress()
    }

    fun localize() {
        addOrganizationButton.text = Localization.get("ui.add_organization")
        addOrganizationIfMaxButton.text = Localization.get("ui.add_organization_if_max")
        unselectOrganizationButton.text = Localization.get("ui.unselect_organization")
        clearOrganizationsButton.text = Localization.get("ui.clear_organizations")
        clearOrganizationAddressButton.text = Localization.get("ui.clear_address")
    }

    init {
        setLayout(layout)
        layout.fontSize = calculateFontSize(15)
        add(addOrganizationButton)
        add(addOrganizationIfMaxButton)
        add(unselectOrganizationButton)
        add(clearOrganizationAddressButton)
        add(clearOrganizationsButton)
    }
}