package ui

import collection.CollectionInterface
import collection.Organization
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.miginfocom.swing.MigLayout
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock
import javax.swing.DefaultCellEditor
import javax.swing.JFrame
import javax.swing.JScrollPane
import javax.swing.JTable
import javax.swing.plaf.FontUIResource
import javax.swing.table.DefaultTableModel
import kotlin.concurrent.withLock


class TablePage(val collection: CollectionInterface) : JFrame() {
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

    private val databaseCommunicationLock = ReentrantLock()
    private val databaseCommunicationLockCondition = databaseCommunicationLock.newCondition()
    val tableViewScope = CoroutineScope(Dispatchers.Default)
    private var tableFilter: Pair<String, Int>? = null
    private var stringFilter: Pair<String, String>? = null
    private val layout = MigLayout(
        "",
        "[fill,70%][fill,30%]",
        "[fill,grow]"
    )
    internal val tablePanel = TablePanel(this)
    internal var organizationTypedArrayCache: Array<Array<String?>>? = null
    private var organizationListCache: List<Organization>? = null

    var tableModel: DefaultTableModel = object : DefaultTableModel(organizationListToArrays(), columnNames) {
        override fun isCellEditable(row: Int, column: Int): Boolean {
            return table.isCellEditable(row, column)
        }
    }

    private val modificationObserver = tableViewScope.launch {
        var lastModificationTime = collection.getLastModificationTime()

        while (true) {
            databaseCommunicationLock.withLock {
                databaseCommunicationLockCondition.await(5, TimeUnit.SECONDS)

                val currentModificationTime = collection.getLastModificationTime()

                if (lastModificationTime != currentModificationTime) {
                    lastModificationTime = currentModificationTime
                    reload()
                }
            }
        }
    }

    val columnValuesSetters = mapOf(
        1 to tablePanel::setOrgName,
        2 to tablePanel::setCoordinateX,
        3 to tablePanel::setCoordinateY,
        5 to tablePanel::setAnnualTurnover,
        6 to tablePanel::setFullName,
        7 to tablePanel::setEmployeesCount,
        8 to tablePanel::setType,
        9 to tablePanel::setPostalAddressZipCode,
        10 to tablePanel::setPostalAddressTownX,
        11 to tablePanel::setPostalAddressTownY,
        12 to tablePanel::setPostalAddressTownZ,
        13 to tablePanel::setPostalAddressTownName
    )

    private val table = ui.widgets.Table(tableModel, this)

    private fun getColumnIndex(table: JTable, header: String): Int {
        for (i in 0 until table.columnCount) {
            if (table.getColumnName(i) == header) return i
        }
        return -1
    }

    internal fun getOrganizationsList(): List<Organization> {
        if (organizationListCache != null) {
            return organizationListCache!!
        }

        organizationListCache = collection.getCollection()
        return organizationListCache!!
    }

    fun requestReload() {
        databaseCommunicationLock.withLock {
            databaseCommunicationLockCondition.signal()
        }
    }

    private fun reload() {
        organizationListCache = null
        organizationTypedArrayCache = null
        tableViewScope.launch {
            runCatching {
                tableModel.setDataVector(organizationListToArrays(), columnNames)
                tableModel.fireTableDataChanged()
                val sportColumn = table.columnModel.getColumn(8)
                sportColumn.cellEditor = DefaultCellEditor(tablePanel.organizationPanel.typeEditor)
            }.onFailure { reload() }
        }
    }

    private fun organizationListToArrays(): Array<Array<String?>> {
        if (organizationTypedArrayCache != null) {
            return organizationTypedArrayCache!!
        }

        var data = getOrganizationsList().map {
            arrayOf(
                it.id.toString(),
                it.name,
                it.coordinates?.x.toString(),
                it.coordinates?.y.toString(),
                it.creationDate.toString(),
                it.annualTurnover.toString(),
                it.fullName,
                it.employeesCount.toString(),
                it.type.toString(),
                it.postalAddress?.zipCode ?: "null",
                it.postalAddress?.town?.x.toString(),
                it.postalAddress?.town?.y.toString(),
                it.postalAddress?.town?.z.toString(),
                it.postalAddress?.town?.name,
                it.creatorId.toString()
            )
        }.toMutableList()

        tableFilter?.let {
            val (columnName, filterId) = it
            val column = getColumnIndex(table, columnName)

            when (filterId % 3) {
                1 -> data.sortBy { it[column] }
                2 -> data.sortByDescending { it[column] }
            }
        }

        stringFilter?.let {
            val (columnName, filter) = stringFilter!!
            val column = getColumnIndex(table, columnName)
            data = data.filter { it[column]?.contains(filter) == true }.toMutableList()
        }

        organizationTypedArrayCache = data.toTypedArray()
        return organizationTypedArrayCache!!
    }

    internal fun addFilter(columnName: String) {
        tableFilter?.let {
            val (prevColumnName, prevFilterId) = it
            tableFilter = columnName to (if (prevColumnName == columnName) prevFilterId + 1 else 1)
        } ?: run {
            tableFilter = columnName to 1
        }

        tableViewScope.launch {
            organizationTypedArrayCache = null
            tableModel.setDataVector(organizationListToArrays(), columnNames)
            tableModel.fireTableDataChanged()
        }
    }

    fun filterChanged() {
        stringFilter = tablePanel.filter
        tableViewScope.launch {
            organizationTypedArrayCache = null
            tableModel.setDataVector(organizationListToArrays(), columnNames)
            tableModel.fireTableDataChanged()
        }
    }

    fun selectOrganization(row: Int) {
        runCatching {
            tablePanel.organizationPanel.loadOrganization(organizationTypedArrayCache!![row])
        }
    }

    init {
        title = "Table"
        defaultCloseOperation = EXIT_ON_CLOSE
        setSize(1200, 600)
        setLayout(layout)

        setUIFont(FontUIResource("Arial", 0, 16))

        table.tableHeader.table.rowHeight = 30
        table.tableHeader.reorderingAllowed = false
        add(JScrollPane(table))
        add(tablePanel)

        tablePanel.localize()
        modificationObserver.start()
        setSize(1200, 600)
    }
}