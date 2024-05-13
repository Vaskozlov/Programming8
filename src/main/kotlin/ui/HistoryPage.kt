package ui

import ui.lib.MigFontLayout
import ui.lib.calculateFontSize
import javax.swing.JFrame
import javax.swing.JTextArea

class HistoryPage(text: String) : JFrame() {
    val layout = MigFontLayout{
        fontSize = calculateFontSize(24)
    }
    private val textArea = JTextArea(text)
    
    init {
        setLayout(layout)
        
        title = "History"
        setSize(800, 600)
        defaultCloseOperation = EXIT_ON_CLOSE
        isVisible = true
        textArea.isEditable = false
        
        add(textArea)
    }
}