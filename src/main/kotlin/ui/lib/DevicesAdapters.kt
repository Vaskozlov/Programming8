package ui.lib

import java.awt.event.*
import javax.swing.AbstractAction
import javax.swing.JButton
import javax.swing.JTextField

fun keyboardKeyReleasedAdapter(action: (e: KeyEvent) -> Unit): KeyAdapter =
    object : KeyAdapter() {
        override fun keyReleased(e: KeyEvent) {
            runCatching {
                action(e)
            }
        }
    }

fun mouseClickAdapter(action: (e: MouseEvent) -> Unit): MouseAdapter =
    object : MouseAdapter() {
        override fun mouseClicked(e: MouseEvent) {
            runCatching {
                action(e)
            }
        }
    }

fun buttonClickAdapter(action: (e: ActionEvent) -> Unit): JButton =
    JButton(object : AbstractAction() {
        override fun actionPerformed(e: ActionEvent) {
            action(e)
        }
    })

fun getTextFieldWithKeyListener(size: Int, keyCode: Int?, action: (JTextField) -> Unit) =
    object : JTextField(size) {
        private val targetKeyCode = keyCode

        private fun forceGetThis(): JTextField {
            return this
        }

        init {
            addKeyListener(
                keyboardKeyReleasedAdapter {
                    if (targetKeyCode == null || it.keyCode == targetKeyCode) {
                        action(this)
                    }
                }
            )

            addFocusListener(object : FocusListener {
                override fun focusGained(e: FocusEvent?) {
                    // empty
                }

                override fun focusLost(e: FocusEvent?) {
                    action(forceGetThis())
                }
            })
        }
    }
