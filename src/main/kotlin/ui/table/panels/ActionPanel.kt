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

        GuiLocalization.addElement(
            "ui.add_organization",
            addOrganizationButton
        )

        GuiLocalization.addElement(
            "ui.add_organization_if_max",
            addOrganizationIfMaxButton
        )

        GuiLocalization.addElement(
            "ui.unselect_organization",
            unselectOrganizationButton
        )

        GuiLocalization.addElement(
            "ui.clear_organizations",
            clearOrganizationsButton
        )

        GuiLocalization.addElement(
            "ui.clear_address",
            clearOrganizationAddressButton
        )

        GuiLocalization.addElement(
            "ui.remove_all_by_address",
            removeAllByPostalAddressButton
        )
    }
}