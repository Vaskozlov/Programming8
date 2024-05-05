package ui

import collection.CollectionInterface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.miginfocom.swing.MigLayout
import ui.lib.MigFontLayout
import ui.lib.OrganizationStorage
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock
import javax.swing.*
import javax.swing.table.DefaultTableModel
import kotlin.concurrent.withLock


class TablePage(collection: CollectionInterface) : JFrame() {
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
    private val databaseCommunicationLock = ReentrantLock()
    private val databaseCommunicationLockCondition = databaseCommunicationLock.newCondition()

    private var tableFilter: Pair<String, Int>? = null
        set(value) {
            field = value
            organizationStorage.filterChanged = true
        }

    private var stringFilter: Pair<String, String>? = null
        set(value) {
            field = value
            organizationStorage.filterChanged = true
        }

    private val layout = MigFontLayout(
        "",
        "[fill,70%][fill,30%]",
        "[fill,grow]"
    )
    internal val tablePanel = TablePanel(this)
    private val visualPanel = Visualization(this)

    internal val organizationStorage = OrganizationStorage(collection)
    {
        var result = it

        tableFilter?.let { tFilter ->
            val (columnName, filterId) = tFilter
            val column = getColumnIndex(table, columnName)

            result = when (filterId % 3) {
                1 -> result.sortedBy { it[column] }
                2 -> result.sortedByDescending { it[column] }
                else -> result
            }
        }

        stringFilter?.let {
            val (columnName, filter) = stringFilter!!
            val column = getColumnIndex(table, columnName)
            result = result.filter { result[column].contains(filter) }.toMutableList()
        }

        result
    }

    private var tableModel: DefaultTableModel =
        object : DefaultTableModel(organizationStorage.getFilteredOrganizationAsArrayOfStrings(), columnNames) {
            override fun isCellEditable(row: Int, column: Int): Boolean {
                return table.isCellEditable(row, column)
            }
        }

    private val modificationObserver = tableViewScope.launch {
        var lastModificationTime = collection.getLastModificationTime()

        while (true) {
            databaseCommunicationLock.withLock {
                databaseCommunicationLockCondition.await(
                    5,
                    TimeUnit.SECONDS
                )

                val currentModificationTime = collection.getLastModificationTime()

                if (lastModificationTime != currentModificationTime) {
                    lastModificationTime = currentModificationTime
                    reload(true)
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

    private val table = ui.lib.Table(tableModel, this)

    private fun getColumnIndex(table: JTable, header: String): Int {
        for (i in 0 until table.columnCount) {
            if (table.getColumnName(i) == header) return i
        }
        return -1
    }

    fun requestReload() {
        databaseCommunicationLock.withLock {
            databaseCommunicationLockCondition.signal()
        }
    }

    private fun reload(fullReload: Boolean) {
        tableViewScope.launch {
            runCatching {
                if (fullReload) {
                    organizationStorage.clearCache()
                }

                tableModel.setDataVector(organizationStorage.getFilteredOrganizationAsArrayOfStrings(), columnNames)
                val sportColumn = table.columnModel.getColumn(8)
                sportColumn.cellEditor = DefaultCellEditor(tablePanel.organizationPanel.typeEditor)
                tableModel.fireTableDataChanged()
                visualPanel.repaint()
            }.onFailure { reload(fullReload) }
        }
    }

    internal fun addFilter(columnName: String) {
        tableFilter?.let {
            val (prevColumnName, prevFilterId) = it
            tableFilter = columnName to (if (prevColumnName == columnName) prevFilterId + 1 else 1)
        } ?: run {
            tableFilter = columnName to 1
        }

        reload(false)
    }

    fun filterChanged() {
        stringFilter = tablePanel.filter
        reload(false)
    }

    fun selectOrganization(row: Int) {
        runCatching {
            tablePanel.organizationPanel.loadOrganization(
                organizationStorage.getFilteredOrganizationAsArrayOfStrings()[row]
            )
        }
    }

    init {
        title = "Table"
        defaultCloseOperation = EXIT_ON_CLOSE
        setSize(1200, 600)
        setLayout(layout)

        table.tableHeader.table.rowHeight = 30
        table.tableHeader.reorderingAllowed = false
        layout.addAsFontOnlyComponent(table)
        layout.addAsFontOnlyComponent(table.tableHeader)
        layout.addAsFontOnlyComponent(
            (table.getDefaultEditor(Any::class.java) as DefaultCellEditor).component
        )
        add(JScrollPane(table))

        val panel = JPanel()
        panel.layout = MigLayout(
            "",
            "[fill,grow]",
            "[fill,grow]"
        )
        panel.add(tablePanel, "wrap")
        panel.add(visualPanel)
        add(panel)

        tablePanel.localize()
        modificationObserver.start()
        setSize(1600, 900)
    }
}