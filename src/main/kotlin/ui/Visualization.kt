package ui

import ui.lib.PointWithInfo
import ui.lib.keyboardKeyReleasedAdapter
import java.awt.Color
import java.awt.Point


class Visualization(private val tablePage: TablePage) : BasicPointsVisualizer() {
    var pointsV : MutableList<PointWithInfo> = mutableListOf()
    private var selectedPoint : PointWithInfo? = null

    override fun getPoints(): List<PointWithInfo> = pointsV

    override fun onClick(closedPoint: PointWithInfo) {
        selectedPoint = closedPoint

        @Suppress("UNCHECKED_CAST")
        tablePage.tablePanel.organizationPanel.loadOrganization(closedPoint.additionalInfo as Array<String?>)
    }

    override fun setSize(width: Int, height: Int) {
        super.setSize(width, height)
        println("resizeing $width $height")
    }

    init {
        background = Color.GRAY

        addKeyListener(
            keyboardKeyReleasedAdapter {
                println(it)
                if (it.keyCode == 127 || (it.keyCode == 8 && it.isShiftDown)) {
                    @Suppress("UNCHECKED_CAST")
                    val castedInfo = selectedPoint?.additionalInfo as Array<String?>
                    tablePage.removeById(castedInfo[0]?.toInt() ?: -1)
                }
            }
        )
    }
}