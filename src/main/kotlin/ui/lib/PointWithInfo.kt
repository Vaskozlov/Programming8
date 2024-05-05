package ui.lib

import java.awt.Point

class PointWithInfo(x: Int, y: Int, val text: String, val additionalInfo: Any? = null) : Point(x, y)
