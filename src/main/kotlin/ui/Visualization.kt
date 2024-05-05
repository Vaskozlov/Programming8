package ui

import ui.lib.PointWithInfo
import java.awt.Color


class Visualization(private val tablePage: TablePage) : BasicPointsVisualizer() {
    var pointsV : MutableList<PointWithInfo> = mutableListOf()

    override fun getPoints(): List<PointWithInfo> = pointsV
//        tablePage.organizationStorage.getFilteredOrganizationAsArrayOfStrings()
//            .map {
//                PointWithInfo(it[2]?.toIntOrNull() ?: 0, it[3]?.toIntOrNull() ?: 0, it[0] as String, it)
//            }.toList()


    override fun onClick(closedPoint: PointWithInfo) {
        @Suppress("UNCHECKED_CAST")
        tablePage.tablePanel.organizationPanel.loadOrganization(closedPoint.additionalInfo as Array<String?>)
    }

    override fun setSize(width: Int, height: Int) {
        super.setSize(width, height)
        println("resizeing $width $height")
    }

    init {
        background = Color.GRAY
    }
}