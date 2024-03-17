package lib

import exceptions.NoBundleLoadedException
import java.util.*

/**
 * Stores bundle with localized strings
 */
object Localization {
    private var bundle: ResourceBundle? = null

    /**
     * @param key for string
     * @return gets string from bundle using key
     */
    fun get(key: String): String {
        if (bundle == null) {
            throw NoBundleLoadedException("No bundle loaded")
        }

        return bundle!!.getString(key)
    }

    fun askUserForALanguage(
        bufferedReaderWithQueueOfStreams: BufferedReaderWithQueueOfStreams
    ) {
        println(
            """
                Choose language:
                0 en (default)
                1 ru
                """.trimIndent()
        )

        val line = bufferedReaderWithQueueOfStreams.readLine()

        when (line) {
            "", "0", "en" -> loadBundle("localization/localization", "en")
            "1", "ru" -> loadBundle("localization/localization", "ru")
            else -> {
                println("Invalid input. Try again.")
                askUserForALanguage(bufferedReaderWithQueueOfStreams)
            }
        }
    }

    fun loadBundle(filename: String, locale: String) {
        bundle = ResourceBundle.getBundle(filename, Locale.of(locale))
    }
}
