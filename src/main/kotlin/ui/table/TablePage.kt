package ui.table

import collection.CollectionInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.swing.Swing
import ui.lib.GuiLocalization
import ui.lib.MigFontLayout
import ui.table.panels.TablePanelAndVisualPanel
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock
import javax.swing.DefaultCellEditor
import javax.swing.JScrollPane
import kotlin.concurrent.withLock


class TablePage(collection: CollectionInterface, userLogin: String) :
    TablePageWithVisualization(collection) {
    private val databaseCommunicationLock = ReentrantLock()
    private val databaseCommunicationLockCondition = databaseCommunicationLock.newCondition()
    
    private val layout = MigFontLayout(
        "insets 0",
        "[fill,grow,65%][fill,grow,35%]",
        "[fill,grow]"
    ) {
        addAsFontOnlyComponent(table)
        addAsFontOnlyComponent(table.tableHeader)
        addAsFontOnlyComponent(
            (table.getDefaultEditor(
                Any::
                class.java
            ) as DefaultCellEditor).component
        )
        
        addAsFontOnlyComponent(tablePanel)
        addAsFontOnlyComponent(visualPanel)
    }
    
    private val scrollPane = JScrollPane(table)
    private val tablePanelAndVisualPanel = TablePanelAndVisualPanel(tablePanel, visualPanel)
    
    private val modificationObserver = tableViewScope.launch {
        var lastModificationTime = collection.getLastModificationTime()
        
        while (true) {
            runCatching {
                databaseCommunicationLock.withLock {
                    val forcedReload = databaseCommunicationLockCondition.await(
                        2,
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
        repaintVisualPanel()
        
        tableViewScope.launch(Dispatchers.Swing) {
            table.clearSelection()
            selectedRows.forEach { table.addRowSelectionInterval(it, it) }
            tableModel.fireTableStructureChanged()
            table.updateTable()
            table.hideCreatorIdColumn()
        }
    }
    
    init {
        setLayout(layout)
        
        add(scrollPane)
        add(tablePanelAndVisualPanel)
        
        modificationObserver.start()
        setSize(1600, 900)
        reload(true)
        
        GuiLocalization.addActionAfterLanguageUpdate {
            title = "${GuiLocalization.currentLocale.uiCurrentUser()} $userLogin, " +
                    "${GuiLocalization.currentLocale.uiYourIdIs()}: ${GuiLocalization.format(getUserId())}"
        }
        
        tableViewScope.launch {
            delay(100)
            GuiLocalization.updateUiElements()
            
            GuiLocalization.addActionAfterLanguageUpdate { requestReload() }
        }
    }
}