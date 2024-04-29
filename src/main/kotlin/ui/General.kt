package ui

import java.util.*
import javax.swing.UIManager
import javax.swing.plaf.FontUIResource

fun setUIFont(f: FontUIResource) {
    val keys: Enumeration<*> = UIManager.getDefaults().keys()
    while (keys.hasMoreElements()) {
        val key = keys.nextElement()
        val value = UIManager.get(key)
        if (value is FontUIResource) UIManager.put(key, f)
    }
}
