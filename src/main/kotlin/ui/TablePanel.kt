package ui

import application.exceptionToMessage
import collection.*
import lib.Localization
import lib.valueOrNull
import ui.lib.BasicTablePage
import ui.lib.MigFontLayout
import ui.lib.getTextFieldWithKeyListener
import java.awt.Dimension
import java.awt.Toolkit
import javax.swing.JComboBox
import javax.swing.JLabel
import javax.swing.JOptionPane
import javax.swing.JPanel


class TablePanel(internal val tablePage: TablePage) : JPanel() {
    private val textFilter = getTextFieldWithKeyListener(30, null) {
        tablePage.filterChanged()
    }

    private val labels = listOf(
        JLabel() to "ui.filter",
        JLabel() to "ui.filter_column",
    )

    private val layout = MigFontLayout()
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

        organizationPanel.localize()
    }

    fun setOrgName(organization: Organization, name: String): Boolean {
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name can not be empty.")
            return false
        }

        return finishOrganizationModification(Organization(organization.id, name))
    }

    fun setCoordinateX(organization: Organization, x: String): Boolean {
        val newX = x.toLongOrNull()

        if (newX == null) {
            JOptionPane.showMessageDialog(this, "Coordinate X must be a number.")
            return false
        }

        return finishOrganizationModification(
            Organization(
                organization.id,
                coordinates = Coordinates(newX, null)
            )
        )
    }

    fun setCoordinateY(organization: Organization, y: String): Boolean {
        val newY = y.toLongOrNull()

        if (newY == null) {
            JOptionPane.showMessageDialog(this, "Coordinate X must be a number.")
            return false
        }

        return finishOrganizationModification(
            Organization(
                organization.id,
                coordinates = Coordinates(null, newY)
            )
        )
    }

    fun setAnnualTurnover(organization: Organization, annualTurnover: String): Boolean {
        val newAnnualTurnover = annualTurnover.toDoubleOrNull()

        if (newAnnualTurnover == null) {
            JOptionPane.showMessageDialog(this, "Annual turnover must be a number.")
            return false
        }

        return finishOrganizationModification(
            Organization(
                organization.id,
                annualTurnover = newAnnualTurnover
            )
        )
    }

    fun setFullName(organization: Organization, fullName: String): Boolean {
        if (fullName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Full name can not be empty.")
            return false
        }

        return finishOrganizationModification(
            Organization(
                organization.id,
                fullName = fullName
            )
        )
    }

    fun setEmployeesCount(organization: Organization, employeesCount: String): Boolean {
        val newEmployeesCount = employeesCount.toIntOrNull()

        if (newEmployeesCount == null) {
            JOptionPane.showMessageDialog(this, "Employees count must be a number.")
            return false
        }

        return finishOrganizationModification(
            Organization(
                organization.id,
                employeesCount = newEmployeesCount
            )
        )
    }

    fun setType(organization: Organization, type: String): Boolean {
        val newType = valueOrNull<OrganizationType>(type)

        if (newType == null && type != "null") {
            JOptionPane.showMessageDialog(this, "Type must be a number.")
            return false
        }

        return finishOrganizationModification(
            Organization(
                organization.id,
                type = if (type == "null") OrganizationType.NULL_TYPE else newType
            )
        )
    }

    fun setPostalAddressZipCode(organization: Organization, zipCode: String?): Boolean {
        if (zipCode != null && zipCode != "null" && (zipCode.length < 3 || zipCode.toIntOrNull() == null)) {
            JOptionPane.showMessageDialog(this, "Zip code must be at least 3 symbols long.")
            return false
        }

        return finishOrganizationModification(
            Organization(
                organization.id,
                postalAddress = Address(zipCode.takeIf { zipCode != "null" } ?: "", null)
            )
        )
    }

    fun setPostalAddressTownX(organization: Organization, x: String): Boolean {
        val newX = x.toDoubleOrNull()

        if (newX == null) {
            JOptionPane.showMessageDialog(this, "Town X must be a number.")
            return false
        }

        return finishOrganizationModification(
            Organization(
                organization.id,
                postalAddress = Address(
                    null,
                    Location(newX, null, null, null)
                )
            )
        )
    }

    fun setPostalAddressTownY(organization: Organization, y: String): Boolean {
        val newY = y.toFloatOrNull()

        if (newY == null) {
            JOptionPane.showMessageDialog(this, "Town Y must be a number.")
            return false
        }

        return finishOrganizationModification(
            Organization(
                organization.id,
                postalAddress = Address(
                    null,
                    Location(null, newY, null, null)
                )
            )
        )
    }

    fun setPostalAddressTownZ(organization: Organization, z: String): Boolean {
        val newZ = z.toLongOrNull()

        if (newZ == null) {
            JOptionPane.showMessageDialog(this, "Town Z must be a number.")
            return false
        }

        return finishOrganizationModification(
            Organization(
                organization.id,
                postalAddress = Address(
                    null,
                    Location(null, null, newZ, null)
                )
            )
        )
    }

    fun setPostalAddressTownName(organization: Organization, name: String): Boolean {
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Town name can not be empty.")
            return false
        }

        return finishOrganizationModification(
            Organization(
                organization.id,
                postalAddress = Address(
                    null,
                    Location(null, null, null, if (name == "null") null else name)
                )
            )
        )
    }

    init {
        setLayout(layout)
        val screenSize: Dimension = Toolkit.getDefaultToolkit().screenSize
        layout.fontSize = 15 * screenSize.width / 1920

        add(labels[0].first)
        add(textFilter, "wrap")
        add(labels[1].first)
        add(columnComboBox, "wrap")
        organizationPanel.init()
    }
}