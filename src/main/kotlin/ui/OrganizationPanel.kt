package ui

import lib.Localization
import javax.swing.JComboBox
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextField

class OrganizationPanel(private val parent: TablePanel) {
    val typeEditor = object : JComboBox<String>() {
        init {
            addItem("COMMERCIAL")
            addItem("PUBLIC")
            addItem("PRIVATE_LIMITED_COMPANY")
            addItem("OPEN_JOINT_STOCK_COMPANY")
            addItem("null")
        }
    }

    private val uiElements = mapOf<String, Pair<JLabel, Any>>(
        "ui.ID" to (JLabel() to JTextField(30)),
        "ui.name" to (JLabel() to JTextField(30)),
        "ui.coordinate_x" to (JLabel() to JTextField(30)),
        "ui.coordinate_y" to (JLabel() to JTextField(30)),
        "ui.creation_date" to (JLabel() to JTextField(30)),
        "ui.annual_turnover" to (JLabel() to JTextField(30)),
        "ui.full_name" to (JLabel() to JTextField(30)),
        "ui.employees_count" to (JLabel() to JTextField(30)),
        "ui.type" to (JLabel() to typeEditor),
        "ui.zip_code" to (JLabel() to JTextField(30)),
        "ui.location_x" to (JLabel() to JTextField(30)),
        "ui.location_y" to (JLabel() to JTextField(30)),
        "ui.location_z" to (JLabel() to JTextField(30)),
        "ui.location_name" to (JLabel() to JTextField(30)),
    )

    private fun getTextOfElement(key: String): String =
        when (val value = uiElements[key]!!.second) {
            is JTextField -> value.text
            is JComboBox<*> -> value.selectedItem!!.toString()
            else -> throw Error()
        }

    private fun setTextOfElement(key: String, value: String?) {
        uiElements[key]!!.first.text = Localization.get(key)

        when (val element = uiElements[key]!!.second) {
            is JTextField -> element.text = value
            is JComboBox<*> -> element.selectedItem = value
            else -> throw Error()
        }
    }

    fun loadOrganization(organization: Array<String?>) {
        setTextOfElement("ui.ID", organization[0])
        setTextOfElement("ui.name", organization[1])
        setTextOfElement("ui.coordinate_x", organization[2])
        setTextOfElement("ui.coordinate_y", organization[3])
        setTextOfElement("ui.creation_date", organization[4])
        setTextOfElement("ui.annual_turnover", organization[5])
        setTextOfElement("ui.full_name", organization[6])
        setTextOfElement("ui.employees_count", organization[7])
        setTextOfElement("ui.type", organization[8])
        setTextOfElement("ui.zip_code", organization[9])
        setTextOfElement("ui.location_x", organization[10])
        setTextOfElement("ui.location_y", organization[11])
        setTextOfElement("ui.location_z", organization[12])
        setTextOfElement("ui.location_name", organization[13])
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

            when (val elem = value.second) {
                is JTextField -> parent.add(elem, "wrap")
                is JComboBox<*> -> parent.add(elem, "wrap")
                else -> throw Error()
            }
        }
    }
}