package ui

import application.exceptionToMessage
import collection.*
import lib.Localization
import lib.valueOrNull
import ui.lib.*
import javax.swing.JComboBox
import javax.swing.JLabel
import javax.swing.JOptionPane
import javax.swing.JPanel


class TablePanel(internal val tablePage: TablePage) : JPanel() {
    private val textFilter = getTextFieldWithKeyListener(null) {
        tablePage.filterChanged()
    }

    private val unselectOrganization = buttonClickAdapter { tablePage.unselectOrganization() }
    private val addOrganization = buttonClickAdapter {
        tablePage.runCatching { addOrganization() }.onFailure {
            JOptionPane.showMessageDialog(
                this@TablePanel,
                exceptionToMessage(it)
            )
        }
    }

    private val clearOrganizationsButton = buttonDoubleClickAdapter {
        if (it.clickCount == 2) {
            tablePage.runCatching { clearOrganizations() }.onFailure {
                JOptionPane.showMessageDialog(
                    this@TablePanel,
                    exceptionToMessage(it)
                )
            }
        }
    }

    private val labels = listOf(
        JLabel() to "ui.filter",
        JLabel() to "ui.filter_column",
    )

    private val layout = MigFontLayout("", "[fill,grow]", "[fill,grow]")
    private val columnComboBox = object : JComboBox<String>() {
        init {
            for (column in BasicTablePage.columnNames) {
                addItem(column)
            }
        }
    }


    val organizationPanel = OrganizationPanel(this)

    val filter: Pair<String, String>
        get() = Pair(columnComboBox.selectedItem as String, textFilter.text)

    private fun finishOrganizationModification(updatedOrganization: Organization): Boolean {
        if (!validateLocationInOrganization(this, updatedOrganization)) {
            return false
        }

        return runCatching {
            tablePage.modifyOrganization(updatedOrganization)
        }.onFailure {
            JOptionPane.showMessageDialog(
                this@TablePanel,
                exceptionToMessage(it)
            )
        }.isSuccess
    }

    fun localize() {
        labels.forEach { (label, key) ->
            label.text = Localization.get(key)
        }

        addOrganization.text = Localization.get("ui.add_organization")
        unselectOrganization.text = Localization.get("ui.unselect_organization")
        clearOrganizationsButton.text = Localization.get("ui.clear_organizations")
        organizationPanel.localize()
    }

    fun setOrgName(organization: Organization, name: String): Boolean {
        return validateOrganizationName(this, name) &&
                finishOrganizationModification(organization.copy(name = name))
    }

    fun setCoordinateX(organization: Organization, x: String): Boolean {
        val newX = x.toLongOrNull()

        val copiedOrganization = organization.copy(
            coordinates = Coordinates(
                newX,
                null
            )
        )

        copiedOrganization.coordinates = fillCoordinatesWithMissedInformation(
            copiedOrganization.coordinates,
            organization.coordinates
        )

        return validateOrganizationCoordinateX(this, x) &&
                finishOrganizationModification(copiedOrganization)
    }

    fun setCoordinateY(organization: Organization, y: String): Boolean {
        val newY = y.toLongOrNull()

        val copiedOrganization = organization.copy(
            coordinates = Coordinates(
                null,
                newY
            )
        )

        copiedOrganization.coordinates = fillCoordinatesWithMissedInformation(
            copiedOrganization.coordinates,
            organization.coordinates
        )

        return validateOrganizationCoordinateY(this, y) &&
                finishOrganizationModification(copiedOrganization)
    }

    fun setAnnualTurnover(organization: Organization, annualTurnover: String): Boolean {
        return validateOrganizationAnnualTurnover(this, annualTurnover) &&
                finishOrganizationModification(organization.copy(annualTurnover = annualTurnover.toDoubleOrNull()))
    }

    fun setFullName(organization: Organization, fullName: String): Boolean {
        return validateOrganizationFullName(this, fullName) &&
                finishOrganizationModification(organization.copy(fullName = fullName))
    }

    fun setEmployeesCount(organization: Organization, employeesCount: String): Boolean {
        val newEmployeesCount = employeesCount.toIntOrNull()

        return validateOrganizationEmployeesCount(this, employeesCount) &&
                finishOrganizationModification(organization.copy(employeesCount = newEmployeesCount))
    }

    fun setType(organization: Organization, type: String): Boolean {
        val newType = valueOrNull<OrganizationType>(type)
        return finishOrganizationModification(organization.copy(type = newType))
    }

    fun setPostalAddressZipCode(organization: Organization, zipCode: String?): Boolean {
        val copiedOrganization = organization.copy(
            postalAddress = Address(
                zipCode,
                null
            )
        )

        copiedOrganization.postalAddress!!.town =
            fillLocationWithMissedInformation(
                copiedOrganization.postalAddress?.town,
                organization.postalAddress?.town
            )

        return validateOrganizationZipCode(this, zipCode) &&
                finishOrganizationModification(copiedOrganization)
    }

    fun setPostalAddressTownLocation(organization: Organization, x: String?, y: String?, z: String?): Boolean {
        val newX = x?.toDoubleOrNull()
        val newY = y?.toFloatOrNull()
        val newZ = z?.toLongOrNull()

        val copiedOrganization = organization.copy(
            postalAddress = Address(
                null,
                Location(newX, newY, newZ, null)
            )
        )

        copiedOrganization.postalAddress =
            fillAddressWithMissedInformation(
                copiedOrganization.postalAddress,
                organization.postalAddress
            )

        return validateOrganizationLocationX(this, x) &&
                validateOrganizationLocationY(this, y) &&
                validateOrganizationLocationZ(this, z) &&
                finishOrganizationModification(copiedOrganization)
    }

    fun setPostalAddressTownX(organization: Organization, x: String): Boolean {
        val newX = x.toDoubleOrNull()

        val copiedOrganization = organization.copy(
            postalAddress = Address(
                null,
                Location(newX, null, null, null)
            )
        )

        copiedOrganization.postalAddress =
            fillAddressWithMissedInformation(
                copiedOrganization.postalAddress,
                organization.postalAddress
            )

        return validateOrganizationLocationX(this, x) &&
                finishOrganizationModification(copiedOrganization)
    }

    fun setPostalAddressTownY(organization: Organization, y: String): Boolean {
        val newY = y.toFloatOrNull()

        val copiedOrganization = organization.copy(
            postalAddress = Address(
                null,
                Location(null, newY, null, null)
            )
        )

        copiedOrganization.postalAddress =
            fillAddressWithMissedInformation(
                copiedOrganization.postalAddress,
                organization.postalAddress
            )

        return validateOrganizationLocationY(this, y) &&
                finishOrganizationModification(copiedOrganization)
    }

    fun setPostalAddressTownZ(organization: Organization, z: String): Boolean {
        val newZ = z.toLongOrNull()

        val copiedOrganization = organization.copy(
            postalAddress = Address(
                null,
                Location(null, null, newZ, null)
            )
        )

        copiedOrganization.postalAddress =
            fillAddressWithMissedInformation(
                copiedOrganization.postalAddress,
                organization.postalAddress
            )

        return validateOrganizationLocationZ(this, z) &&
                finishOrganizationModification(copiedOrganization)
    }

    fun setPostalAddressTownName(organization: Organization, name: String): Boolean {
        val copiedOrganization = organization.copy(
            postalAddress = Address(
                null,
                Location(null, null, null, name)
            )
        )

        copiedOrganization.postalAddress =
            fillAddressWithMissedInformation(
                copiedOrganization.postalAddress,
                organization.postalAddress
            )

        return finishOrganizationModification(copiedOrganization)
    }

    init {
        setLayout(layout)
        layout.fontSize = calculateFontSize(15)

        val buttonPanel = object : JPanel() {
            private val layout = MigFontLayout("", "[fill,grow][fill,grow]", "[fill,grow]")

            init {
                setLayout(layout)
                layout.fontSize = calculateFontSize(15)
                add(addOrganization)
                add(unselectOrganization)
                add(clearOrganizationsButton)
            }
        }

        add(buttonPanel, "span 2,wrap")
        add(labels[0].first)
        add(textFilter, "wrap")
        add(labels[1].first)
        add(columnComboBox, "wrap")
        organizationPanel.init()
    }
}