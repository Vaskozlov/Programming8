package ui.lib

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.swing.Swing
import kotlinx.coroutines.withContext
import lib.Localization
import lib.LocalizedClass
import java.awt.Component
import java.text.NumberFormat
import javax.swing.JButton
import javax.swing.JLabel

object GuiLocalization : LocalizedClass {
    private const val LOCALE_FILENAME = "localization/gui"

    private val localizationSupervisorScope = CoroutineScope(Dispatchers.Default)
    private var localization = Localization(LOCALE_FILENAME)
    private val uiElements = mutableMapOf<String, Component>()
    private val actionsOnLanguageChange = mutableListOf<suspend () -> Unit>()
    private var numberFormat = NumberFormat.getInstance(localization.locale)

    override fun format(number: Number?) = localization.format(number)
    override fun format(date: java.time.LocalDate?) = localization.format(date)
    override fun parse(text: String?) = localization.parse(text)

    fun addElement(key: String, component: Component) {
        uiElements[key] = component
    }

    fun addAction(action: suspend () -> Unit) {
        actionsOnLanguageChange.add(action)
    }

    fun clearElements() = uiElements.clear()

    fun clearActions() = actionsOnLanguageChange.clear()

    fun get(key: String): String {
        return localization.get(key)
    }

    suspend fun setLanguage(language: String) {
        localization = Localization(LOCALE_FILENAME, language)
        numberFormat = NumberFormat.getInstance(localization.locale)
        updateUiElements()
    }

    private suspend fun updateUiElements() {
        localizationSupervisorScope.launch {
            actionsOnLanguageChange.forEach { it() }
        }

        withContext(Dispatchers.Swing) {
            uiElements.forEach { (key, component) ->
                when (component) {
                    is JLabel -> component.text = get(key)
                    is JButton -> component.text = get(key)
                    else -> throw IllegalArgumentException("Unsupported component type")
                }
            }
        }
    }
}