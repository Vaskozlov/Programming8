package ui.widgets

import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent

fun keyboardKeyReleasedAdapter(action: (e: KeyEvent) -> Unit): KeyAdapter {
    return object : KeyAdapter() {
        override fun keyReleased(e: KeyEvent) {
            runCatching {
                action(e)
            }
        }
    }
}

fun mouseClickAdapter(action: (e: MouseEvent) -> Unit): MouseAdapter {
    return object : MouseAdapter() {
        override fun mouseClicked(e: MouseEvent) {
            runCatching {
                action(e)
            }
        }
    }
}