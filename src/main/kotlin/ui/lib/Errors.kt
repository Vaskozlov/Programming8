package ui.lib

import collection.Organization
import lib.Localization
import java.awt.Component
import javax.swing.JOptionPane

private fun nullTest(value: String?): Boolean {
    return value.isNullOrBlank() || value == "null"
}

fun validateOrganizationName(parentComponent: Component, name: String?): Boolean {
    if (nullTest(name)) {
        JOptionPane.showMessageDialog(parentComponent, Localization.get("ui.validate.invalid_name"))
        return false
    }

    return true
}

fun validateOrganizationCoordinateX(parentComponent: Component, x: String?): Boolean {
    val newX = x?.toLongOrNull()

    if (newX == null) {
        JOptionPane.showMessageDialog(parentComponent, Localization.get("ui.validate.invalid_coordinate_x"))
        return false
    }

    return true
}

fun validateOrganizationCoordinateY(parentComponent: Component, y: String?): Boolean {
    val newY = y?.toDoubleOrNull()

    if (newY == null) {
        JOptionPane.showMessageDialog(parentComponent, Localization.get("ui.validate.invalid_coordinate_y"))
        return false
    }

    return true
}

fun validateOrganizationAnnualTurnover(parentComponent: Component, turnover: String?): Boolean {
    val newTurnover = turnover?.toDoubleOrNull()

    if (newTurnover == null || newTurnover < 0) {
        JOptionPane.showMessageDialog(parentComponent, Localization.get("ui.validate.invalid_annual_turnover"))
        return false
    }

    return true
}

fun validateOrganizationFullName(parentComponent: Component, fullName: String?): Boolean {
    if (fullName.isNullOrBlank()) {
        JOptionPane.showMessageDialog(parentComponent, Localization.get("ui.validate.invalid_full_name"))
        return false
    }

    return true
}

fun validateOrganizationEmployeesCount(parentComponent: Component, employeesCount: String?): Boolean {
    val newEmployeesCount = employeesCount?.toIntOrNull()

    if (newEmployeesCount != null && newEmployeesCount < 0) {
        JOptionPane.showMessageDialog(parentComponent, Localization.get("ui.validate.invalid_employees_count"))
        return false
    }

    return true
}

fun validateOrganizationZipCode(parentComponent: Component, zipCode: String?): Boolean {
    if (!nullTest(zipCode) && (zipCode!!.length < 3 || zipCode.toLongOrNull() == null)) {
        JOptionPane.showMessageDialog(parentComponent, Localization.get("ui.validate.invalid_zip_code"))
        return false
    }

    return true
}

fun validateOrganizationLocationX(parentComponent: Component, x: String?): Boolean {
    val newX = x?.toDoubleOrNull()

    if (x != null && newX == null) {
        JOptionPane.showMessageDialog(parentComponent, Localization.get("ui.validate.invalid_location_x"))
        return false
    }

    return true
}

fun validateOrganizationLocationY(parentComponent: Component, y: String?): Boolean {
    val newY = y?.toFloatOrNull()

    if (y != null && newY == null) {
        JOptionPane.showMessageDialog(parentComponent, Localization.get("ui.validate.invalid_location_y"))
        return false
    }

    return true
}

fun validateOrganizationLocationZ(parentComponent: Component, z: String?): Boolean {
    val newZ = z?.toLongOrNull()

    if (z != null && newZ == null) {
        JOptionPane.showMessageDialog(parentComponent, Localization.get("ui.validate.invalid_location_z"))
        return false
    }

    return true
}

fun validateLocationInOrganization(parentComponent: Component, organization: Organization): Boolean {
    val location = organization.postalAddress?.town

    if (location != null && !location.allNull()) {
        if (location.x == null) {
            JOptionPane.showMessageDialog(parentComponent, Localization.get("ui.validate.null_location_x"))
            return false
        }

        if (location.y == null) {
            JOptionPane.showMessageDialog(parentComponent, Localization.get("ui.validate.null_location_y"))
            return false
        }

        if (location.z == null) {
            JOptionPane.showMessageDialog(parentComponent, Localization.get("ui.validate.null_location_z"))
            return false
        }
    }

    return true
}

