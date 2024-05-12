package ui.table

import collection.CollectionInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.swing.Swing
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import ui.lib.GuiLocalization
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
                    GuiLocalization.toInt(it[Table.COORDINATE_X_COLUMN]) ?: 0,
                    GuiLocalization.toInt(it[Table.COORDINATE_Y_COLUMN]) ?: 0,
                    it[Table.ID_COLUMN] as String,
                    it
                )
            }.toList()

    private suspend fun deletePointsFromVisualPanel(removedPoints: List<PointWithInfo>, visualEffectDelay: Long) {
        for (point in removedPoints) {
            visualPanel.pointsV.remove(point)

            withContext(Dispatchers.Swing) {
                visualPanel.repaint()
            }

            delay(visualEffectDelay)
        }
    }

    private suspend fun addPointsToVisualPanel(addedPoints: List<PointWithInfo>, visualEffectDelay: Long) {
        for (point in addedPoints) {
            visualPanel.pointsV.add(point)

            withContext(Dispatchers.Swing) {
                visualPanel.repaint()
            }

            delay(visualEffectDelay)
        }
    }

    protected fun repaintVisualPanel() = tableViewScope.launch {
        visualLock.withPermit {
            val oldPoints = visualPanel.getPoints()
            val currentPoints = getCurrentPoints()
            val addedPoints = currentPoints.filterNot { oldPoints.contains(it) }
            val removedPoints = oldPoints.filterNot { currentPoints.contains(it) }
            val visualEffectDelay = min(1000L / (max(1, addedPoints.size + removedPoints.size)), 200L)

            deletePointsFromVisualPanel(removedPoints, visualEffectDelay)
            addPointsToVisualPanel(addedPoints, visualEffectDelay)

            visualPanel.pointsV = currentPoints.toMutableList()
            visualPanel.repaint()
        }
    }
}