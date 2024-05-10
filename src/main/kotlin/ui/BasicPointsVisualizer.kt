package ui

import ui.lib.PointWithInfo
import ui.lib.mouseClickAdapter
import java.awt.*
import java.awt.event.MouseEvent

abstract class BasicPointsVisualizer : Canvas() {
    companion object {
        private const val SCALE_FACTOR_TO_FIT_ORGANIZATION_WITH_LARGE_COORDINATES = 0.95
        private const val DEFAULT_LEFT_BORDER = 0
        private const val DEFAULT_RIGHT_BORDER = 100
        private const val DEFAULT_TOP_BORDER = 0
        private const val DEFAULT_BOTTOM_BORDER = 100
    }

    private var radius: Int = 15 * 2
    private val x = Point()
    private val y = Point()
    private var xScale = 1.0
    private var yScale = 1.0
    private val pointsCenters = mutableListOf<PointWithInfo>()

    abstract fun getPoints(): List<PointWithInfo>

    abstract fun pointColor(point: PointWithInfo): Color

    abstract fun onClick(closedPoint: PointWithInfo)

    override fun setFont(f: Font?) {
        super.setFont(f)
        radius = f!!.size * 2
    }

    override fun paint(g: Graphics) {
        val points = getPoints()
        val diameter = radius * 2
        pointsCenters.clear()

        x.x = points.minByOrNull { it.x }?.x ?: DEFAULT_LEFT_BORDER
        x.y = points.maxByOrNull { it.x }?.x ?: DEFAULT_RIGHT_BORDER
        y.x = points.minByOrNull { it.y }?.y ?: DEFAULT_TOP_BORDER
        y.y = points.maxByOrNull { it.y }?.y ?: DEFAULT_BOTTOM_BORDER

        x.x -= diameter
        x.y += diameter
        y.x -= diameter
        y.y += diameter

        xScale = width.toDouble() / (x.y - x.x).toDouble() * SCALE_FACTOR_TO_FIT_ORGANIZATION_WITH_LARGE_COORDINATES
        yScale = height.toDouble() / (y.y - y.x).toDouble() * SCALE_FACTOR_TO_FIT_ORGANIZATION_WITH_LARGE_COORDINATES

        points.forEach { drawPoint(g, it) }
    }

    private fun drawPoint(g: Graphics, point: PointWithInfo) {
        val x = (point.x - x.x) * xScale
        val y = (point.y - y.x) * yScale
        val virtualPoint = PointWithInfo(
            x.toInt(),
            y.toInt(),
            point.text,
            point.additionalInfo
        )

        g.color = pointColor(point)
        g.fillOval(virtualPoint.x, virtualPoint.y, radius, radius)

        g.color = Color.BLACK
        g.drawChars(
            point.text.toCharArray(),
            0,
            point.text.length,
            virtualPoint.x + radius / 4,
            virtualPoint.y - radius / 5
        )

        pointsCenters.add(virtualPoint)
    }

    private fun onMouseClick(event: MouseEvent) {
        pointsCenters
            .filter { it.distance(event.point) <= radius }
            .minByOrNull { it.distance(event.point) }
            ?.let { onClick(it) }
    }

    init {
        addMouseListener(mouseClickAdapter {
            onMouseClick(it)
        })
    }
}