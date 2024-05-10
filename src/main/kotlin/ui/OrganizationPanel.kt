package ui

import application.exceptionToMessage
import collection.*
import lib.Localization
import lib.valueOrNull
import ui.lib.*
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JOptionPane
import javax.swing.JTextField

class OrganizationPanel(internal val parent: TablePanel) {
    companion object {
        const val ENTER_KEYCODE = 10
        const val UI_ID = "ui.ID"
        const val UI_NAME = "ui.name"
        const val UI_COORDINATE_X = "ui.coordinate_x"
        const val UI_COORDINATE_Y = "ui.coordinate_y"
        const val UI_CREATION_DATE = "ui.creation_date"
        const val UI_ANNUAL_TURNOVER = "ui.annual_turnover"
        const val UI_FULL_NAME = "ui.full_name"
        const val UI_EMPLOYEES_COUNT = "ui.employees_count"
        const val UI_TYPE = "ui.type"
        const val UI_ZIP_CODE = "ui.zip_code"
        const val UI_LOCATION_X = "ui.location_x"
        const val UI_LOCATION_Y = "ui.location_y"
        const val UI_LOCATION_Z = "ui.location_z"
        const val UI_LOCATION_NAME = "ui.location_name"
    }

    val typeEditor = TypeEditor(this)

    private val uiElements = mapOf<String, Pair<JLabel, JComponent>>(
        UI_ID to (JLabel() to object : JTextField() {
            init {
                isEnabled = false
            }
        }),
        UI_NAME to (JLabel() to getTextFieldWithKeyListener(ENTER_KEYCODE) {
            getOrganizationByIdInUI()?.let { org ->
                parent.setOrgName(org, it.text)
            }
        }),
        UI_COORDINATE_X to (JLabel() to getTextFieldWithKeyListener(ENTER_KEYCODE)
        {
            getOrganizationByIdInUI()?.let { org ->
                parent.setCoordinateX(org, it.text)
            }
        }),
        UI_COORDINATE_Y to (JLabel() to getTextFieldWithKeyListener(ENTER_KEYCODE)
        {
            getOrganizationByIdInUI()?.let { org ->
                parent.setCoordinateY(org, it.text)
            }
        }),
        UI_CREATION_DATE to (JLabel() to object : JTextField() {
            init {
                isEnabled = false
            }
        }),
        UI_ANNUAL_TURNOVER to (JLabel() to getTextFieldWithKeyListener(ENTER_KEYCODE)
        {
            getOrganizationByIdInUI()?.let { org ->
                parent.setAnnualTurnover(org, it.text)
            }
        }),
        UI_FULL_NAME to (JLabel() to getTextFieldWithKeyListener(ENTER_KEYCODE)
        {
            getOrganizationByIdInUI()?.let { org ->
                parent.setFullName(org, it.text)
            }
        }),
        UI_EMPLOYEES_COUNT to (JLabel() to getTextFieldWithKeyListener(ENTER_KEYCODE)
        {
            getOrganizationByIdInUI()?.let { org ->
                parent.setEmployeesCount(org, it.text)
            }
        }),
        UI_TYPE to (JLabel() to typeEditor),
        UI_ZIP_CODE to (JLabel() to getTextFieldWithKeyListener(ENTER_KEYCODE)
        {
            getOrganizationByIdInUI()?.let { org ->
                parent.setPostalAddressZipCode(org, it.text)
            }
        }),
        UI_LOCATION_X to (JLabel() to getTextFieldWithKeyListener(ENTER_KEYCODE)
        {
            getOrganizationByIdInUI()?.let { org ->
                parent.setPostalAddressTownLocation(
                    org,
                    it.text,
                    getTextOfElement(UI_LOCATION_Y),
                    getTextOfElement(UI_LOCATION_Z)
                )
            }
        }),
        UI_LOCATION_Y to (JLabel() to getTextFieldWithKeyListener(ENTER_KEYCODE)
        {
            getOrganizationByIdInUI()?.let { org ->
                parent.setPostalAddressTownLocation(
                    org,
                    getTextOfElement(UI_LOCATION_X),
                    it.text,
                    getTextOfElement(UI_LOCATION_Z)
                )
            }
        }),
        UI_LOCATION_Z to (JLabel() to getTextFieldWithKeyListener(ENTER_KEYCODE)
        {
            getOrganizationByIdInUI()?.let { org ->
                parent.setPostalAddressTownLocation(
                    org,
                    getTextOfElement(UI_LOCATION_X),
                    getTextOfElement(UI_LOCATION_Y),
                    it.text
                )
            }
        }),
        UI_LOCATION_NAME to (JLabel() to getTextFieldWithKeyListener(ENTER_KEYCODE)
        {
            getOrganizationByIdInUI()?.let { org ->
                parent.setPostalAddressTownName(org, it.text)
            }
        }),
    )

    internal fun getOrganizationByIdInUI(): Organization? =
        parent.tablePage.getOrganizationById(getTextOfElement(UI_ID)?.toIntOrNull() ?: -1)

    private fun getTextOfElement(key: String): String? =
        when (val value = uiElements[key]!!.second) {
            is JTextField -> value.text.takeIf { it.isNotBlank() }
            is TypeEditor -> value.selectedItem!!.toString()
            else -> throw Error()
        }

    private fun setTextOfElement(key: String, value: String?) {
        uiElements[key]!!.first.text = Localization.get(key)

        when (val element = uiElements[key]!!.second) {
            is JTextField -> element.text = value
            is TypeEditor -> element.setTextNoUpdate(value)
            else -> throw Error()
        }
    }

    fun loadOrganization(organization: Array<String?>) {
        setTextOfElement("ui.ID", organization[Table.ORGANIZATION_ID_COLUMN])
        setTextOfElement("ui.name", organization[Table.ORGANIZATION_NAME_COLUMN])
        setTextOfElement("ui.coordinate_x", organization[Table.ORGANIZATION_COORDINATE_X_COLUMN])
        setTextOfElement("ui.coordinate_y", organization[Table.ORGANIZATION_COORDINATE_Y_COLUMN])
        setTextOfElement("ui.creation_date", organization[Table.ORGANIZATION_CREATION_DATE_COLUMN])
        setTextOfElement("ui.annual_turnover", organization[Table.ORGANIZATION_ANNUAL_TURNOVER_COLUMN])
        setTextOfElement("ui.full_name", organization[Table.ORGANIZATION_FULL_NAME_COLUMN])
        setTextOfElement("ui.employees_count", organization[Table.ORGANIZATION_EMPLOYEES_COUNT_COLUMN])
        setTextOfElement("ui.type", organization[Table.ORGANIZATION_TYPE_COLUMN])
        setTextOfElement("ui.zip_code", organization[Table.ORGANIZATION_ZIP_CODE_COLUMN])
        setTextOfElement("ui.location_x", organization[Table.ORGANIZATION_LOCATION_X_COLUMN])
        setTextOfElement("ui.location_y", organization[Table.ORGANIZATION_LOCATION_Y_COLUMN])
        setTextOfElement("ui.location_z", organization[Table.ORGANIZATION_LOCATION_Z_COLUMN])
        setTextOfElement("ui.location_name", organization[Table.ORGANIZATION_CREATOR_ID_COLUMN])

        val isEditable =
            organization[Table.ORGANIZATION_CREATOR_ID_COLUMN]?.toIntOrNull() == parent.tablePage.getUserId()

        uiElements["ui.name"]!!.second.isEnabled = isEditable
        uiElements["ui.coordinate_x"]!!.second.isEnabled = isEditable
        uiElements["ui.coordinate_y"]!!.second.isEnabled = isEditable
        uiElements["ui.annual_turnover"]!!.second.isEnabled = isEditable
        uiElements["ui.full_name"]!!.second.isEnabled = isEditable
        uiElements["ui.employees_count"]!!.second.isEnabled = isEditable
        uiElements["ui.type"]!!.second.isEnabled = isEditable
        uiElements["ui.zip_code"]!!.second.isEnabled = isEditable
        uiElements["ui.location_x"]!!.second.isEnabled = isEditable
        uiElements["ui.location_y"]!!.second.isEnabled = isEditable
        uiElements["ui.location_z"]!!.second.isEnabled = isEditable
        uiElements["ui.location_name"]!!.second.isEnabled = isEditable
    }

    fun localize() {
        uiElements.forEach { (key, elem) ->
            val (label, _) = elem
            label.text = Localization.get(key)
        }
    }

    fun clearFields() {
        uiElements.forEach { (_, elem) ->
            val (_, component) = elem

            when (component) {
                is JTextField -> component.text = ""
                is TypeEditor -> component.setTextNoUpdate("null")
            }
        }
    }

    fun getOrganization(): Organization? {
        var result = validateOrganizationName(parent, getTextOfElement(UI_NAME))

        result = result && validateOrganizationCoordinateX(parent, getTextOfElement(UI_COORDINATE_X))
        result = result && validateOrganizationCoordinateY(parent, getTextOfElement(UI_COORDINATE_Y))
        result = result && validateOrganizationAnnualTurnover(parent, getTextOfElement(UI_ANNUAL_TURNOVER))
        result = result && validateOrganizationFullName(parent, getTextOfElement(UI_FULL_NAME))
        result = result && validateOrganizationEmployeesCount(parent, getTextOfElement(UI_EMPLOYEES_COUNT))
        result = result && validateOrganizationZipCode(parent, getTextOfElement(UI_ZIP_CODE))
        result = result && validateOrganizationLocationX(parent, getTextOfElement(UI_LOCATION_X))
        result = result && validateOrganizationLocationY(parent, getTextOfElement(UI_LOCATION_Y))
        result = result && validateOrganizationLocationZ(parent, getTextOfElement(UI_LOCATION_Z))

        if (!result) {
            return null
        }

        val organization = Organization(
            id = null,
            name = getTextOfElement(UI_NAME),
            coordinates = Coordinates(
                getTextOfElement(UI_COORDINATE_X)?.toLongOrNull(),
                getTextOfElement(UI_COORDINATE_Y)?.toLongOrNull()
            ),
            annualTurnover = getTextOfElement(UI_ANNUAL_TURNOVER)?.toDoubleOrNull(),
            fullName = getTextOfElement(UI_FULL_NAME),
            employeesCount = getTextOfElement(UI_EMPLOYEES_COUNT)?.toIntOrNull(),
            type = valueOrNull<OrganizationType>(getTextOfElement(UI_TYPE)),
            postalAddress = Address(
                getTextOfElement(UI_ZIP_CODE),
                Location(
                    getTextOfElement(UI_LOCATION_X)?.toDoubleOrNull(),
                    getTextOfElement(UI_LOCATION_Y)?.toFloatOrNull(),
                    getTextOfElement(UI_LOCATION_Z)?.toLongOrNull(),
                    getTextOfElement(UI_LOCATION_NAME)
                )
            ),
        )

        if (!validateLocationInOrganization(parent, organization)) {
            return null
        }

        organization.optimize()

        organization.runCatching {
            validate()
        }.onFailure {
            JOptionPane.showMessageDialog(parent, exceptionToMessage(it))
        }

        return organization
    }

    fun init() {
        uiElements.forEach { (_, value) ->
            parent.add(value.first)
            parent.add(value.second, "wrap")
        }
    }
}