package lib

import exceptions.RecursionReadErrorException
import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException
import java.io.Reader
import java.nio.file.Path
import java.util.*
import kotlin.io.path.Path
import kotlin.io.path.isSameFileAs

/**
 * BufferedReader with an ability to push new stream which will be used instead of a previous one,
 * when stream returns null reader will come back to the previous stream. If BufferReader runs out of streams
 * readLine/popStream will throw IOException.
 */
class BufferedReaderWithQueueOfStreams {
    private val readers = ArrayDeque<Pair<BufferedReader, Path?>>()
    private var currentReader: BufferedReader

    constructor(filename: String) : this(FileReader(filename))

    constructor(input: Reader) {
        readers.addLast(BufferedReader(input) to null)
        currentReader = readers.last.first
    }

    /**
     * Reads line from last stream, throws IOException when there are not any streams left.
     */
    fun readLine(): String {
        val result: String?

        try {
            result = currentReader.readLine()
        } catch (e: IOException) {
            popStream()
            return readLine()
        }

        if (result == null) {
            popStream()
            return readLine()
        }

        return result
    }

    fun pushStream(filename: String) {
        val path = Path(filename)
        val streamWithTheSameFile =
            readers.find { it.second != null && it.second!!.isSameFileAs(path) }

        streamWithTheSameFile?.let {
            throw RecursionReadErrorException()
        }

        pushStream(FileReader(filename), path)
    }

    private fun pushStream(input: Reader, path: Path? = null) {
        readers.addLast(BufferedReader(input) to path)
        currentReader = readers.last.first
    }

    /**
     * Removes last stream, if there are no streams left after popping IOException will be thrown.
     */
    private fun popStream() {
        val removedReader = readers.removeLast()
        removedReader.first.close()

        if (readers.isEmpty()) {
            throw IOException("No available streams left.")
        }

        currentReader = readers.last.first
    }
}
