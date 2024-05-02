package ui.lib

import ui.TablePage
import javax.swing.DefaultCellEditor
import javax.swing.JTable
import javax.swing.event.ListSelectionEvent
import javax.swing.table.TableModel

class Table(model: TableModel, private val tablePage: TablePage) : JTable(model) {
    override fun isCellEditable(row: Int, column: Int): Boolean {
        return column != 0 &&
                column != 4 &&
                column != 14 &&
                tablePage.organizationTypedArrayCache?.get(row)?.get(14)
                    ?.toIntOrNull() == tablePage.collection.getCreatorId()
    }

    override fun setValueAt(aValue: Any?, row: Int, column: Int) {
        if ((getValueAt(row, column) as String) == (aValue as String)) {
            return
        }

        val id = (getValueAt(row, 0) as String).toInt()

        val result =
            tablePage.columnValuesSetters[column]?.invoke(
                tablePage.getOrganizationsList().find { it.id == id }!!,
                aValue.toString()
            ) as Boolean

        if (result) {
            super.setValueAt(aValue, row, column)
            println("Setting value at $row, $column to $aValue")
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

        val sportColumn = columnModel.getColumn(8)
        sportColumn.cellEditor = DefaultCellEditor(tablePage.tablePanel.organizationPanel.typeEditor)
    }
}