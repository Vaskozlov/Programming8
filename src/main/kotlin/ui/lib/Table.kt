package ui.lib

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.swing.Swing
import kotlinx.coroutines.withContext
import ui.table.TablePageWithOrganizationPanels
import javax.swing.DefaultCellEditor
import javax.swing.JTable
import javax.swing.ListSelectionModel.SINGLE_SELECTION
import javax.swing.event.ListSelectionEvent
import javax.swing.table.TableModel

class Table(model: TableModel, private val tablePage: TablePageWithOrganizationPanels) : JTable(model) {
    companion object {
        const val ORGANIZATION_ID_COLUMN = 0
        const val ORGANIZATION_NAME_COLUMN = 1
        const val ORGANIZATION_COORDINATE_X_COLUMN = 2
        const val ORGANIZATION_COORDINATE_Y_COLUMN = 3
        const val ORGANIZATION_CREATION_DATE_COLUMN = 4
        const val ORGANIZATION_ANNUAL_TURNOVER_COLUMN = 5
        const val ORGANIZATION_FULL_NAME_COLUMN = 6
        const val ORGANIZATION_EMPLOYEES_COUNT_COLUMN = 7
        const val ORGANIZATION_TYPE_COLUMN = 8
        const val ORGANIZATION_ZIP_CODE_COLUMN = 9
        const val ORGANIZATION_LOCATION_X_COLUMN = 10
        const val ORGANIZATION_LOCATION_Y_COLUMN = 11
        const val ORGANIZATION_LOCATION_Z_COLUMN = 12
        const val ORGANIZATION_LOCATION_NAME_COLUMN = 13
        const val ORGANIZATION_CREATOR_ID_COLUMN = 14
    }

    private fun doesRowBelongToUser(row: Int): Boolean {
        return tablePage.getOrganizationByRow(row)?.creatorId == tablePage.getUserId()
    }

    override fun isCellEditable(row: Int, column: Int): Boolean {
        return column != ORGANIZATION_ID_COLUMN &&
                column != ORGANIZATION_CREATION_DATE_COLUMN &&
                column != ORGANIZATION_CREATOR_ID_COLUMN &&
                doesRowBelongToUser(row)
    }

    override fun setValueAt(aValue: Any?, row: Int, column: Int) {
        if ((getValueAt(row, column) as String?) == (aValue as String)) {
            return
        }

        val id = (getValueAt(row, ORGANIZATION_ID_COLUMN) as String).toInt()

        tablePage.tableViewScope.launch {
            val result =
                tablePage.columnValuesSetters[column]?.invoke(
                    tablePage.getOrganizationById(id)!!,
                    aValue.toString()
                ) as Boolean

            if (result) {
                withContext(Dispatchers.Swing) {
                    super.setValueAt(aValue, row, column)
                }
            }
        }
    }

    override fun valueChanged(e: ListSelectionEvent) {
        super.valueChanged(e)
        tablePage.selectOrganization(selectedRow)
    }

    init {
        addMouseListener(
            mouseClickAdapter {
                if (it.clickCount == 2) {
                    val row = rowAtPoint(it.point)
                    tablePage.selectOrganization(row)
                }
            }
        )

        addKeyListener(
            keyboardKeyReleasedAdapter {
                if (it.keyCode == 127 || (it.keyCode == 8 && it.isShiftDown)) {
                    val row = selectedRow
                    val id = (getValueAt(row, ORGANIZATION_ID_COLUMN) as String).toInt()
                    tablePage.removeById(id)
                }
            }
        )

        tableHeader.addMouseListener(
            mouseClickAdapter {
                if (it.clickCount == 2) {
                    val column = columnAtPoint(it.point)
                    val columnName = columnModel.getColumn(column).headerValue as String
                    tablePage.addFilter(columnName)
                }
            }
        )

        tableHeader.table.rowHeight = 30
        tableHeader.reorderingAllowed = false

        val sportColumn = columnModel.getColumn(ORGANIZATION_TYPE_COLUMN)
        sportColumn.cellEditor = DefaultCellEditor(tablePage.tablePanel.organizationPanel.typeEditor)

        setSelectionMode(SINGLE_SELECTION)
    }
}