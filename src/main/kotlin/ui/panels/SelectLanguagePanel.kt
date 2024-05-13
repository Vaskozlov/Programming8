package ui.panels

import kotlinx.coroutines.CoroutineScope
import ui.lib.GuiLocalization
import ui.lib.LanguageCombobox
import ui.lib.MigFontLayout
import ui.lib.calculateFontSize
import javax.swing.JLabel
import javax.swing.JPanel

class SelectLanguagePanel(size: Int, scope: CoroutineScope) : JPanel() {
    private val chooseLanguageLabel = JLabel()
    private val languageCombobox = LanguageCombobox(scope)

    private val layout = MigFontLayout("insets 0") {
        fontSize = calculateFontSize(size)
    }

    init {
        setLayout(layout)
        add(chooseLanguageLabel)
        add(languageCombobox)

        GuiLocalization.addElement("ui.choose_language", chooseLanguageLabel)
    }
}