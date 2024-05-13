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
        const val ID_COLUMN = 0
        const val NAME_COLUMN = 1
        const val COORDINATE_X_COLUMN = 2
        const val COORDINATE_Y_COLUMN = 3
        const val CREATION_DATE_COLUMN = 4
        const val ANNUAL_TURNOVER_COLUMN = 5
        const val FULL_NAME_COLUMN = 6
        const val EMPLOYEES_COUNT_COLUMN = 7
        const val TYPE_COLUMN = 8
        const val ZIP_CODE_COLUMN = 9
        const val LOCATION_X_COLUMN = 10
        const val LOCATION_Y_COLUMN = 11
        const val LOCATION_Z_COLUMN = 12
        const val LOCATION_NAME_COLUMN = 13
        const val CREATOR_NAME_COLUMN = 14
        const val CREATOR_ID_COLUMN = 15

        const val DELETE_KEY_CODE = 127
        const val BACKSPACE_KEY_CODE = 8
    }

    private fun doesRowBelongToUser(row: Int): Boolean {
        return tablePage.getOrganizationByRow(row)?.creatorId == tablePage.getUserId()
    }

    override fun isCellEditable(row: Int, column: Int): Boolean {
        return column != ID_COLUMN &&
                column != CREATION_DATE_COLUMN &&
                column != CREATOR_ID_COLUMN &&
                column != CREATOR_NAME_COLUMN &&
                doesRowBelongToUser(row)
    }

    override fun setValueAt(aValue: Any?, row: Int, column: Int) {
        if ((getValueAt(row, column) as String?) == (aValue as String)) {
            return
        }

        val id = tablePage.getIdByRow(row)

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

    fun hideCreatorIdColumn() {
        if (columnModel.columnCount == CREATOR_ID_COLUMN) {
            return
        }

        //columnModel.removeColumn(columnModel.getColumn(CREATOR_ID_COLUMN))
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
                if (it.keyCode == DELETE_KEY_CODE || (it.keyCode == BACKSPACE_KEY_CODE && it.isShiftDown)) {
                    val row = selectedRow
                    val id = tablePage.getIdByRow(row)
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

        tableHeader.table.rowHeight = calculateFontSize(30)
        tableHeader.reorderingAllowed = false

        val sportColumn = columnModel.getColumn(TYPE_COLUMN)
        sportColumn.cellEditor = DefaultCellEditor(tablePage.tablePanel.organizationPanel.typeEditor)

        setSelectionMode(SINGLE_SELECTION)
    }
}