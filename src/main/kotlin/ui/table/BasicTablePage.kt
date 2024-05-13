package ui.table

import kotlinx.coroutines.*
import ui.lib.GuiLocalization
import ui.lib.Table
import ui.lib.showMessageDialog
import javax.swing.JFrame
import javax.swing.JTable
import javax.swing.table.DefaultTableModel
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

abstract class BasicTablePage : JFrame() {
    val tableViewScope = CoroutineScope(Dispatchers.Default)
    protected lateinit var tableModel: DefaultTableModel
    protected lateinit var table: Table

    var columnNames = Array(16) { "" }
        private set

    abstract fun getOrganizationsAsArrayOfStrings(): Array<Array<String?>>
    abstract fun requestReload()
    abstract fun reload(requestFullReload: Boolean): Job

    protected fun getColumnIndex(table: JTable, columnName: String): Int {
        for (i in 0 until table.columnCount) {
            if (table.getColumnName(i) == columnName) return i
        }
        return -1
    }

    fun getIdByRow(row: Int): Int {
        return GuiLocalization.toInt(table.getValueAt(row, Table.ID_COLUMN) as String)!!
    }

    fun executeCatching(
        scope: CoroutineScope = tableViewScope,
        context: CoroutineContext = EmptyCoroutineContext,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        action: suspend () -> Unit
    ) = scope.launch(context, start) {
        runCatching {
            action()
        }.onFailure {
            showMessageDialog(this@BasicTablePage, it)
        }
    }

    private fun localizeTableHeader() {
        columnNames[Table.ID_COLUMN] = GuiLocalization.get("ui.table.header.id")
        columnNames[Table.NAME_COLUMN] = GuiLocalization.get("ui.table.header.name")
        columnNames[Table.COORDINATE_X_COLUMN] =
            GuiLocalization.get("ui.table.header.coordinate_x")
        columnNames[Table.COORDINATE_Y_COLUMN] =
            GuiLocalization.get("ui.table.header.coordinate_y")
        columnNames[Table.CREATION_DATE_COLUMN] =
            GuiLocalization.get("ui.table.header.creation_date")
        columnNames[Table.ANNUAL_TURNOVER_COLUMN] =
            GuiLocalization.get("ui.table.header.annual_turnover")
        columnNames[Table.FULL_NAME_COLUMN] = GuiLocalization.get("ui.table.header.full_name")
        columnNames[Table.EMPLOYEES_COUNT_COLUMN] =
            GuiLocalization.get("ui.table.header.employees_count")
        columnNames[Table.TYPE_COLUMN] = GuiLocalization.get("ui.table.header.type")
        columnNames[Table.ZIP_CODE_COLUMN] = GuiLocalization.get("ui.table.header.zip_code")
        columnNames[Table.LOCATION_X_COLUMN] = GuiLocalization.get("ui.table.header.location_x")
        columnNames[Table.LOCATION_Y_COLUMN] = GuiLocalization.get("ui.table.header.location_y")
        columnNames[Table.LOCATION_Z_COLUMN] = GuiLocalization.get("ui.table.header.location_z")
        columnNames[Table.LOCATION_NAME_COLUMN] =
            GuiLocalization.get("ui.table.header.location_name")
        columnNames[Table.CREATOR_NAME_COLUMN] = GuiLocalization.get("ui.table.header.user")
        columnNames[Table.CREATOR_ID_COLUMN] = GuiLocalization.get("ui.table.header.creator_id")
        requestReload()
    }

    init {
        defaultCloseOperation = EXIT_ON_CLOSE
        GuiLocalization.addActionAfter { localizeTableHeader() }
    }
}