package lib

import lib.CSV.CSVStreamWriter

/**
 * If object is null writes null to stream, otherwise calls the function
 *
 * @param function, which will be called in case object is not null
 */
fun <T> writeNullableToStream(
    stream: CSVStreamWriter,
    value: T?,
    timesIfNull: Int,
    function: (T) -> Unit
) {
    if (value == null) {
        for (i in timesIfNull downTo 1) {
            stream.append("null")
        }
    } else {
        function.invoke(value)
    }
}

fun interface WritableToCSVStream {
    fun writeToStream(stream: CSVStreamWriter)
}
