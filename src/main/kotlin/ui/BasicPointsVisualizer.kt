package ui

import ui.lib.PointWithInfo
import ui.lib.mouseClickAdapter
import java.awt.*
import java.awt.event.MouseEvent

abstract class BasicPointsVisualizer : Canvas() {
    var radius: Int = 50
    private val x = Point()
    private val y = Point()
    private var xScale = 1.0
    private var yScale = 1.0
    private val pointsCenters = mutableListOf<PointWithInfo>()

    abstract fun getPoints(): List<PointWithInfo>

    abstract fun onClick(closedPoint: PointWithInfo)

    override fun setFont(f: Font?) {
        super.setFont(f)
        radius = f!!.size * 2
    }

    override fun paint(g: Graphics) {
        val points = getPoints()
        pointsCenters.clear()
        x.x = (points.minByOrNull { it.x }?.x ?: 0) - radius * 2
        x.y = (points.maxByOrNull { it.x }?.x ?: 100) + radius * 2
        y.x = (points.minByOrNull { it.y }?.y ?: 0) - radius * 2
        y.y = (points.maxByOrNull { it.y }?.y ?: 100) + radius * 2
        xScale = width.toDouble() / (x.y - x.x).toDouble()
        yScale = height.toDouble() / (y.y - y.x).toDouble()

        points.forEach { drawPoint(g, it) }
    }

    private fun drawPoint(g: Graphics, point: PointWithInfo) {
        g.color = Color.RED
        val x = (point.x - x.x) * xScale
        val y = (point.y - y.x) * yScale
        val virtualPoint = PointWithInfo(
            x.toInt(),
            y.toInt(),
            point.text,
            point.additionalInfo
        )
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