package ui.widgets

import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent

fun mouseClickAdapter(action: (e: MouseEvent) -> Unit): MouseAdapter {
    return object : MouseAdapter() {
        override fun mouseClicked(e: MouseEvent) {
            runCatching {
                action(e)
            }
        }
    }
}