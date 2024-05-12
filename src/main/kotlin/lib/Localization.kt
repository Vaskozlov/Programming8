package lib

import java.text.NumberFormat
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

/**
 * Stores bundle with localized strings
 */
class Localization(filename: String, val locale: Locale) {
    private val bundle: ResourceBundle = ResourceBundle.getBundle(filename, locale)
    private val numberFormat = NumberFormat.getInstance(locale)

    constructor(filename: String, localeName: String) : this(filename, Locale.of(localeName))
    constructor(filename: String) : this(filename, "en")

    fun get(key: String): String {
        return bundle.getString(key)
    }

    fun format(number: Number?): String =
        if (number == null) "null" else numberFormat.format(number)

    fun format(date: java.time.LocalDate?): String {
        if (date == null) {
            return "null"
        }

        return DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).withLocale(locale).format(date)
    }

    fun parse(text: String?): Number? {
        if (text == null) {
            return null
        }

        return numberFormat.runCatching { parse(text) }.getOrNull()
    }
}

interface LocalizedClass {
    fun format(number: Number?): String
    fun format(date: java.time.LocalDate?): String
    fun parse(text: String?): Number?

    fun toInt(input: String?) = parse(input)?.toInt()
    fun toLong(input: String?) = parse(input)?.toLong()

    fun toFloat(input: String?) = parse(input)?.toFloat()
    fun toDouble(input: String?) = parse(input)?.toDouble()
}
