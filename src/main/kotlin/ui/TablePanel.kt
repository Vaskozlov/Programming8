package ui

import collection.*
import kotlinx.coroutines.launch
import lib.Localization
import lib.valueOrNull
import ui.lib.MigFontLayout
import ui.lib.getTextFieldWithKeyListener
import javax.swing.JComboBox
import javax.swing.JLabel
import javax.swing.JOptionPane
import javax.swing.JPanel


class TablePanel(private val tablePage: TablePage) : JPanel() {
    private val textFilter = getTextFieldWithKeyListener(30) {
        tablePage.filterChanged()
    }

    private val labels = listOf(
        JLabel() to "ui.filter",
        JLabel() to "ui.filter_column",
    )

    private val layout = MigFontLayout()
    private val columnComboBox = object : JComboBox<String>() {
        init {
            for (column in TablePage.columnNames) {
                addItem(column)
            }
        }
    }


    val organizationPanel = OrganizationPanel(this)

    val filter: Pair<String, String>
        get() = Pair(columnComboBox.selectedItem as String, textFilter.text)

    fun localize() {
        labels.forEach { (label, key) ->
            label.text = Localization.get(key)
        }

        organizationPanel.localize()
    }

    fun setOrgName(organization: Organization, name: String): Boolean {
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name can not be empty.")
            return false
        }

        val updatedOrganization = Organization(organization.id, name)
        tablePage.organizationStorage.collection.modifyOrganization(updatedOrganization)
        tablePage.requestReload()

        return true
    }

    fun setCoordinateX(organization: Organization, x: String): Boolean {
        val newX = x.toLongOrNull()

        if (newX == null) {
            JOptionPane.showMessageDialog(this, "Coordinate X must be a number.")
            return false
        }

        val updatedOrganization = Organization(
            organization.id,
            coordinates = Coordinates(newX, null)
        )
        tablePage.organizationStorage.collection.modifyOrganization(updatedOrganization)
        tablePage.requestReload()

        return true
    }

    fun setCoordinateY(organization: Organization, y: String): Boolean {
        val newY = y.toLongOrNull()

        if (newY == null) {
            JOptionPane.showMessageDialog(this, "Coordinate X must be a number.")
            return false
        }

        val updatedOrganization = Organization(
            organization.id,
            coordinates = Coordinates(null, newY)
        )

        tablePage.tableViewScope.launch {
            tablePage.organizationStorage.collection.modifyOrganization(updatedOrganization)
            tablePage.requestReload()
        }

        return true
    }

    fun setAnnualTurnover(organization: Organization, annualTurnover: String): Boolean {
        val newAnnualTurnover = annualTurnover.toDoubleOrNull()

        if (newAnnualTurnover == null) {
            JOptionPane.showMessageDialog(this, "Annual turnover must be a number.")
            return false
        }

        val updatedOrganization = Organization(
            organization.id,
            annualTurnover = newAnnualTurnover
        )
        tablePage.organizationStorage.collection.modifyOrganization(updatedOrganization)
        tablePage.requestReload()
        return true
    }

    fun setFullName(organization: Organization, fullName: String): Boolean {
        if (fullName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Full name can not be empty.")
            return false
        }

        val updatedOrganization = Organization(
            organization.id,
            fullName = fullName
        )
        tablePage.organizationStorage.collection.modifyOrganization(updatedOrganization)
        tablePage.requestReload()
        return true
    }

    fun setEmployeesCount(organization: Organization, employeesCount: String): Boolean {
        val newEmployeesCount = employeesCount.toIntOrNull()

        if (newEmployeesCount == null) {
            JOptionPane.showMessageDialog(this, "Employees count must be a number.")
            return false
        }

        val updatedOrganization = Organization(
            organization.id,
            employeesCount = newEmployeesCount
        )
        tablePage.organizationStorage.collection.modifyOrganization(updatedOrganization)
        tablePage.requestReload()
        return true
    }

    fun setType(organization: Organization, type: String): Boolean {
        val newType = valueOrNull<OrganizationType>(type)

        if (newType == null && type != "null") {
            JOptionPane.showMessageDialog(this, "Type must be a number.")
            return false
        }

        val updatedOrganization = Organization(
            organization.id,
            type = if (type == "null") OrganizationType.NULL_TYPE else newType
        )

        tablePage.organizationStorage.collection.modifyOrganization(updatedOrganization)
        tablePage.requestReload()

        return true
    }

    fun setPostalAddressZipCode(organization: Organization, zipCode: String): Boolean {
        if (zipCode.length < 3 || zipCode.toIntOrNull() == null) {
            JOptionPane.showMessageDialog(this, "Zip code must be at least 3 symbols long.")
            return false
        }

        val updatedOrganization = Organization(
            organization.id,
            postalAddress = Address(zipCode, null)
        )

        tablePage.organizationStorage.collection.modifyOrganization(updatedOrganization)
        tablePage.requestReload()
        return true
    }

    fun setPostalAddressTownX(organization: Organization, x: String): Boolean {
        val newX = x.toDoubleOrNull()

        if (newX == null) {
            JOptionPane.showMessageDialog(this, "Town X must be a number.")
            return false
        }

        val updatedOrganization = Organization(
            organization.id,
            postalAddress = Address(
                null,
                Location(newX, null, null, null)
            )
        )
        tablePage.organizationStorage.collection.modifyOrganization(updatedOrganization)
        tablePage.requestReload()
        return true
    }

    fun setPostalAddressTownY(organization: Organization, y: String): Boolean {
        val newY = y.toFloatOrNull()

        if (newY == null) {
            JOptionPane.showMessageDialog(this, "Town Y must be a number.")
            return false
        }

        val updatedOrganization = Organization(
            organization.id,
            postalAddress = Address(
                null,
                Location(null, newY, null, null)
            )
        )
        tablePage.organizationStorage.collection.modifyOrganization(updatedOrganization)
        tablePage.requestReload()
        return true
    }

    fun setPostalAddressTownZ(organization: Organization, z: String): Boolean {
        val newZ = z.toLongOrNull()

        if (newZ == null) {
            JOptionPane.showMessageDialog(this, "Town Z must be a number.")
            return false
        }

        val updatedOrganization = Organization(
            organization.id,
            postalAddress = Address(
                null,
                Location(null, null, newZ, null)
            )
        )
        tablePage.organizationStorage.collection.modifyOrganization(updatedOrganization)
        tablePage.requestReload()
        return true
    }

    fun setPostalAddressTownName(organization: Organization, name: String): Boolean {
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Town name can not be empty.")
            return false
        }

        val updatedOrganization = Organization(
            organization.id,
            postalAddress = Address(
                null,
                Location(null, null, null, if (name == "null") null else name)
            )
        )

        return runCatching {
            tablePage.organizationStorage.collection.modifyOrganization(updatedOrganization)
            tablePage.requestReload()
        }.isSuccess
    }


    init {
        setLayout(layout)
        add(labels[0].first)
        add(textFilter, "wrap")
        add(labels[1].first)
        add(columnComboBox, "wrap")
        organizationPanel.init()
    }
}