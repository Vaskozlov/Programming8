package ui.table.panels

import application.exceptionToMessage
import collection.*
import lib.valueOrNull
import ui.lib.*
import ui.table.TablePageWithOrganizationPanels
import javax.swing.JComboBox
import javax.swing.JLabel
import javax.swing.JOptionPane
import javax.swing.JPanel


class TablePanel(internal val tablePage: TablePageWithOrganizationPanels) : JPanel() {
    private val textFilter = getTextFieldWithKeyListener(null, tablePage.tableViewScope) {
        tablePage.filterChanged()
    }

    private val labels = listOf(
        JLabel() to "ui.filter",
        JLabel() to "ui.filter_column",
    )

    private val layout = MigFontLayout()

    private val columnComboBox = object : JComboBox<String>() {
        init {
            GuiLocalization.addActionAfter {
                val selected = selectedIndex
                removeAllItems()

                for (column in tablePage.columnNames) {
                    addItem(column)
                }

                selectedIndex = selected
            }
        }
    }

    private val actionPanel = ActionPanel(this)

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

    suspend fun setOrgName(organization: Organization, name: String): Boolean {
        return validateOrganizationName(this, name) &&
                finishOrganizationModification(organization.copy(name = name))
    }

    suspend fun setCoordinateX(organization: Organization, x: String): Boolean {
        val newX = GuiLocalization.toLong(x)

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
        val newY = GuiLocalization.toLong(y)

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
                finishOrganizationModification(
                    organization.copy(
                        annualTurnover = GuiLocalization.toDouble(annualTurnover)
                    )
                )
    }

    suspend fun setFullName(organization: Organization, fullName: String): Boolean {
        return validateOrganizationFullName(this, fullName) &&
                finishOrganizationModification(organization.copy(fullName = fullName))
    }

    suspend fun setEmployeesCount(organization: Organization, employeesCount: String): Boolean {
        val newEmployeesCount = GuiLocalization.toInt(employeesCount)

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

    suspend fun setPostalAddressTownLocation(
        organization: Organization,
        x: String?,
        y: String?,
        z: String?,
        townName: String?
    ): Boolean {
        val newX = GuiLocalization.toDouble(x)
        val newY = GuiLocalization.toFloat(y)
        val newZ = GuiLocalization.toLong(z)

        val copiedOrganization = organization.copy(
            postalAddress = Address(
                null,
                Location(newX, newY, newZ, townName)
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
        val newX = GuiLocalization.toDouble(x)

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
        val newY = GuiLocalization.toFloat(y)

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
        val newZ = GuiLocalization.toLong(z)

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

    suspend fun setPostalAddressTownName(organization: Organization, name: String?): Boolean {
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

        add(actionPanel, "span 2,wrap")
        add(labels[0].first)
        add(textFilter, "wrap")
        add(labels[1].first)
        add(columnComboBox, "wrap")
        organizationPanel.init()

        labels.forEach { (label, key) ->
            GuiLocalization.addElement(key, label)
        }
    }
}