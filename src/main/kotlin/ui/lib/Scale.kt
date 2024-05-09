package ui.lib

import java.awt.Dimension
import java.awt.Toolkit

fun calculateFontSize(defaultFontSize: Int): Int {
    val screenSize: Dimension = Toolkit.getDefaultToolkit().screenSize
    return defaultFontSize * screenSize.width / 1920
}