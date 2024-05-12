package lib

object CliLocalization : LocalizedClass {
    private const val LOCALE_FILENAME = "localization/cli"
    private var localization = Localization(LOCALE_FILENAME, "en")

    fun get(key: String): String {
        return localization.get(key)
    }

    override fun format(number: Number?) = localization.format(number)
    override fun format(date: java.time.LocalDate?) = localization.format(date)
    override fun parse(text: String?) = localization.parse(text)

    fun setLanguage(language: String) {
        localization = Localization(LOCALE_FILENAME, language)
    }
}