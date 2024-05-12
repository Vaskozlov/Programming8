package ui.table.panels

import application.exceptionToMessage
import collection.*
import kotlinx.coroutines.launch
import lib.ExecutionStatus
import lib.valueOrNull
import ui.lib.*
import java.awt.Color
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

    val uiElements = mapOf<String, Pair<JLabel, JComponent>>(
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
                    getTextOfElement(UI_LOCATION_Z),
                    getTextOfElement(UI_LOCATION_NAME)
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
                    getTextOfElement(UI_LOCATION_Z),
                    getTextOfElement(UI_LOCATION_NAME)
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
                    it.text,
                    getTextOfElement(UI_LOCATION_NAME)
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

    private fun constantFields() = listOf(
        UI_ID,
        UI_CREATION_DATE
    )

    internal fun getOrganizationByIdInUI(): Organization? =
        parent.tablePage.getOrganizationById(GuiLocalization.toInt(getTextOfElement(UI_ID)) ?: -1)

    private fun getTextOfElement(key: String): String? =
        when (val value = uiElements[key]!!.second) {
            is JTextField -> value.text.takeIf { it.isNotBlank() }
            is TypeEditor -> value.selectedItem!!.toString()
            else -> throw Error()
        }

    private fun setTextOfElement(key: String, value: String?) {
        uiElements[key]!!.first.text = GuiLocalization.get(key)

        when (val element = uiElements[key]!!.second) {
            is JTextField -> element.text = value
            is TypeEditor -> element.setTextNoUpdate(value)
            else -> throw Error()
        }
    }

    fun loadOrganization(organization: Array<String?>) {
        setTextOfElement(UI_ID, organization[Table.ID_COLUMN])
        setTextOfElement(UI_NAME, organization[Table.NAME_COLUMN])
        setTextOfElement(UI_COORDINATE_X, organization[Table.COORDINATE_X_COLUMN])
        setTextOfElement(UI_COORDINATE_Y, organization[Table.COORDINATE_Y_COLUMN])
        setTextOfElement(UI_CREATION_DATE, organization[Table.CREATION_DATE_COLUMN])
        setTextOfElement(UI_ANNUAL_TURNOVER, organization[Table.ANNUAL_TURNOVER_COLUMN])
        setTextOfElement(UI_FULL_NAME, organization[Table.FULL_NAME_COLUMN])
        setTextOfElement(UI_EMPLOYEES_COUNT, organization[Table.EMPLOYEES_COUNT_COLUMN])
        setTextOfElement(UI_TYPE, organization[Table.TYPE_COLUMN])
        setTextOfElement(UI_ZIP_CODE, organization[Table.ZIP_CODE_COLUMN])
        setTextOfElement(UI_LOCATION_X, organization[Table.LOCATION_X_COLUMN])
        setTextOfElement(UI_LOCATION_Y, organization[Table.LOCATION_Y_COLUMN])
        setTextOfElement(UI_LOCATION_Z, organization[Table.LOCATION_Z_COLUMN])
        setTextOfElement(UI_LOCATION_NAME, organization[Table.LOCATION_NAME_COLUMN])

        val isEditable =
            GuiLocalization.toInt(organization[Table.CREATOR_ID_COLUMN]) == parent.tablePage.getUserId()

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

        tableViewScope.launch {
            setColorToTablePanelLabel(
                parent,
                listOf(
                    UI_NAME,
                    UI_COORDINATE_X,
                    UI_COORDINATE_Y,
                    UI_ANNUAL_TURNOVER,
                    UI_FULL_NAME,
                    UI_EMPLOYEES_COUNT,
                    UI_TYPE,
                    UI_ZIP_CODE,
                    UI_LOCATION_X,
                    UI_LOCATION_Y,
                    UI_LOCATION_Z,
                    UI_LOCATION_NAME
                ),
                Color.WHITE
            )
        }
    }

    fun clearFields() {
        uiElements.forEach { (name, elem) ->
            val (_, component) = elem

            when (component) {
                is JTextField -> {
                    component.text = ""

                    constantFields().contains(name).takeUnless { it }?.let {
                        component.isEnabled = true
                    }
                }

                is TypeEditor -> {
                    component.setTextNoUpdate("null")

                    constantFields().contains(name).takeUnless { it }?.let {
                        component.isEnabled = true
                    }
                }
            }
        }
    }

    fun getOrganizationAddress(): Address? {
        if (getTextOfElement(UI_ID).isNullOrBlank()) {
            return null
        }

        return Address(
            getTextOfElement(UI_ZIP_CODE).takeUnless { isNullString(it) },
            Location(
                GuiLocalization.toDouble(getTextOfElement(UI_LOCATION_X)),
                GuiLocalization.toFloat(getTextOfElement(UI_LOCATION_Y)),
                GuiLocalization.toLong(getTextOfElement(UI_LOCATION_Z)),
                getTextOfElement(UI_LOCATION_NAME).takeUnless { isNullString(it) }
            )
        )
    }

    private fun constructCoordinates() = Coordinates(
        GuiLocalization.toLong(getTextOfElement(UI_COORDINATE_X)),
        GuiLocalization.toLong(getTextOfElement(UI_COORDINATE_Y))
    )

    private suspend fun checkOrganizationFieldsCorrectness(): ExecutionStatus {
        val result = mutableListOf(
            validateOrganizationName(parent, getTextOfElement(UI_NAME)),
            validateOrganizationCoordinateX(parent, getTextOfElement(UI_COORDINATE_X)),
            validateOrganizationCoordinateY(parent, getTextOfElement(UI_COORDINATE_Y)),
            validateOrganizationAnnualTurnover(parent, getTextOfElement(UI_ANNUAL_TURNOVER)),
            validateOrganizationFullName(parent, getTextOfElement(UI_FULL_NAME)),
            validateOrganizationEmployeesCount(parent, getTextOfElement(UI_EMPLOYEES_COUNT)),
            validateOrganizationZipCode(parent, getTextOfElement(UI_ZIP_CODE)),
        )

        val locationX = getTextOfElement(UI_LOCATION_X)
        val locationY = getTextOfElement(UI_LOCATION_Y)
        val locationZ = getTextOfElement(UI_LOCATION_Z)

        if (!isNullString(locationX) || !isNullString(locationY) || !isNullString(locationZ)) {
            result.add(validateOrganizationLocationX(parent, locationX))
            result.add(validateOrganizationLocationY(parent, locationY))
            result.add(validateOrganizationLocationZ(parent, locationZ))
        }

        if (result.any { !it }) {
            return ExecutionStatus.FAILURE
        }

        return ExecutionStatus.SUCCESS
    }

    suspend fun getOrganization(): Organization? {
        if (checkOrganizationFieldsCorrectness() == ExecutionStatus.FAILURE) {
            return null
        }

        val organization = Organization(
            id = GuiLocalization.toInt(getTextOfElement(UI_ID)),
            name = getTextOfElement(UI_NAME),
            coordinates = constructCoordinates(),
            annualTurnover = GuiLocalization.toDouble(getTextOfElement(UI_ANNUAL_TURNOVER)),
            fullName = getTextOfElement(UI_FULL_NAME),
            employeesCount = GuiLocalization.toInt(getTextOfElement(UI_EMPLOYEES_COUNT)),
            type = valueOrNull<OrganizationType>(getTextOfElement(UI_TYPE)),
            postalAddress = getOrganizationAddress()
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
        uiElements.forEach { (key, value) ->
            parent.add(value.first)
            parent.add(value.second, "wrap")

            val (label, _) = value
            GuiLocalization.addElement(key, label)
        }
    }
}