package lib

import java.io.BufferedInputStream
import java.io.FileInputStream
import java.io.IOException
import java.nio.charset.StandardCharsets

object IOHelper {
    /**
     * @return null if unable to read file, otherwise file content
     */
    fun readFile(filename: String): String? {
        try {
            FileInputStream(filename).use { file ->
                BufferedInputStream(file).use { stream ->
                    return String(stream.readAllBytes(), StandardCharsets.UTF_8)
                }
            }
        } catch (_: IOException) {
            return null
        }
    }
}
