package ui.lib

import net.miginfocom.swing.MigLayout
import java.awt.Component
import java.awt.Font

class MigFontLayout(
    layoutConstraints: String = "",
    colConstraints: String = "",
    rowConstraints: String = ""
) : MigLayout(layoutConstraints, colConstraints, rowConstraints) {
    private val componentsList = mutableListOf<Component>()
    var fontName = "Arial"
    var fontSize: Int = 24
        set(value) {
            for (comp in componentsList) {
                setFontForComponent(comp)
            }

            field = value
        }

    private fun setFontForComponent(comp: Component) {
        comp.font = Font(fontName, 0, fontSize)
    }

    override fun addLayoutComponent(comp: Component?, constraints: Any?) {
        super.addLayoutComponent(comp, constraints)
        setFontForComponent(comp!!)
        componentsList.add(comp)
    }

    fun addAsFontOnlyComponent(comp: Component)
    {
        componentsList.add(comp)
        setFontForComponent(comp)
    }
}