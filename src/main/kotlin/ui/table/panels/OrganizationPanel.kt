package ui.table.panels

import application.exceptionToMessage
import collection.*
import lib.Localization
import lib.valueOrNull
import ui.lib.*
import javax.swing.JComponent
import javax.swing.JLabel
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

    private val tableViewScope = parent.tablePage.tableViewScope
    val typeEditor = TypeEditor(this)

    private val uiElements = mapOf<String, Pair<JLabel, JComponent>>(
        UI_ID to (JLabel() to object : JTextField() {
            init {
                isEnabled = false
            }
        }),
        UI_NAME to (JLabel() to getTextFieldWithKeyListener(ENTER_KEYCODE, tableViewScope) {
            getOrganizationByIdInUI()?.let { org ->
                parent.setOrgName(org, it.text)
            }
        }),
        UI_COORDINATE_X to (JLabel() to getTextFieldWithKeyListener(ENTER_KEYCODE, tableViewScope)
        {
            getOrganizationByIdInUI()?.let { org ->
                parent.setCoordinateX(org, it.text)
            }
        }),
        UI_COORDINATE_Y to (JLabel() to getTextFieldWithKeyListener(ENTER_KEYCODE, tableViewScope)
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
        UI_ANNUAL_TURNOVER to (JLabel() to getTextFieldWithKeyListener(ENTER_KEYCODE, tableViewScope)
        {
            getOrganizationByIdInUI()?.let { org ->
                parent.setAnnualTurnover(org, it.text)
            }
        }),
        UI_FULL_NAME to (JLabel() to getTextFieldWithKeyListener(ENTER_KEYCODE, tableViewScope)
        {
            getOrganizationByIdInUI()?.let { org ->
                parent.setFullName(org, it.text)
            }
        }),
        UI_EMPLOYEES_COUNT to (JLabel() to getTextFieldWithKeyListener(ENTER_KEYCODE, tableViewScope)
        {
            getOrganizationByIdInUI()?.let { org ->
                parent.setEmployeesCount(org, it.text)
            }
        }),
        UI_TYPE to (JLabel() to typeEditor),
        UI_ZIP_CODE to (JLabel() to getTextFieldWithKeyListener(ENTER_KEYCODE, tableViewScope)
        {
            getOrganizationByIdInUI()?.let { org ->
                parent.setPostalAddressZipCode(org, it.text)
            }
        }),
        UI_LOCATION_X to (JLabel() to getTextFieldWithKeyListener(ENTER_KEYCODE, tableViewScope)
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
        UI_LOCATION_Y to (JLabel() to getTextFieldWithKeyListener(ENTER_KEYCODE, tableViewScope)
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
        UI_LOCATION_Z to (JLabel() to getTextFieldWithKeyListener(ENTER_KEYCODE, tableViewScope)
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
        UI_LOCATION_NAME to (JLabel() to getTextFieldWithKeyListener(ENTER_KEYCODE, tableViewScope)
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
        setTextOfElement(UI_ID, organization[Table.ORGANIZATION_ID_COLUMN])
        setTextOfElement(UI_NAME, organization[Table.ORGANIZATION_NAME_COLUMN])
        setTextOfElement(UI_COORDINATE_X, organization[Table.ORGANIZATION_COORDINATE_X_COLUMN])
        setTextOfElement(UI_COORDINATE_Y, organization[Table.ORGANIZATION_COORDINATE_Y_COLUMN])
        setTextOfElement(UI_CREATION_DATE, organization[Table.ORGANIZATION_CREATION_DATE_COLUMN])
        setTextOfElement(UI_ANNUAL_TURNOVER, organization[Table.ORGANIZATION_ANNUAL_TURNOVER_COLUMN])
        setTextOfElement(UI_FULL_NAME, organization[Table.ORGANIZATION_FULL_NAME_COLUMN])
        setTextOfElement(UI_EMPLOYEES_COUNT, organization[Table.ORGANIZATION_EMPLOYEES_COUNT_COLUMN])
        setTextOfElement(UI_TYPE, organization[Table.ORGANIZATION_TYPE_COLUMN])
        setTextOfElement(UI_ZIP_CODE, organization[Table.ORGANIZATION_ZIP_CODE_COLUMN])
        setTextOfElement(UI_LOCATION_X, organization[Table.ORGANIZATION_LOCATION_X_COLUMN])
        setTextOfElement(UI_LOCATION_Y, organization[Table.ORGANIZATION_LOCATION_Y_COLUMN])
        setTextOfElement(UI_LOCATION_Z, organization[Table.ORGANIZATION_LOCATION_Z_COLUMN])
        setTextOfElement(UI_LOCATION_NAME, organization[Table.ORGANIZATION_CREATOR_ID_COLUMN])

        val isEditable =
            organization[Table.ORGANIZATION_CREATOR_ID_COLUMN]?.toIntOrNull() == parent.tablePage.getUserId()

        uiElements[UI_NAME]!!.second.isEnabled = isEditable
        uiElements[UI_COORDINATE_X]!!.second.isEnabled = isEditable
        uiElements[UI_COORDINATE_Y]!!.second.isEnabled = isEditable
        uiElements[UI_ANNUAL_TURNOVER]!!.second.isEnabled = isEditable
        uiElements[UI_FULL_NAME]!!.second.isEnabled = isEditable
        uiElements[UI_EMPLOYEES_COUNT]!!.second.isEnabled = isEditable
        uiElements[UI_TYPE]!!.second.isEnabled = isEditable
        uiElements[UI_ZIP_CODE]!!.second.isEnabled = isEditable
        uiElements[UI_LOCATION_X]!!.second.isEnabled = isEditable
        uiElements[UI_LOCATION_Y]!!.second.isEnabled = isEditable
        uiElements[UI_LOCATION_Z]!!.second.isEnabled = isEditable
        uiElements[UI_LOCATION_NAME]!!.second.isEnabled = isEditable
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

    suspend fun getOrganization(): Organization? {
        if (getTextOfElement(UI_ID).isNullOrBlank()) {
            return null
        }

        val result = listOf(
            validateOrganizationName(parent, getTextOfElement(UI_NAME)),
            validateOrganizationCoordinateX(parent, getTextOfElement(UI_COORDINATE_X)),
            validateOrganizationCoordinateY(parent, getTextOfElement(UI_COORDINATE_Y)),
            validateOrganizationAnnualTurnover(parent, getTextOfElement(UI_ANNUAL_TURNOVER)),
            validateOrganizationFullName(parent, getTextOfElement(UI_FULL_NAME)),
            validateOrganizationEmployeesCount(parent, getTextOfElement(UI_EMPLOYEES_COUNT)),
            validateOrganizationZipCode(parent, getTextOfElement(UI_ZIP_CODE)),
            validateOrganizationLocationX(parent, getTextOfElement(UI_LOCATION_X)),
            validateOrganizationLocationY(parent, getTextOfElement(UI_LOCATION_Y)),
            validateOrganizationLocationZ(parent, getTextOfElement(UI_LOCATION_Z))
        )

        if (result.any { !it }) {
            return null
        }

        val organization = Organization(
            id = getTextOfElement(UI_ID)?.toIntOrNull(),
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
            showMessageDialog(parent, exceptionToMessage(it))
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