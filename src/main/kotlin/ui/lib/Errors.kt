package ui.lib

import application.exceptionToMessage
import collection.Organization
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.swing.Swing
import kotlinx.coroutines.withContext
import ui.table.panels.OrganizationPanel
import ui.table.panels.TablePanel
import java.awt.Color
import java.awt.Component
import javax.swing.JOptionPane
import javax.swing.JTextField

suspend fun setColorToTablePanelLabel(parentComponent: TablePanel, keys: List<String>, color: Color) =
    withContext(Dispatchers.Swing) {
        val panel = parentComponent.organizationPanel
        keys.forEach { key ->
            val field = panel.uiElements[key]?.second!! as? JTextField
            field?.background = color
        }
    }

suspend fun showMessageDialog(parentComponent: Component, error: Throwable) {
    withContext(Dispatchers.Swing) {
        JOptionPane.showMessageDialog(parentComponent, exceptionToMessage(error))
    }
}

suspend fun showMessageDialog(parentComponent: Component, message: String) {
    withContext(Dispatchers.Swing) {
        JOptionPane.showMessageDialog(parentComponent, message)
    }
}

internal fun isNullString(value: String?): Boolean {
    return value.isNullOrBlank() || value == "null"
}

suspend fun validateOrganizationName(parentComponent: Component, name: String?): Boolean {
    if (isNullString(name)) {
        showMessageDialog(parentComponent, GuiLocalization.get("ui.validate.invalid_name"))
        return false
    }

    return true
}

suspend fun validateOrganizationCoordinateX(parentComponent: Component, x: String?): Boolean {
    val newX = GuiLocalization.toLong(x)

    if (newX == null) {
        showMessageDialog(parentComponent, GuiLocalization.get("ui.validate.invalid_coordinate_x"))
        return false
    }

    return true
}

suspend fun validateOrganizationCoordinateY(parentComponent: Component, y: String?): Boolean {
    val newY = GuiLocalization.toDouble(y)

    if (newY == null) {
        showMessageDialog(parentComponent, GuiLocalization.get("ui.validate.invalid_coordinate_y"))
        return false
    }

    return true
}

suspend fun validateOrganizationAnnualTurnover(parentComponent: Component, turnover: String?): Boolean {
    val newTurnover = GuiLocalization.toDouble(turnover)

    if (newTurnover == null || newTurnover < 0) {
        showMessageDialog(parentComponent, GuiLocalization.get("ui.validate.invalid_annual_turnover"))
        return false
    }

    return true
}

suspend fun validateOrganizationFullName(parentComponent: Component, fullName: String?): Boolean {
    if (fullName.isNullOrBlank()) {
        showMessageDialog(parentComponent, GuiLocalization.get("ui.validate.invalid_full_name"))
        return false
    }

    return true
}

suspend fun validateOrganizationEmployeesCount(parentComponent: Component, employeesCount: String?): Boolean {
    val newEmployeesCount = GuiLocalization.toInt(employeesCount)

    if (newEmployeesCount != null && newEmployeesCount < 0) {
        showMessageDialog(parentComponent, GuiLocalization.get("ui.validate.invalid_employees_count"))
        return false
    }

    return true
}

suspend fun validateOrganizationZipCode(parentComponent: Component, zipCode: String?): Boolean {
    if (!isNullString(zipCode) && (zipCode!!.length < 3 || GuiLocalization.toLong(zipCode) == null)) {
        showMessageDialog(parentComponent, GuiLocalization.get("ui.validate.invalid_zip_code"))
        return false
    }

    return true
}

suspend fun validateOrganizationLocationX(parentComponent: TablePanel, x: String?): Boolean {
    val newX = GuiLocalization.toDouble(x)

    if (x != null && newX == null) {
        setColorToTablePanelLabel(
            parentComponent,
            listOf(OrganizationPanel.UI_LOCATION_X),
            Color.RED
        )
        return false
    }

    setColorToTablePanelLabel(
        parentComponent,
        listOf(OrganizationPanel.UI_LOCATION_X),
        Color.WHITE
    )

    return true
}

suspend fun validateOrganizationLocationY(parentComponent: TablePanel, y: String?): Boolean {
    val newY = GuiLocalization.parse(y)?.toFloat()

    if (y != null && newY == null) {
        setColorToTablePanelLabel(
            parentComponent,
            listOf(OrganizationPanel.UI_LOCATION_Y),
            Color.RED
        )
        return false
    }

    setColorToTablePanelLabel(
        parentComponent,
        listOf(OrganizationPanel.UI_LOCATION_Y),
        Color.WHITE
    )

    return true
}

suspend fun validateOrganizationLocationZ(parentComponent: TablePanel, z: String?): Boolean {
    val newZ = GuiLocalization.toLong(z)

    if (z != null && newZ == null) {
        setColorToTablePanelLabel(
            parentComponent,
            listOf(OrganizationPanel.UI_LOCATION_Z),
            Color.RED
        )
        return false
    }

    setColorToTablePanelLabel(
        parentComponent,
        listOf(OrganizationPanel.UI_LOCATION_Z),
        Color.WHITE
    )

    return true
}

suspend fun validateLocationInOrganization(parentComponent: TablePanel, organization: Organization): Boolean {
    val location = organization.postalAddress?.town

    if (location != null && !location.allNull()) {
        if (location.x == null) {
            setColorToTablePanelLabel(parentComponent, listOf(OrganizationPanel.UI_LOCATION_X), Color.RED)
            return false
        }

        if (location.y == null) {
            setColorToTablePanelLabel(parentComponent, listOf(OrganizationPanel.UI_LOCATION_Y), Color.RED)
            return false
        }

        if (location.z == null) {
            setColorToTablePanelLabel(parentComponent, listOf(OrganizationPanel.UI_LOCATION_Z), Color.RED)
            return false
        }
    }

    return true
}

