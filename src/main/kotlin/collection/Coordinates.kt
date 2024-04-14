package collection

import kotlinx.serialization.Serializable
import lib.CSV.CSVStreamWriter
import lib.WritableToCSVStream

@Serializable
data class Coordinates(val x: Long?, val y: Long?) : WritableToCSVStream {
    override fun writeToStream(stream: CSVStreamWriter) {
        stream.append(x.toString())
        stream.append(y.toString())
    }
}