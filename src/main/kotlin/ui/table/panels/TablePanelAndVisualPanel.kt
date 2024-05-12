package ui.table.panels

import ui.Visualization
import ui.lib.MigFontLayout
import ui.lib.calculateFontSize
import javax.swing.JPanel

class TablePanelAndVisualPanel(tablePanel: TablePanel, visualPanel: Visualization) : JPanel() {
    private val layout = MigFontLayout {
        fontSize = calculateFontSize(15)
    }

    init {
        setLayout(layout)

        add(tablePanel, "wrap")
        add(visualPanel)
    }
}