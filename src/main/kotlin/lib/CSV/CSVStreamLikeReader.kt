package lib.CSV

import java.io.IOException

/**
 * Sequence of elements with an ability to read first element and append to the end
 */
class CSVStreamLikeReader(data: String) {
    private val data: Array<List<String>> = data.split("\n").map { it.split(";") }.toTypedArray();
    private var lineIndex = 0
    private var column = 0

    private data class ReadResult(val elem: String, val lineIndex: Int, val column: Int)

    val isEndOfLine: Boolean
        get() = column >= data[lineIndex].size

    val isEndOfStream: Boolean
        get() = lineIndex + (if (isEndOfLine) 1 else 0) >= data.size

    val elementLeftInLine: Int
        get() = data[lineIndex].size - column

    fun nextLine() {
        ++lineIndex
        column = 0
    }

    fun readElem(): String {
        val result = readNextElement(lineIndex, column)

        lineIndex = result.lineIndex
        column = result.column

        return result.elem
    }

    fun readNullableElem(): String? {
        val elem = readElem()

        if (elem == "null") {
            return null
        }

        return elem
    }

    val next: String
        get() = readNextElement(lineIndex, column).elem

    private fun readNextElement(lineIndex: Int, column: Int): ReadResult {
        var lineIndex = lineIndex
        if (data.size <= lineIndex) {
            throw IOException("No more elements")
        }

        if (column >= data[lineIndex].size) {
            return readNextElement(++lineIndex, 0)
        }

        return ReadResult(data[lineIndex][column], lineIndex, column + 1)
    }
}
