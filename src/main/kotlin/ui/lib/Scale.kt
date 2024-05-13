package ui.lib

import java.awt.Dimension
import java.awt.Toolkit

const val DEFAULT_SCREEN_WIDTH = 1920

fun calculateFontSize(defaultFontSize: Int): Int {
    val screenSize: Dimension = Toolkit.getDefaultToolkit().screenSize
    return defaultFontSize * screenSize.width / DEFAULT_SCREEN_WIDTH
}