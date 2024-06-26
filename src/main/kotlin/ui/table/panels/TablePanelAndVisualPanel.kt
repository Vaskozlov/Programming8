package ui.table.panels

import ui.Visualization
import ui.lib.MigFontLayout
import javax.swing.JPanel

class TablePanelAndVisualPanel(tablePanel: TablePanel, visualPanel: Visualization) : JPanel() {
    private val layout = MigFontLayout("insets 0")

    init {
        setLayout(layout)

        add(tablePanel, "wrap")
        add(visualPanel)
    }
}