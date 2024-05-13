package ui.lib

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.swing.JComboBox

class LanguageCombobox(scope: CoroutineScope) : JComboBox<String>() {
    private val languages = mapOf(
        "English" to "en",
        "Русский" to "ru",
        "Salvador español" to "es_SV",
        "Deutschland" to "de"
    )

    init {
        languages.keys.forEach { addItem(it) }

        selectedItem = languages.filter { it.value == GuiLocalization.localeName }.keys.first()

        addItemListener {
            val language = languages[it.item.toString()]!!

            scope.launch {
                GuiLocalization.setLanguage(language)
            }
        }
    }
}