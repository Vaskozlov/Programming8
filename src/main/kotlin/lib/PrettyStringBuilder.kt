package lib

import exceptions.BadIdentException
import java.util.concurrent.atomic.AtomicInteger

/**
 * Helps to build pretty string with different idents in thread safe way
 */
class PrettyStringBuilder(identSize: Int = 2) {
    private val stringBuilder = StringBuffer()
    private val ident = AtomicInteger(0)
    private val identSize = AtomicInteger(identSize)
    private var initialized = false

    override fun toString(): String {
        return stringBuilder.toString()
    }

    fun increaseIdent() {
        ident.incrementAndGet()
    }

    /**
     * decreases ident, if it is less than zero throws BadIdentException exception
     */
    fun decreaseIdent() {
        ident.decrementAndGet()

        if (ident.get() < 0) {
            throw BadIdentException("Ident must not be below zero")
        }
    }

    fun appendLine(line: String?) {
        addNewLine()
        stringBuilder.append(" ".repeat(ident.get() * identSize.get())).append(line)
    }

    fun appendLine(format: String?, vararg objects: Any?) {
        addNewLine()
        stringBuilder.append(" ".repeat(ident.get() * identSize.get())).append(String.format(format!!, *objects))
    }

    private fun addNewLine() {
        if (initialized) {
            stringBuilder.append('\n')
        } else {
            initialized = true
        }
    }
}
