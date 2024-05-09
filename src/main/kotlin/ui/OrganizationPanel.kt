package ui

import collection.Organization
import lib.Localization
import ui.lib.Table
import ui.lib.TypeEditor
import ui.lib.getTextFieldWithKeyListener
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JTextField

class OrganizationPanel(internal val parent: TablePanel) {
    companion object {
        const val FIELD_LENGTH = 30
        const val ENTER_KEYCODE = 10
    }

    val typeEditor = TypeEditor(this)

    private val uiElements = mapOf<String, Pair<JLabel, JComponent>>(
        "ui.ID" to (JLabel() to object : JTextField(FIELD_LENGTH) {
            init {
                isEnabled = false
            }
        }),
        "ui.name" to (JLabel() to getTextFieldWithKeyListener(FIELD_LENGTH, ENTER_KEYCODE) {
            getOrganizationByIdInUI()?.let { org ->
                parent.setOrgName(org, it.text)
            }
        }),
        "ui.coordinate_x" to (JLabel() to getTextFieldWithKeyListener(FIELD_LENGTH, ENTER_KEYCODE)
        {
            getOrganizationByIdInUI()?.let { org ->
                parent.setCoordinateX(org, it.text)
            }
        }),
        "ui.coordinate_y" to (JLabel() to getTextFieldWithKeyListener(FIELD_LENGTH, ENTER_KEYCODE)
        {
            getOrganizationByIdInUI()?.let { org ->
                parent.setCoordinateY(org, it.text)
            }
        }),
        "ui.creation_date" to (JLabel() to object : JTextField(FIELD_LENGTH) {
            init {
                isEnabled = false
            }
        }),
        "ui.annual_turnover" to (JLabel() to getTextFieldWithKeyListener(FIELD_LENGTH, ENTER_KEYCODE)
        {
            getOrganizationByIdInUI()?.let { org ->
                parent.setAnnualTurnover(org, it.text)
            }
        }),
        "ui.full_name" to (JLabel() to getTextFieldWithKeyListener(FIELD_LENGTH, ENTER_KEYCODE)
        {
            getOrganizationByIdInUI()?.let { org ->
                parent.setFullName(org, it.text)
            }
        }),
        "ui.employees_count" to (JLabel() to getTextFieldWithKeyListener(FIELD_LENGTH, ENTER_KEYCODE)
        {
            getOrganizationByIdInUI()?.let { org ->
                parent.setEmployeesCount(org, it.text)
            }
        }),
        "ui.type" to (JLabel() to typeEditor),
        "ui.zip_code" to (JLabel() to getTextFieldWithKeyListener(FIELD_LENGTH, ENTER_KEYCODE)
        {
            getOrganizationByIdInUI()?.let { org ->
                parent.setPostalAddressZipCode(org, it.text)
            }
        }),
        "ui.location_x" to (JLabel() to getTextFieldWithKeyListener(FIELD_LENGTH, ENTER_KEYCODE)
        {
            getOrganizationByIdInUI()?.let { org ->
                parent.setPostalAddressTownX(org, it.text)
            }
        }),
        "ui.location_y" to (JLabel() to getTextFieldWithKeyListener(FIELD_LENGTH, ENTER_KEYCODE)
        {
            getOrganizationByIdInUI()?.let { org ->
                parent.setPostalAddressTownY(org, it.text)
            }
        }),
        "ui.location_z" to (JLabel() to getTextFieldWithKeyListener(FIELD_LENGTH, ENTER_KEYCODE)
        {
            getOrganizationByIdInUI()?.let { org ->
                parent.setPostalAddressTownZ(org, it.text)
            }
        }),
        "ui.location_name" to (JLabel() to getTextFieldWithKeyListener(FIELD_LENGTH, ENTER_KEYCODE)
        {
            getOrganizationByIdInUI()?.let { org ->
                parent.setPostalAddressTownName(org, it.text)
            }
        }),
    )

    internal fun getOrganizationByIdInUI(): Organization? =
        parent.tablePage.getOrganizationById(getTextOfElement("ui.ID").toIntOrNull() ?: -1)

    private fun getTextOfElement(key: String): String =
        when (val value = uiElements[key]!!.second) {
            is JTextField -> value.text
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
    }

    fun localize() {
        uiElements.forEach { (key, elem) ->
            val (label, _) = elem
            label.text = Localization.get(key)
        }
    }

    fun init() {
        uiElements.forEach { (_, value) ->
            parent.add(value.first)
            parent.add(value.second, "wrap")
        }
    }
}