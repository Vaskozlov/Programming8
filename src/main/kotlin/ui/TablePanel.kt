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
    private val textFilter = getTextFieldWithKeyListener(null, tablePage.tableViewScope) {
        tablePage.filterChanged()
    }

    private val unselectOrganizationButton = buttonClickAdapter { tablePage.unselectOrganization() }
    private val addOrganizationButton = buttonClickAdapter {
        tablePage.runCatching { addOrganization() }.onFailure {
            JOptionPane.showMessageDialog(
                this@TablePanel,
                exceptionToMessage(it)
            )
        }
    }

    private val clearOrganizationsButton = buttonDoubleClickAdapter { mouseEvent ->
        if (mouseEvent.clickCount == 2) {
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

    private suspend fun finishOrganizationModification(updatedOrganization: Organization): Boolean {
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

        addOrganizationButton.text = Localization.get("ui.add_organization")
        unselectOrganizationButton.text = Localization.get("ui.unselect_organization")
        clearOrganizationsButton.text = Localization.get("ui.clear_organizations")
        organizationPanel.localize()
    }

    suspend fun setOrgName(organization: Organization, name: String): Boolean {
        return validateOrganizationName(this, name) &&
                finishOrganizationModification(organization.copy(name = name))
    }

    suspend fun setCoordinateX(organization: Organization, x: String): Boolean {
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

    suspend fun setCoordinateY(organization: Organization, y: String): Boolean {
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

    suspend fun setAnnualTurnover(organization: Organization, annualTurnover: String): Boolean {
        return validateOrganizationAnnualTurnover(this, annualTurnover) &&
                finishOrganizationModification(organization.copy(annualTurnover = annualTurnover.toDoubleOrNull()))
    }

    suspend fun setFullName(organization: Organization, fullName: String): Boolean {
        return validateOrganizationFullName(this, fullName) &&
                finishOrganizationModification(organization.copy(fullName = fullName))
    }

    suspend fun setEmployeesCount(organization: Organization, employeesCount: String): Boolean {
        val newEmployeesCount = employeesCount.toIntOrNull()

        return validateOrganizationEmployeesCount(this, employeesCount) &&
                finishOrganizationModification(organization.copy(employeesCount = newEmployeesCount))
    }

    suspend fun setType(organization: Organization, type: String): Boolean {
        val newType = valueOrNull<OrganizationType>(type)
        return finishOrganizationModification(organization.copy(type = newType))
    }

    suspend fun setPostalAddressZipCode(organization: Organization, zipCode: String?): Boolean {
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

    suspend fun setPostalAddressTownLocation(organization: Organization, x: String?, y: String?, z: String?): Boolean {
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

    suspend fun setPostalAddressTownX(organization: Organization, x: String): Boolean {
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

    suspend fun setPostalAddressTownY(organization: Organization, y: String): Boolean {
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

    suspend fun setPostalAddressTownZ(organization: Organization, z: String): Boolean {
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

    suspend fun setPostalAddressTownName(organization: Organization, name: String): Boolean {
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
                add(addOrganizationButton)
                add(unselectOrganizationButton)
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