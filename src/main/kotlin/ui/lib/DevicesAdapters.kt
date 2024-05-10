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

fun buttonDoubleClickAdapter(action: (e: MouseEvent) -> Unit): JButton =
    object : JButton() {
        init {
            addMouseListener(
                mouseClickAdapter {
                    action(it)
                }
            )
        }
    }

fun getTextFieldWithKeyListener(keyCode: Int?, action: (JTextField) -> Unit) =
    object : JTextField() {
        private val targetKeyCode = keyCode

        @Volatile
        private var hasChanged = false

        private fun forceGetThis(): JTextField {
            return this
        }

        init {
            addKeyListener(
                keyboardKeyReleasedAdapter {
                    if (targetKeyCode == null || it.keyCode == targetKeyCode) {
                        hasChanged = false
                        action(this)
                    } else {
                        hasChanged = true
                    }
                }
            )

            addFocusListener(object : FocusListener {
                override fun focusGained(e: FocusEvent?) {
                    // empty
                }

                override fun focusLost(e: FocusEvent?) {
                    // make sure that the last key press is processed
                    Thread.yield()

                    if (hasChanged) {
                        action(forceGetThis())
                    }
                }
            })
        }
    }
