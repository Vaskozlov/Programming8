package ui.table

import collection.CollectionInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.swing.Swing
import lib.Localization
import net.miginfocom.swing.MigLayout
import ui.lib.MigFontLayout
import ui.lib.calculateFontSize
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock
import javax.swing.DefaultCellEditor
import javax.swing.JPanel
import javax.swing.JScrollPane
import kotlin.concurrent.withLock


class TablePage(collection: CollectionInterface, userLogin: String) :
    TablePageWithVisualization(collection) {
    private val databaseCommunicationLock = ReentrantLock()
    private val databaseCommunicationLockCondition = databaseCommunicationLock.newCondition()

    private val layout = MigFontLayout(
        "",
        "[fill,grow,65%][fill,grow,35%]",
        "[fill,grow]"
    )

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

    override fun requestReload() {
        databaseCommunicationLock.withLock {
            databaseCommunicationLockCondition.signal()
        }
    }

    override fun reload(requestFullReload: Boolean) = tableViewScope.launch {
        while (true) {
            try {
                tryToReload(requestFullReload)
                break
            } catch (e: Throwable) {
                delay(100)
            }
        }
    }

    private fun tryToReload(requestFullReload: Boolean) {
        if (requestFullReload) {
            organizationStorage.clearCache()
        }

        val selectedRows = table.selectedRows

        tableModel.setDataVector(organizationStorage.getFilteredOrganizationAsArrayOfStrings(), columnNames)
        val sportColumn = table.columnModel.getColumn(8)
        sportColumn.cellEditor = DefaultCellEditor(tablePanel.organizationPanel.typeEditor)
        tableModel.fireTableDataChanged()
        repaintVisualPanel()

        tableViewScope.launch(Dispatchers.Swing) {
            table.clearSelection()
            selectedRows.forEach { table.addRowSelectionInterval(it, it) }
        }
    }

    init {
        layout.fontSize = calculateFontSize(15)
        setLayout(layout)

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