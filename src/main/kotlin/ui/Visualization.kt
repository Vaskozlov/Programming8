package ui

import java.awt.Canvas
import java.awt.Color
import java.awt.Graphics
import java.awt.event.ActionEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.AbstractAction
import javax.swing.JFrame


class Visualization : Canvas() {

    init {
        addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                println(getComponentAt(e.point))
            }
        })
    }

    override fun paint(g: Graphics) {
        g.color = Color.RED
        g.fillOval(50, 50, 100, 100)
    }

    companion object {
        private val buttonAction = object : AbstractAction() {
            override fun actionPerformed(e: ActionEvent) {
                println("Launching rockets")
            }
        }

        @JvmStatic
        fun main(args: Array<String>) {
            val m: Visualization = Visualization()
            val f = JFrame()
            f.add(m)
            f.setSize(400, 400)


            //f.setLayout(null);
            f.isVisible = true
        }
    }
}