package ui.table

import collection.CollectionInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.swing.Swing
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import ui.lib.PointWithInfo
import ui.lib.Table
import kotlin.math.max
import kotlin.math.min

abstract class TablePageWithVisualization(collection: CollectionInterface) :
    TablePageWithOrganizationPanels(collection) {
    private val visualLock = Semaphore(1)

    private fun getCurrentPoints() =
        organizationStorage.getFilteredOrganizationAsArrayOfStrings()
            .map {
                PointWithInfo(
                    it[Table.ORGANIZATION_COORDINATE_X_COLUMN]?.toIntOrNull() ?: 0,
                    it[Table.ORGANIZATION_COORDINATE_Y_COLUMN]?.toIntOrNull() ?: 0,
                    it[Table.ORGANIZATION_ID_COLUMN] as String,
                    it
                )
            }.toList()

    protected fun repaintVisualPanel() = tableViewScope.launch {
        visualLock.withPermit {
            val oldPoints = visualPanel.getPoints()
            val currentPoints = getCurrentPoints()
            val addedPoints = currentPoints.filterNot { oldPoints.contains(it) }
            val removedPoints = oldPoints.filterNot { currentPoints.contains(it) }
            val visualEffectDelay = min(1000L / (max(1, addedPoints.size + removedPoints.size)), 200L)

            for (point in removedPoints) {
                visualPanel.pointsV.remove(point)

                withContext(Dispatchers.Swing) {
                    visualPanel.repaint()
                }

                delay(visualEffectDelay)
            }

            for (point in addedPoints) {
                visualPanel.pointsV.add(point)

                withContext(Dispatchers.Swing) {
                    visualPanel.repaint()
                }

                delay(visualEffectDelay)
            }

            visualPanel.pointsV = currentPoints.toMutableList()
            visualPanel.repaint()
        }
    }
}