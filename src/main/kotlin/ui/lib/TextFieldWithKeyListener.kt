package ui.lib

import javax.swing.JTextField

fun getTextFieldWithKeyListener(size: Int, action: (JTextField) -> Unit) = object : JTextField(size) {
    init {
        addKeyListener(
            keyboardKeyReleasedAdapter {
                action(this)
            }
        )
    }
}