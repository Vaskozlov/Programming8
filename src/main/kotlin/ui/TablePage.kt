package ui

import collection.CollectionInterface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import lib.Localization
import net.miginfocom.swing.MigLayout
import ui.lib.BasicTablePage
import ui.lib.MigFontLayout
import ui.lib.calculateFontSize
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock
import javax.swing.DefaultCellEditor
import javax.swing.JPanel
import javax.swing.JScrollPane
import kotlin.concurrent.withLock
import kotlin.math.max
import kotlin.math.min


class TablePage(collection: CollectionInterface, userLogin: String) : BasicTablePage(collection) {
    val tableViewScope = CoroutineScope(Dispatchers.Default)
    private val databaseCommunicationLock = ReentrantLock()
    private val databaseCommunicationLockCondition = databaseCommunicationLock.newCondition()
    private val visualLock = Semaphore(1)

    private val layout = MigFontLayout(
        "",
        "[fill,grow,65%][fill,grow,35%]",
        "[fill,grow]"
    )
    internal val tablePanel = TablePanel(this)
    private val visualPanel = Visualization(this)

    private val modificationObserver = tableViewScope.launch {
        var lastModificationTime = collection.getLastModificationTime()

        while (true) {
            databaseCommunicationLock.withLock {
                val forcedReload = databaseCommunicationLockCondition.await(
                    5,
                    TimeUnit.SECONDS
                )

                val currentModificationTime = collection.getLastModificationTime()

                if (lastModificationTime != currentModificationTime || forcedReload) {
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

    private fun repaintVisualPanel() {
        tableViewScope.launch {
            while (!visualLock.tryAcquire()) {
                delay(100)
            }

            val oldPoints = visualPanel.getPoints()
            val currentPoints = getCurrentPoints()
            val addedPoints = currentPoints.filterNot { oldPoints.contains(it) }
            val removedPoints = oldPoints.filterNot { currentPoints.contains(it) }
            val visualEffectDelay = min(1000L / (max(1, addedPoints.size + removedPoints.size)), 200L)

            for (point in removedPoints) {
                visualPanel.pointsV.remove(point)
                visualPanel.repaint()
                delay(visualEffectDelay)
            }

            for (point in addedPoints) {
                visualPanel.pointsV.add(point)
                visualPanel.repaint()
                delay(visualEffectDelay)
            }

            visualPanel.pointsV = currentPoints.toMutableList()
            visualPanel.repaint()
            visualLock.release()
        }
    }

    override fun requestReload() {
        databaseCommunicationLock.withLock {
            databaseCommunicationLockCondition.signal()
        }
    }

    override fun reload(requestFullReload: Boolean) {
        tableViewScope.launch {
            runCatching {
                if (requestFullReload) {
                    organizationStorage.clearCache()
                }

                tableModel.setDataVector(organizationStorage.getFilteredOrganizationAsArrayOfStrings(), columnNames)
                val sportColumn = table.columnModel.getColumn(8)
                sportColumn.cellEditor = DefaultCellEditor(tablePanel.organizationPanel.typeEditor)
                tableModel.fireTableDataChanged()
                repaintVisualPanel()
            }.onFailure { reload(requestFullReload) }
        }
    }

    override fun addOrganization() {
        tablePanel.organizationPanel.getOrganization()?.let {
            addOrganization(it)
        }
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

    fun unselectOrganization() {
        table.clearSelection()
        tablePanel.organizationPanel.clearFields()
    }

    init {
        title = "Table"
        defaultCloseOperation = EXIT_ON_CLOSE
        layout.fontSize = calculateFontSize(15)
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
        layout.addAsFontOnlyComponent(tablePanel)
        layout.addAsFontOnlyComponent(visualPanel)

        tablePanel.localize()
        modificationObserver.start()
        setSize(1600, 900)
        reload(true)

        title =
            "${Localization.get("ui.current_user")} $userLogin, ${Localization.get("ui.your_id_is")}: ${getUserId()}"
    }
}