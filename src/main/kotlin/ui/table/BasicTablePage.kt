package ui.table

import collection.CollectionInterface
import kotlinx.coroutines.*
import ui.lib.Table
import ui.lib.showMessageDialog
import javax.swing.JFrame
import javax.swing.JTable
import javax.swing.table.DefaultTableModel
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

abstract class BasicTablePage : JFrame() {
    companion object {
        val columnNames = arrayOf(
            "ID",
            "Name",
            "Coordinate x",
            "Coordinate y",
            "Creation date",
            "Annual turnover",
            "Full name",
            "Employees count",
            "Type",
            "Zip code",
            "Location x",
            "Location y",
            "Location z",
            "Location name",
            "Creator id"
        )
    }

    val tableViewScope = CoroutineScope(Dispatchers.Default)

    abstract fun getOrganizationsAsArrayOfStrings(): Array<Array<String?>>

    protected lateinit var tableModel: DefaultTableModel

    protected lateinit var table : Table

    abstract fun requestReload()
    abstract fun reload(requestFullReload: Boolean): Job

    protected fun getColumnIndex(table: JTable, header: String): Int {
        for (i in 0 until table.columnCount) {
            if (table.getColumnName(i) == header) return i
        }
        return -1
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

    init {
        defaultCloseOperation = EXIT_ON_CLOSE
    }
}