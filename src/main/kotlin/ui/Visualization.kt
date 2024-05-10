package ui

import ui.lib.PointWithInfo
import ui.lib.Table
import ui.lib.keyboardKeyReleasedAdapter
import java.awt.Color


class Visualization(private val tablePage: TablePage) : BasicPointsVisualizer() {
    companion object {
        val availableColors = listOf(
            Color.RED,
            Color.GREEN,
            Color.BLUE,
            Color.YELLOW,
            Color.ORANGE,
            Color.CYAN,
            Color.MAGENTA,
            Color.PINK,
            Color.DARK_GRAY
        )
    }

    var pointsV: MutableList<PointWithInfo> = mutableListOf()
    private var selectedPoint: PointWithInfo? = null

    override fun getPoints(): List<PointWithInfo> = pointsV

    override fun pointColor(point: PointWithInfo): Color {
        @Suppress("UNCHECKED_CAST")
        val castedInfo = point.additionalInfo as Array<String?>
        val creatorId = castedInfo[Table.ORGANIZATION_CREATOR_ID_COLUMN]?.toInt() ?: -1
        val id = (creatorId - tablePage.getUserId()!!).run { if (this < 0) this + availableColors.size else this }
        var color = availableColors[id % availableColors.size]

        for (i in 0 until id / availableColors.size) {
            color = color.darker()
        }

        return color
    }

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
                if (it.keyCode == 127 || (it.keyCode == 8 && it.isShiftDown)) {
                    @Suppress("UNCHECKED_CAST")
                    val castedInfo = selectedPoint?.additionalInfo as Array<String?>
                    tablePage.removeById(castedInfo[Table.ORGANIZATION_ID_COLUMN]?.toInt() ?: -1)
                }
            }
        )
    }
}