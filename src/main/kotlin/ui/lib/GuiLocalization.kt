package ui.lib

import collection.OrganizationType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.swing.Swing
import kotlinx.coroutines.withContext
import lib.CliLocalization
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
    private val actionsAfterLanguageChange = mutableListOf<suspend () -> Unit>()
    private var numberFormat = NumberFormat.getInstance(localization.locale)
    var localeName = "en"
        private set

    fun format(type: OrganizationType?): String {
        if (type == null) {
            return get("ui.type.null")
        }

        return when (type) {
            OrganizationType.COMMERCIAL -> get("ui.type.commercial")
            OrganizationType.PUBLIC -> get("ui.type.public")
            OrganizationType.PRIVATE_LIMITED_COMPANY -> get("ui.type.private_limited_company")
            OrganizationType.OPEN_JOINT_STOCK_COMPANY -> get("ui.type.open_joint_stock_company")
        }
    }

    override fun format(number: Number?) = localization.format(number)
    override fun format(date: java.time.LocalDate?) = localization.format(date)

    fun parseOrganizationType(text: String?): OrganizationType? {
        if (text == null) {
            return null
        }

        return when (text) {
            get("ui.type.commercial") -> OrganizationType.COMMERCIAL
            get("ui.type.public") -> OrganizationType.PUBLIC
            get("ui.type.private_limited_company") -> OrganizationType.PRIVATE_LIMITED_COMPANY
            get("ui.type.open_joint_stock_company") -> OrganizationType.OPEN_JOINT_STOCK_COMPANY
            else -> null
        }
    }

    override fun parse(text: String?) = localization.parse(text)

    fun addElement(key: String, component: Component) {
        uiElements[key] = component
    }

    fun addActionBefore(action: suspend () -> Unit) {
        actionsOnLanguageChange.add(action)
    }

    fun addActionAfter(action: suspend () -> Unit) {
        actionsAfterLanguageChange.add(action)
    }

    fun clearElements() = uiElements.clear()

    fun clearActions() = actionsOnLanguageChange.clear()

    fun get(key: String): String {
        return localization.get(key)
    }

    suspend fun setLanguage(language: String) {
        localeName = language
        CliLocalization.setLanguage(language)
        localization = Localization(LOCALE_FILENAME, language)
        numberFormat = NumberFormat.getInstance(localization.locale)
        updateUiElements()
    }

    suspend fun updateUiElements() {
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

            actionsAfterLanguageChange.forEach { it() }
        }
    }
}