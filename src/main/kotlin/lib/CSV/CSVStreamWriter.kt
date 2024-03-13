package lib.CSV

import java.io.*

/**
 * Sequence of elements with an ability to read first element and append to the end
 */
class CSVStreamWriter(var writer: Writer) {
    var newLineStarted: Boolean = true

    fun append(sequence: CharSequence?) {
        if (!newLineStarted) {
            writer.write(';'.code)
        }

        newLineStarted = false
        writer.append(sequence)
    }

    fun append(number: Number?) {
        append(number.toString())
    }

    fun newLine() {
        newLineStarted = true
        writer.write('\n'.code)
    }
}
