package ui.lib

import java.awt.event.*
import javax.swing.AbstractAction
import javax.swing.JButton

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